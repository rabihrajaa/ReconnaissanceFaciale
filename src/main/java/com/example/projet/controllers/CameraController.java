package com.example.projet.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CameraController {
    static {
        try {
            // Load the OpenCV library
            System.loadLibrary("opencv_java4100");
            System.out.println("OpenCV loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Error loading OpenCV: " + e.getMessage());
        }
    }

    @FXML
    private Button captureButton;

    @FXML
    private ImageView cameraView;

    @FXML
    private Label messageLabel; // Label to show access status

    @FXML
    private Label timestampLabel; // Label to show date and time of capture

    private VideoCapture capture;
    private boolean cameraActive = false;

    private final String captureFolder = "captures";

    public void initialize() {
        captureButton.setOnAction(event -> {
            if (!cameraActive) {
                startCamera();
                captureButton.setText("Capture");
            } else {
                capturePhotoAndVerify();
                captureButton.setText("Start Camera");
            }
        });

        // Create the captures folder if it doesn't exist
        File folder = new File(captureFolder);
        if (!folder.exists() && folder.mkdir()) {
            System.out.println("Capture folder created.");
        }
    }

    private void startCamera() {
        capture = new VideoCapture();

        if (!capture.open(0)) {
            System.err.println("Error: Unable to access the camera.");
            return;
        }

        cameraActive = true;

        new Thread(() -> {
            Mat frame = new Mat();
            while (cameraActive) {
                if (capture.read(frame) && !frame.empty()) {
                    Image image = mat2Image(frame);
                    Platform.runLater(() -> cameraView.setImage(image));
                } else {
                    System.err.println("Error: Could not capture frame.");
                }
            }
        }).start();
    }

    private void stopCamera() {
        cameraActive = false;
        if (capture != null && capture.isOpened()) {
            capture.release();
            System.out.println("Camera stopped.");
        }
    }

    private void capturePhotoAndVerify() {
        if (capture != null && capture.isOpened()) {
            Mat frame = new Mat();
            if (capture.read(frame) && !frame.empty()) {
                try {
                    // Generate a unique filename
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                    String fileName = captureFolder + "/capture_" + timestamp + ".jpg";

                    // Save the image
                    Imgcodecs.imwrite(fileName, frame);
                    System.out.println("Photo captured and saved as: " + fileName);

                    // Update the timestamp label
                    Platform.runLater(() -> timestampLabel.setText("Last Capture: " + formattedTimestamp));

                    // Call the Python script for face verification
                    String pythonScript = "E:\\demo\\src\\main\\java\\com\\example\\projet\\AI\\verify.py";
                    ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScript, fileName);
                    Process process = processBuilder.start();

                    // Read the output from the Python script
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuilder output = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line);
                    }
                    int exitCode = process.waitFor();

                    // Update the message label with the result
                    Platform.runLater(() -> {
                        if (exitCode == 0) {
                            if (output.toString().contains("Access Authorized")) {
                                messageLabel.setText("Access Authorized");
                                messageLabel.setStyle("-fx-text-fill: green;");
                            } else {
                                messageLabel.setText("Access Not Authorized");
                                messageLabel.setStyle("-fx-text-fill: red;");
                            }
                        } else {
                            messageLabel.setText("Verification Error");
                            messageLabel.setStyle("-fx-text-fill: orange;");
                        }
                    });

                } catch (Exception e) {
                    System.err.println("Error capturing photo: " + e.getMessage());
                }
            } else {
                System.err.println("Error: Unable to capture photo. Frame is empty.");
            }
        } else {
            System.err.println("Error: Camera is not active.");
        }
    }

    private Image mat2Image(Mat mat) {
        try {
            byte[] buffer = new byte[(int) (mat.total() * mat.channels())];
            mat.get(0, 0, buffer);
            return new Image(new ByteArrayInputStream(buffer));
        } catch (Exception e) {
            System.err.println("Error converting Mat to Image: " + e.getMessage());
            return null;
        }
    }
}

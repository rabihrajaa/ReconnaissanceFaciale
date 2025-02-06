package com.example.projet.controllers;

import com.example.projet.models.Resident;
import com.example.projet.utils.DatabaseHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;

public class AddResidentController {

    @FXML
    private TextField residentNameField;

    @FXML
    private TextField residentLastnameField;

    @FXML
    private TextField residentCINField;

    @FXML
    private TextField residentPhoneField;

    @FXML
    private DatePicker residentBirthdatePicker;

    @FXML
    private ComboBox<String> residentStatusChoiceBox;

    @FXML
    private Button signUpButton;

    @FXML
    private ImageView residentPhoto;

    private String photoPath; // To store the selected photo path

    public void initialize() {
        // Initialize the ChoiceBox with "Authorized" and "NotAuthorized"
        residentStatusChoiceBox.getItems().addAll("Authorized", "NotAuthorized");

        // Configure the image to display in a circle
        Circle clip = new Circle(50, 50, 50); // x, y coordinates and radius
        residentPhoto.setClip(clip);
    }

    @FXML
    private void choosePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            photoPath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            residentPhoto.setImage(image);
            showInfoAlert("Photo Selected", "Path: " + photoPath);
        } else {
            showErrorAlert("No Photo Selected", "Photo is required. Please select a photo.");
        }
    }

    @FXML
    private void addResident(ActionEvent event) {
        try {
            // Validate required fields
            if (residentNameField.getText().isEmpty() ||
                    residentLastnameField.getText().isEmpty() ||
                    residentCINField.getText().isEmpty() ||
                    residentPhoneField.getText().isEmpty() ||
                    residentStatusChoiceBox.getValue() == null ||
                    residentBirthdatePicker.getValue() == null) {
                showErrorAlert("Missing Fields", "Please fill in all required fields.");
                return;
            }

            // Validate name (must be a string)
            if (!residentNameField.getText().matches("[a-zA-Z\\s]+")) {
                showErrorAlert("Invalid Name", "Name must only contain alphabetic characters.");
                return;
            }

            // Validate phone number (starts with 06 or 07 and has 10 digits)
            if (!residentPhoneField.getText().matches("^(06|07)\\d{8}$")) {
                showErrorAlert("Invalid Phone Number", "Phone number must start with 06 or 07 and contain exactly 10 digits.");
                return;
            }

            // Validate CNE (must start with two letters followed by 6 digits)
            if (!residentCINField.getText().matches("^[A-Za-z]{2}\\d{6}$")) {
                showErrorAlert("Invalid CNE", "CNE must start with two letters followed by six digits, e.g., SH199605.");
                return;
            }

            // Validate birthdate (age must be > 18 years)
            LocalDate birthdate = residentBirthdatePicker.getValue();
            if (Period.between(birthdate, LocalDate.now()).getYears() < 18) {
                showErrorAlert("Invalid Birthdate", "Age must be greater than 18 years.");
                return;
            }

            // Validate photo selection
            if (photoPath == null || photoPath.isEmpty()) {
                showErrorAlert("Missing Photo", "Photo is required. Please select a photo.");
                return;
            }

            // Check if resident already exists in the database
            if (DatabaseHelper.residentExists(residentCINField.getText())) {
                showErrorAlert("Resident Exists", "A resident with this CNE already exists in the database.");
                return;
            }

            // Save the photo in the project resources folder
            String targetDirectory = "src/main/resources/images/";
            String photoFileName = residentNameField.getText() + photoPath.substring(photoPath.lastIndexOf('.'));
            Path targetPath = Path.of(targetDirectory, photoFileName);
            try {
                Files.createDirectories(Path.of(targetDirectory));
                Files.copy(Path.of(photoPath), targetPath, StandardCopyOption.REPLACE_EXISTING);
                photoPath = targetPath.toString(); // Update photoPath to the new location
            } catch (IOException e) {
                showErrorAlert("Photo Upload Failed", "Failed to upload photo. Please try again.");
                return;
            }

            // Create a new Resident object
            Resident resident = new Resident(
                    residentCINField.getText(),
                    residentNameField.getText(),
                    residentLastnameField.getText(),
                    residentPhoneField.getText(),
                    birthdate,
                    photoPath, // Save the photo path
                    Resident.Status.valueOf(residentStatusChoiceBox.getValue())
            );

            // Add the resident to the database
            boolean success = DatabaseHelper.addResident(resident);

            // Show success or error alert
            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle(success ? "Success" : "Error");
            alert.setHeaderText(success ? "Resident added successfully" : "Error adding resident");
            alert.setContentText(success ?
                    "Resident:\nCIN: " + resident.getCIN() + "\nName: " + resident.getName() + " " + resident.getLastname() :
                    "Please check the entered information or try again.");
            alert.showAndWait();

            if (success) {
                // Clear all fields after successful addition
                clearFields();
            }

        } catch (Exception e) {
            // Handle unexpected exceptions
            showErrorAlert("Unexpected Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        residentNameField.clear();
        residentLastnameField.clear();
        residentCINField.clear();
        residentPhoneField.clear();
        residentBirthdatePicker.setValue(null);
        residentStatusChoiceBox.setValue(null);
        residentPhoto.setImage(null);
        photoPath = null;
    }

    // Method to show an info alert
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to show an error alert
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

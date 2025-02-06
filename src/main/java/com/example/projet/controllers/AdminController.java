package com.example.projet.controllers;

import com.example.projet.models.Admin;
import com.example.projet.utils.DatabaseHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminController {

    @FXML
    private ImageView adminPhoto;

    @FXML
    private Label adminFisrtNameId;

    @FXML
    private Label adminLastNameId;

    @FXML
    private Label adminEmailId;

    @FXML
    private Label adminCNEId;

    @FXML
    private Label adminPhoneNumberId;

    @FXML
    private Label adminBirthDateId;

    private Admin currentAdmin;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void initialize() {
        try {
            // Récupérer l'admin unique depuis la base de données
            currentAdmin = DatabaseHelper.getSingleAdmin();

            if (currentAdmin != null) {
                adminFisrtNameId.setText(currentAdmin.getName());
                adminLastNameId.setText(currentAdmin.getLastname());
                adminEmailId.setText(currentAdmin.getEmail());
                adminCNEId.setText(currentAdmin.getCIN());
                adminPhoneNumberId.setText(currentAdmin.getPhone());

                // Formater et afficher la date de naissance
                if (currentAdmin.getDateBirth() != null) {
                    adminBirthDateId.setText(currentAdmin.getDateBirth().format(DATE_FORMATTER));
                }

                String photoPath = currentAdmin.getPhoto();
                if (photoPath != null && !photoPath.isEmpty()) {
                    File photoFile = new File(photoPath);
                    if (photoFile.exists() && photoFile.isFile()) {
                        adminPhoto.setImage(new Image("file:" + photoPath));
                    } else {
                        loadDefaultPhoto();
                    }
                } else {
                    loadDefaultPhoto();
                }
            } else {
                showAlert("Admin not found", "Error", "No admin found in the database.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Initialization Error", "Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void EditAdmin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/projet/adminEdit.fxml"));
            Stage editStage = new Stage();
            editStage.setScene(new Scene(loader.load()));
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.setTitle("Edit Admin");

            AdminEditController editController = loader.getController();
            editController.setAdminData(currentAdmin);

            editStage.showAndWait();

            refreshAdminData();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Load Error", "Error", "Unable to load the edit page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshAdminData() {
        if (currentAdmin != null) {
            currentAdmin = DatabaseHelper.getSingleAdmin();
            if (currentAdmin != null) {
                adminFisrtNameId.setText(currentAdmin.getName());
                adminLastNameId.setText(currentAdmin.getLastname());
                adminEmailId.setText(currentAdmin.getEmail());
                adminCNEId.setText(currentAdmin.getCIN());
                adminPhoneNumberId.setText(currentAdmin.getPhone());

                if (currentAdmin.getDateBirth() != null) {
                    adminBirthDateId.setText(currentAdmin.getDateBirth().format(DATE_FORMATTER));
                }

                if (currentAdmin.getPhoto() != null) {
                    adminPhoto.setImage(new Image("file:" + currentAdmin.getPhoto()));
                }
            }
        }
    }

    private void loadDefaultPhoto() {
        try {
            adminPhoto.setImage(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
        } catch (NullPointerException e) {
            System.err.println("Default photo resource not found: /images/logo.png");
            adminPhoto.setImage(null);
        }
    }

    private void showAlert(String title, String header, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }


}

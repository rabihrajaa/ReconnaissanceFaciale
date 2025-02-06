package com.example.projet.controllers;

import com.example.projet.utils.DatabaseHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SigneupController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private final String staticPassword = "Rajaa@2002";

    @FXML
    private void handleSignUpAction(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Vérification des champs
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Empty Fields", "Please fill all the fields.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification de l'email et du mot de passe
        try (Connection connection = DatabaseHelper.connect()) {
            String query = "SELECT * FROM admins WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Admin trouvé, vérifier le mot de passe
                if (staticPassword.equals(password)) {
                    // Afficher la page Dashboard
                    showAlert("Success", "Login Successful", "Welcome, " + resultSet.getString("name") + "!", Alert.AlertType.INFORMATION);
                    openDashboard();
                } else {
                    showAlert("Error", "Invalid Password", "The password you entered is incorrect.", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error", "Admin Not Found", "No admin found with the provided email.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Database Error", "An error occurred while accessing the database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/projet/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("Dashboard");
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "UI Error", "An error occurred while loading the dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

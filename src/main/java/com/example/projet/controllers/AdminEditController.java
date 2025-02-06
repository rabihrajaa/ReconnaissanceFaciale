package com.example.projet.controllers;

import com.example.projet.models.Admin;
import com.example.projet.utils.DatabaseHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AdminEditController {

    @FXML
    private TextField Name;

    @FXML
    private TextField Lastname;

    @FXML
    private TextField Email;

    @FXML
    private TextField CIN;

    @FXML
    private TextField Phone;

    @FXML
    private DatePicker Birthdate;

    @FXML
    private ImageView adminPhoto;

    private Admin admin;

    private File selectedPhotoFile;

    public void setAdminData(Admin admin) {
        this.admin = admin;
        if (admin != null) {
            Name.setText(admin.getName());
            Lastname.setText(admin.getLastname());
            Email.setText(admin.getEmail());
            CIN.setText(admin.getCIN());
            Phone.setText(admin.getPhone());

            // Set the birthdate value
            if (admin.getDateBirth() != null) {
                Birthdate.setValue(admin.getDateBirth());
            }

            if (admin.getPhoto() != null && !admin.getPhoto().isEmpty()) {
                adminPhoto.setImage(new Image("file:" + admin.getPhoto()));
            }
        }
    }

    @FXML
    private void saveChanges() {
        try {
            // Vérification si l'objet Admin est null
            if (admin == null) {
               // showErrorAlert("Admin Null", "L'objet admin est null");
                admin = new Admin(); // Instanciation si nécessaire
            }

            // Validation du nom (doit contenir uniquement des lettres)
            if (!Name.getText().matches("[a-zA-Z\\s]+")) {
                showErrorAlert("Nom Invalide", "Le nom doit contenir uniquement des caractères alphabétiques.");
                return;
            }

            // Validation du prénom (doit contenir uniquement des lettres)
            if (!Lastname.getText().matches("[a-zA-Z\\s]+")) {
                showErrorAlert("Prénom Invalide", "Le prénom doit contenir uniquement des caractères alphabétiques.");
                return;
            }

            // Validation de l'email (doit être au bon format)
            if (!Email.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                showErrorAlert("Email Invalide", "Veuillez entrer une adresse email valide.");
                return;
            }

            // Validation du téléphone (doit commencer par 06 ou 07 et avoir 10 chiffres)
            if (!Phone.getText().matches("^(06|07)\\d{8}$")) {
                showErrorAlert("Téléphone Invalide", "Le numéro de téléphone doit commencer par 06 ou 07 et contenir exactement 10 chiffres.");
                return;
            }

            // Validation de la photo (une photo doit être sélectionnée)
            if (selectedPhotoFile == null || !selectedPhotoFile.exists()) {
                showErrorAlert("Photo Manquante", "Veuillez sélectionner une photo pour l'administrateur.");
                return;
            }

            // Validate birthdate (age must be > 18 years)
            LocalDate birthdate = Birthdate.getValue();
            if (Period.between(birthdate, LocalDate.now()).getYears() < 18) {
                showErrorAlert("Invalid Birthdate", "Age must be greater than 18 years.");
                return;
            }

            // Affectation des valeurs aux propriétés de l'objet admin
            admin.setName(Name.getText());
            admin.setLastname(Lastname.getText());
            admin.setEmail(Email.getText());
            admin.setCIN(CIN.getText());
            admin.setPhone(Phone.getText());
            admin.setDateBirth(birthdate);

            // Gestion de la photo
            File destinationFolder = new File("src/main/resources/images/");
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }
            String newFileName = Lastname.getText() + ".png";
            File destinationFile = new File(destinationFolder, newFileName);

            if (destinationFile.exists()) {
                destinationFile.delete();
            }

            Files.copy(selectedPhotoFile.toPath(), destinationFile.toPath());
            admin.setPhoto(destinationFile.getAbsolutePath());

            // Vérification de l'existence de l'admin dans la base de données
            boolean success;
            if (DatabaseHelper.adminExists(CIN.getText())) {
              //  showInfoAlert("Admin Existant", "Un administrateur avec ce CIN existe déjà. Mise à jour des informations...");
                success = DatabaseHelper.updateAdmin(admin); // Mise à jour
            } else {
               // showInfoAlert("Nouvel Admin", "Aucun administrateur avec ce CIN n'a été trouvé. Ajout dans la base de données...");
                success = DatabaseHelper.addAdmin(admin); // Ajout
            }

            // Affichage des messages de réussite ou d'échec
            if (success) {
                showInfoAlert("Succès", "Les détails de l'administrateur ont été enregistrés avec succès.");
                Stage stage = (Stage) Name.getScene().getWindow();
                stage.close();
            } else {
                showErrorAlert("Erreur", "Une erreur s'est produite lors de l'enregistrement des détails de l'administrateur.");
            }

        } catch (Exception e) {
            showErrorAlert("Erreur", "Une erreur inattendue s'est produite : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour afficher une alerte d'information
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher une alerte d'erreur
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    @FXML
    public void choosePhoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Admin Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(adminPhoto.getScene().getWindow());
        if (file != null) {
            selectedPhotoFile = file;
            adminPhoto.setImage(new Image("file:" + file.getAbsolutePath()));
            showAlert("Photo Selected", null, "The selected photo will be saved when you click 'Save'.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("No Photo Selected", null, "Please select a photo to use for the admin.", Alert.AlertType.WARNING);
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void showAdminDetails() {
        if (admin != null) {
            // Gather admin details in a string
            String adminDetails = "Admin Details:\n" +
                    "Name: " + admin.getName() + "\n" +
                    "Lastname: " + admin.getLastname() + "\n" +
                    "Email: " + admin.getEmail() + "\n" +
                    "CIN: " + admin.getCIN() + "\n" +
                    "Phone: " + admin.getPhone() + "\n" +
                    "Date of Birth: " + (admin.getDateBirth() != null ? admin.getDateBirth() : "Not Set") + "\n" +
                    "Photo: " + (admin.getPhoto() != null && !admin.getPhoto().isEmpty() ? admin.getPhoto() : "No photo");

            // Show the details in an alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Admin Details");
            alert.setHeaderText("Information about the admin");
            alert.setContentText(adminDetails);
            alert.showAndWait();
        } else {
            // If the admin object is null
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Admin Not Found");
            alert.setHeaderText("No admin data available");
            alert.setContentText("The admin object is null, and no data is available to display.");
            alert.showAndWait();
        }
    }



}


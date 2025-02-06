package com.example.projet.controllers;

import com.example.projet.models.Resident;
import com.example.projet.utils.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;

public class UpdateResidentController {

    @FXML
    private BorderPane mainPane;
    @FXML
    private AnchorPane formPane;
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
    private ImageView residentPhoto;

    private Resident selectedResident;
    private File selectedPhotoFile;

    @FXML
    public void initialize() {
        residentStatusChoiceBox.setItems(FXCollections.observableArrayList("Authorized", "NotAuthorized"));
    }

    private void populateFields() {
        if (selectedResident == null) {
            showAlert("Erreur", "Aucune sélection", "Aucun résident sélectionné pour modification.", Alert.AlertType.WARNING);
            return;
        }

        residentNameField.setText(selectedResident.getName());
        residentLastnameField.setText(selectedResident.getLastname());
        residentCINField.setText(selectedResident.getCIN());
        residentPhoneField.setText(selectedResident.getPhone());
        residentBirthdatePicker.setValue(selectedResident.getDateBirth());
        residentStatusChoiceBox.setValue(selectedResident.getStatus().toString());

        // Charger la photo si elle existe
        if (selectedResident.getPhoto() != null) {
            File photoFile = new File(selectedResident.getPhoto());
            if (photoFile.exists()) {
                residentPhoto.setImage(new Image(photoFile.toURI().toString()));
            }
        }
    }

    @FXML
    private void choosePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            selectedPhotoFile = selectedFile;
            Image image = new Image(selectedFile.toURI().toString());
            residentPhoto.setImage(image);
        } else {
            showAlert("Information", "Aucune photo sélectionnée", "Veuillez sélectionner une photo.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void updateResident(ActionEvent event) {
        if (selectedResident == null) {
            showAlert("Erreur", "Aucune sélection", "Veuillez sélectionner un résident pour mise à jour.", Alert.AlertType.WARNING);
            return;
        }

        // Récupérer les données des champs
        String name = residentNameField.getText();
        String lastname = residentLastnameField.getText();
        String CIN = residentCINField.getText();
        String phone = residentPhoneField.getText();
        LocalDate birthDate = residentBirthdatePicker.getValue();
        String status = residentStatusChoiceBox.getValue();

        // Valider les champs
        if (name.isEmpty() || lastname.isEmpty() || CIN.isEmpty() || phone.isEmpty() || birthDate == null || status == null) {
            showAlert("Erreur", "Champs obligatoires manquants", "Veuillez remplir tous les champs requis.", Alert.AlertType.ERROR);
            return;
        }

        if (!validateName(name)) {
            showAlert("Erreur", "Nom invalide", "Le nom ne doit contenir que des lettres et des espaces.", Alert.AlertType.ERROR);
            return;
        }

        if (!validatePhone(phone)) {
            showAlert("Erreur", "Numéro de téléphone invalide", "Le numéro doit commencer par 06 ou 07 et contenir 10 chiffres.", Alert.AlertType.ERROR);
            return;
        }

        if (!validateCNE(CIN)) {
            showAlert("Erreur", "CNE invalide", "Le CNE doit commencer par deux lettres suivies de six chiffres (ex. SH199605).", Alert.AlertType.ERROR);
            return;
        }

        if (!validateAge(birthDate)) {
            showAlert("Erreur", "Âge invalide", "L'âge doit être supérieur à 18 ans.", Alert.AlertType.ERROR);
            return;
        }

        // Sauvegarder une nouvelle photo si sélectionnée
        String photoPath = selectedResident.getPhoto(); // Utiliser la photo existante si aucune nouvelle photo n'est sélectionnée
        if (selectedPhotoFile != null) {
            photoPath = savePhoto(selectedPhotoFile, name);
            if (photoPath == null) {
                showAlert("Erreur", "Photo non sauvegardée", "Impossible de sauvegarder la nouvelle photo.", Alert.AlertType.ERROR);
                return;
            }
        }

        // Mettre à jour l'objet Resident
        selectedResident.setName(name);
        selectedResident.setLastname(lastname);
        selectedResident.setCIN(CIN);
        selectedResident.setPhone(phone);
        selectedResident.setDateBirth(birthDate);
        selectedResident.setStatus(Resident.Status.valueOf(status));
        selectedResident.setPhoto(photoPath);

        // Mise à jour dans la base de données
        boolean success = DatabaseHelper.updateResident(selectedResident);

        if (success) {
            showAlert("Succès", "Résident mis à jour", "Les informations du résident ont été mises à jour avec succès.", Alert.AlertType.INFORMATION);
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) mainPane.getScene().getWindow();
            currentStage.close();

        } else {
            showAlert("Erreur", "Échec de la mise à jour", "Impossible de mettre à jour les informations dans la base de données.", Alert.AlertType.ERROR);
        }
    }


    private String savePhoto(File photoFile, String name) {
        String targetDirectory = "src/main/resources/images/";
        String photoFileName = name + "_" + photoFile.getName();
        Path targetPath = Path.of(targetDirectory, photoFileName);

        try {
            Files.createDirectories(Path.of(targetDirectory));
            Files.copy(photoFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void redirectToResidents(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/views/residents.fxml"));
            Stage stage = (Stage) formPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la vue", "Veuillez réessayer.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setSelectedResident(Resident resident) {
        this.selectedResident = resident;
        populateFields();
    }

    // Validation du nom (doit contenir uniquement des lettres et des espaces)
    private boolean validateName(String name) {
        return name.matches("[a-zA-Z\\s]+");
    }

    // Validation du numéro de téléphone (commence par 06 ou 07 et contient exactement 10 chiffres)
    private boolean validatePhone(String phone) {
        return phone.matches("^(06|07)\\d{8}$");
    }

    // Validation du CNE (commence par 2 lettres suivies de 6 chiffres)
    private boolean validateCNE(String cne) {
        return cne.matches("^[A-Za-z]{2}\\d{6}$");
    }

    // Validation de l'âge (doit être supérieur à 18 ans)
    private boolean validateAge(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears() >= 18;
    }

}

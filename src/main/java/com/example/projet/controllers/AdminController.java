package com.example.projet.controllers;

import com.example.projet.models.Admin;
import com.example.projet.models.Resident;
import com.example.projet.models.Foreigner;
import com.example.projet.utils.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class AdminController {

    // Champs Admin
    @FXML private TextField adminCINField;
    @FXML private TextField adminNameField;
    @FXML private TextField adminLastnameField;
    @FXML private TextField adminPhoneField;
    @FXML private DatePicker AdminBirthdatePicker;
    @FXML private TextField adminEmailField;
    @FXML private PasswordField adminPasswordField;

    // Champs Résident
    @FXML private TextField residentCINField;
    @FXML private TextField residentNameField;
    @FXML private TextField residentLastnameField;
    @FXML private TextField residentPhoneField;
    @FXML private DatePicker residentBirthdatePicker;
    @FXML private ChoiceBox<String> residentStatusChoiceBox;

    // Champs Etranger
    @FXML private TextField foreignerIdField;
    @FXML private TextField foreignerPhotoField;

    // Label de statut
    @FXML private Label statusLabel;

    // Tables Résidents et Étrangers
    @FXML private TableView<Resident> residentsTable;
    @FXML private TableView<Foreigner> foreignersTable;

    private ObservableList<Resident> residentsList;
    private ObservableList<Foreigner> foreignersList;

    @FXML
    private TableColumn<Resident, String> cinColumn;
    @FXML
    private TableColumn<Resident, String> nameColumn;
    @FXML
    private TableColumn<Resident, String> phoneColumn;

    @FXML
    private TableColumn<Foreigner, Integer> idColumn;
    @FXML
    private TableColumn<Foreigner, String> photoColumn;
    @FXML
    private ImageView imageView;

    private String selectedImagePath;

    // Méthode pour choisir une image via un FileChooser
    @FXML
    private void handleChooseImage(ActionEvent event) {
        // Créer un FileChooser pour sélectionner une image
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        // Ouvrir la boîte de dialogue pour choisir l'image
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            // Récupérer le nom de l'image (pour éviter les conflits de noms, vous pouvez ajouter un identifiant unique)
            String imageName = file.getName();

            // Définir le dossier cible où l'image sera copiée
            File targetDir = new File(System.getProperty("user.dir") + "/resources/Image/");

            // S'assurer que le dossier existe
            if (!targetDir.exists()) {
                boolean created = targetDir.mkdirs();  // Créer le dossier si nécessaire
                if (created) {
                    System.out.println("Dossier 'Image' créé avec succès.");
                } else {
                    System.out.println("Le dossier 'Image' existe déjà ou n'a pas pu être créé.");
                }
            }

            // Définir le chemin cible où l'image sera copiée
            File targetFile = new File(targetDir, imageName);

            try {
                // Copier l'image dans le dossier cible
                Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour l'ImageView avec l'image sélectionnée
                imageView.setImage(new javafx.scene.image.Image(targetFile.toURI().toString()));

                // Stocker le chemin relatif de l'image
                selectedImagePath = "resources/Image/" + imageName;

                // Afficher le chemin de l'image dans une alerte
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chemin de l'image sélectionnée");
                alert.setHeaderText("Image choisie avec succès !");
                alert.setContentText("Chemin de l'image : " + selectedImagePath);
                alert.showAndWait();

                System.out.println("Image copiée à : " + targetFile.getAbsolutePath());
            } catch (IOException e) {
                // Gérer les erreurs lors de la copie de l'image
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de copie d'image");
                alert.setHeaderText("Erreur lors du transfert de l'image");
                alert.setContentText("L'image n'a pas pu être copiée dans le dossier cible.");
                alert.showAndWait();
            }
        }
    }


    @FXML
    public void initialize() {
        // Initialisation du choix pour le statut du résident
        residentStatusChoiceBox.setItems(FXCollections.observableArrayList("Authorized", "NotAuthorized"));

        // Initialisation des colonnes pour la table des résidents
        cinColumn.setCellValueFactory(cellData -> cellData.getValue().cinProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());

        // Remplissage des tables avec les données
        residentsList = FXCollections.observableArrayList(DatabaseHelper.getAllResidents());
        residentsTable.setItems(residentsList);

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        photoColumn.setCellValueFactory(cellData -> cellData.getValue().photoProperty());

        foreignersList = FXCollections.observableArrayList(DatabaseHelper.getAllForeigners());
        foreignersTable.setItems(foreignersList);
    }

    /* --- Gestion des Admins --- */

    @FXML
    public void addAdmin(ActionEvent event) {
        try {
            Admin admin = new Admin(
                    adminCINField.getText(),
                    adminNameField.getText(),
                    adminLastnameField.getText(),
                    adminPhoneField.getText(),
                    AdminBirthdatePicker.getValue(),
                    null, // Pour la photo, laissez vide pour l'instant
                    adminEmailField.getText(),
                    adminPasswordField.getText()
            );

            boolean success = DatabaseHelper.addAdmin(admin);

            // Afficher une alerte de confirmation avec les données
            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle(success ? "Succès" : "Erreur");
            alert.setHeaderText(success ? "Admin ajouté avec succès" : "Erreur lors de l'ajout de l'admin");
            alert.setContentText(success ?
                    "Admin:\nCIN: " + admin.getCIN() + "\nNom: " + admin.getName() + " " + admin.getLastname() + "\nEmail: " + admin.getEmail() :
                    "Veuillez vérifier les informations ou réessayer.");
            alert.showAndWait();
        } catch (Exception e) {
            showErrorAlert("Erreur inattendue lors de l'ajout de l'admin", e.getMessage());
        }
    }

    @FXML
    public void getAdmin(ActionEvent event) {
        try {
            String cin = adminCINField.getText();
            Admin admin = DatabaseHelper.getAdmin(cin);

            if (admin != null) {
                adminNameField.setText(admin.getName());
                adminLastnameField.setText(admin.getLastname());
                adminPhoneField.setText(admin.getPhone());
                AdminBirthdatePicker.setValue(admin.getDateBirth());
                adminEmailField.setText(admin.getEmail());
                adminPasswordField.setText(admin.getPassword());

                showInfoAlert("Admin trouvé", "Admin:\nCIN: " + admin.getCIN() + "\nNom: " + admin.getName() + " " + admin.getLastname());
            } else {
                showErrorAlert("Admin introuvable", "Aucun admin trouvé avec le CIN: " + cin);
            }
        } catch (Exception e) {
            showErrorAlert("Erreur inattendue lors de la récupération de l'admin", e.getMessage());
        }
    }

    /* --- Gestion des Résidents --- */

    @FXML
    private void addResident(ActionEvent event) {
        try {
            // Vérifier si une date de naissance a été sélectionnée
            LocalDate birthdate = residentBirthdatePicker.getValue();
            if (birthdate == null) {
                showErrorAlert("Date de naissance manquante", "Veuillez sélectionner une date valide.");
                return;
            }

            // Ouvrir un FileChooser pour sélectionner une photo
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());

            String photoPath = null;
            if (selectedFile != null) {
                photoPath = selectedFile.getAbsolutePath();  // Chemin absolu de l'image
            }

            // Créer un nouvel objet Resident
            Resident resident = new Resident(
                    residentCINField.getText(),
                    residentNameField.getText(),
                    residentLastnameField.getText(),
                    residentPhoneField.getText(),
                    birthdate,
                    photoPath,  // Enregistrer le chemin de la photo
                    Resident.Status.valueOf(residentStatusChoiceBox.getValue())
            );

            // Ajouter le résident à la base de données
            boolean success = DatabaseHelper.addResident(resident);

            // Afficher une alerte selon le succès de l'opération
            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle(success ? "Succès" : "Erreur");
            alert.setHeaderText(success ? "Résident ajouté avec succès" : "Erreur lors de l'ajout du résident");
            alert.setContentText(success ?
                    "Résident:\nCIN: " + resident.getCIN() + "\nNom: " + resident.getName() + " " + resident.getLastname() :
                    "Vérifiez les informations saisies ou réessayez.");
            alert.showAndWait();

            if (success) {
                refreshResidentsTable();
            }

        } catch (Exception e) {
            showErrorAlert("Erreur inattendue lors de l'ajout du résident", e.getMessage());
        }
    }

    @FXML
    public void deleteResident(ActionEvent event) {
        // Récupérer le résident sélectionné dans la TableView
        Resident selectedResident = residentsTable.getSelectionModel().getSelectedItem();

        if (selectedResident != null) {
            String cin = selectedResident.getCIN(); // Récupérer le CIN du résident sélectionné
            boolean success = DatabaseHelper.deleteResidentByCIN(cin);

            // Afficher un message de succès ou d'erreur
            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle(success ? "Succès" : "Erreur");
            alert.setHeaderText(success ? "Résident supprimé" : "Erreur lors de la suppression");
            alert.setContentText(success ? "Le résident avec le CIN " + cin + " a été supprimé." : "Aucune suppression effectuée.");
            alert.showAndWait();

            if (success) {
                refreshResidentsTable();
            }
        } else {
            // Avertir l'utilisateur qu'aucune ligne n'a été sélectionnée
            showErrorAlert("Aucune sélection", "Veuillez sélectionner un résident à supprimer.");
        }
    }


    /* --- Gestion des Étrangers --- */

    @FXML
    public void addForeigner(ActionEvent event) {
        try {
            String photo = foreignerPhotoField.getText();

            Foreigner foreigner = new Foreigner(0, photo);
            boolean success = DatabaseHelper.addForeigner(foreigner);

            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle(success ? "Succès" : "Erreur");
            alert.setHeaderText(success ? "Étranger ajouté avec succès" : "Erreur lors de l'ajout");
            alert.setContentText(success ?
                    "Étranger:\nPhoto: " + photo :
                    "Veuillez vérifier les informations ou réessayer.");
            alert.showAndWait();

            if (success) {
                refreshForeignersTable();
            }
        } catch (Exception e) {
            showErrorAlert("Erreur inattendue lors de l'ajout de l'étranger", e.getMessage());
        }
    }

    @FXML
    public void deleteForeigner(ActionEvent event) {
        // Récupérer l'étranger sélectionné dans la TableView
        Foreigner selectedForeigner = foreignersTable.getSelectionModel().getSelectedItem();

        if (selectedForeigner != null) {
            int id = selectedForeigner.getId(); // Récupérer l'ID de l'étranger sélectionné
            boolean success = DatabaseHelper.deleteForeignerById(id);

            // Afficher un message de succès ou d'erreur
            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle(success ? "Succès" : "Erreur");
            alert.setHeaderText(success ? "Étranger supprimé" : "Erreur lors de la suppression");
            alert.setContentText(success ? "Étranger avec l'ID " + id + " supprimé." : "Aucune suppression effectuée.");
            alert.showAndWait();

            if (success) {
                refreshForeignersTable();
            }
        } else {
            // Avertir l'utilisateur qu'aucune ligne n'a été sélectionnée
            showErrorAlert("Aucune sélection", "Veuillez sélectionner un étranger à supprimer.");
        }
    }


    /* --- Méthodes utilitaires --- */

    private void refreshResidentsTable() {
        residentsList.setAll(DatabaseHelper.getAllResidents());
    }

    private void refreshForeignersTable() {
        foreignersList.setAll(DatabaseHelper.getAllForeigners());
    }

    private void showInfoAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

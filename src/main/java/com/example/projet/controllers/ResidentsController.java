package com.example.projet.controllers;

import com.example.projet.models.Resident;
import com.example.projet.utils.DatabaseHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ResidentsController {

    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Resident> residentTableView;
    @FXML
    private TableColumn<Resident, String> cinColumn, nameColumn, lastnameColumn, phoneColumn, statusColumn, photoColumn;

    private ObservableList<Resident> residentList;

    @FXML
    public void initialize() {
        // Initialize the ComboBox with status options
        statusComboBox.setItems(FXCollections.observableArrayList("AUTHORIZED", "NOTAUTHORIZED"));
        statusComboBox.setOnAction(e -> filterResidentsByStatus()); // Event listener for ComboBox selection

        // Initialize the search TextField with a key listener
        searchTextField.setOnKeyReleased(this::searchByLastname); // Event listener for search by last name

        // Initialize the columns of the TableView
        cinColumn.setCellValueFactory(data -> data.getValue().cinProperty());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        lastnameColumn.setCellValueFactory(data -> data.getValue().lastnameProperty());
        phoneColumn.setCellValueFactory(data -> data.getValue().phoneProperty());
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));

        // Configure the photo column to display images
        photoColumn.setCellValueFactory(cellData -> cellData.getValue().photoProperty());
        photoColumn.setCellFactory(param -> new TableCell<Resident, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String photoPath, boolean empty) {
                super.updateItem(photoPath, empty);
                if (empty || photoPath == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(new javafx.scene.image.Image("file:" + photoPath));
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    setGraphic(imageView);
                }
            }
        });

        // Load initial data from the database
        loadResidents();
    }

    // Method to load the residents list from the database
    private void loadResidents() {
        List<Resident> residents = DatabaseHelper.getAllResidents();
        residentList = FXCollections.observableArrayList(residents);
        residentTableView.setItems(residentList);
    }

    // Method to filter residents by status
    private void filterResidentsByStatus() {
        String selectedStatus = statusComboBox.getValue();
        if (selectedStatus != null) {
            List<Resident> filteredResidents = residentList.stream()
                    .filter(resident -> resident.getStatus().toString().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());
            residentTableView.setItems(FXCollections.observableArrayList(filteredResidents));
        } else {
            residentTableView.setItems(residentList); // If no filter is selected, show all residents
        }
    }

    // Method to search residents by last name
    private void searchByLastname(KeyEvent event) {
        String searchText = searchTextField.getText().toLowerCase();
        List<Resident> filteredResidents = residentList.stream()
                .filter(resident -> resident.getLastname().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        residentTableView.setItems(FXCollections.observableArrayList(filteredResidents));
    }


    @FXML
    private void deleteResident() {
        Resident selectedResident = residentTableView.getSelectionModel().getSelectedItem();
        if (selectedResident == null) {
            showAlert("Erreur", "Aucune sélection", "Veuillez sélectionner un résident à supprimer.", Alert.AlertType.WARNING);
            return;
        }

        boolean success = DatabaseHelper.deleteResidentByCIN(selectedResident.getCIN());
        if (success) {
            showAlert("Succès", "Suppression réussie", "Le résident a été supprimé.", Alert.AlertType.INFORMATION);
            loadResidents();
        } else {
            showAlert("Erreur", "Échec de la suppression", "Impossible de supprimer le résident.", Alert.AlertType.ERROR);
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Obtenir la scène principale (fenêtre) et la définir comme propriétaire de l'alerte
        Stage stage = (Stage) residentTableView.getScene().getWindow();
        alert.initOwner(stage);

        // Afficher l'alerte et attendre l'interaction de l'utilisateur
        alert.showAndWait();
    }

    // Méthode pour éditer un résident
    @FXML
    private void editResent() {
        // Récupération du résident sélectionné
        Resident selectedResident = residentTableView.getSelectionModel().getSelectedItem();

        if (selectedResident == null) {
            // Alerte si aucun résident n'est sélectionné
            showAlert("Erreur", "Aucune sélection!", "Veuillez sélectionner un résident à modifier.", Alert.AlertType.WARNING);
            return;
        }

        try {

            // Chargement de la fenêtre de mise à jour
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/projet/UpdateResident.fxml"));
            // Création de la scène pour la nouvelle fenêtre
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modifier Résident"); // Titre de la fenêtre
            // Transfert des données au contrôleur UpdateResidentController
            UpdateResidentController controller = loader.getController();
            controller.setSelectedResident(selectedResident);
            // Affichage de la nouvelle fenêtre
            stage.show();

        } catch (IOException e) {
            // Gestion des exceptions et affichage d'une alerte
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue", "Une erreur est survenue lors du chargement de la fenêtre.", Alert.AlertType.ERROR);
        }
    }


}

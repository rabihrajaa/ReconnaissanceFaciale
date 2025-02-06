package com.example.projet.controllers;

import com.example.projet.models.History;
import com.example.projet.utils.DatabaseHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TableCell;

import java.util.List;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;



public class HistoryController {

    @FXML
    private TableView<History> historyTableView;
    @FXML
    private TableColumn<History, String> photoColumn;
    @FXML
    private TableColumn<History, String> DateColumn;
    @FXML
    private TableColumn<History, String> HeureColumn;
    @FXML
    private TableColumn<History, String> StatusColumn;
    @FXML
    private TableColumn<History, String> NameColumn;

    @FXML
    public void initialize() {
        // Initialize the columns of the TableView using PropertyValueFactory
        photoColumn.setCellValueFactory(data -> data.getValue().photoProperty());
        DateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        HeureColumn.setCellValueFactory(data -> data.getValue().heureProperty());
        StatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));
        NameColumn.setCellValueFactory(data -> data.getValue().nameProperty());

        // Configure the photo column to display images
        photoColumn.setCellFactory(param -> new TableCell<History, String>() {
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

        try {
            // Load data into the TableView
            List<History> historyList = DatabaseHelper.getHistory();

            // Check each history entry for null values and replace with "null" if necessary
            for (History history : historyList) {
                if (history.getPhoto() == null) {
                    history.setPhoto("null");
                }
                if (history.getDate() == null) {
                    history.setDate("null");
                }
                if (history.getHeure() == null) {
                    history.setHeure("null");
                }
                if (history.getStatus() == null) {
                    history.setStatus("null");
                }
                if (history.getName() == null) {
                    history.setName("null");
                }
            }

            // Create an Alert to display the history list
            StringBuilder historyText = new StringBuilder("History List:\n");

            for (History history : historyList) {
                historyText.append(", Date: ").append(history.getDate())
                        .append(", Name: ").append(history.getName())
                        .append(", Status: ").append(history.getStatus())
                        .append("\n");
            }

            // Show the alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("History List");
            alert.setHeaderText("List of History Records");
            alert.setContentText(historyText.toString());
            alert.showAndWait();

            // Convert history list to ObservableList for the TableView
            ObservableList<History> observableHistoryList = FXCollections.observableArrayList(historyList);
            historyTableView.setItems(observableHistoryList);

        } catch (Exception e) {
            // Handle the exception and show an error alert
            e.printStackTrace();

            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("An error occurred while loading the history.");
            errorAlert.setContentText("Error details: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }


    public void print(ActionEvent actionEvent) {
        try {
            // Chemin de destination du fichier PDF
            String pdfFilePath = "historiquePDF/Historique_d_entree.pdf";

            // Créer un PdfWriter pour le fichier
            PdfWriter writer = new PdfWriter(pdfFilePath);

            // Créer un PdfDocument et un Document pour iText
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Ajouter le titre au PDF
            Paragraph title = new Paragraph("Historique d'entrée")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold();
            document.add(title);

            // Ajouter un espacement entre le titre et le tableau
            document.add(new Paragraph("\n"));

            // Récupérer les données de la TableView
            ObservableList<History> historyList = historyTableView.getItems();

            // Créer un tableau avec 5 colonnes
            float[] columnWidths = {1, 2, 2, 2, 2}; // Largeurs relatives des colonnes
            Table table = new Table(columnWidths);

            // Ajouter les en-têtes de colonnes
            table.addHeaderCell(new Cell().add(new Paragraph("Photo").setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Date").setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Heure").setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Status").setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Name").setTextAlignment(TextAlignment.CENTER)));

            // Ajouter les données de chaque ligne
            for (History history : historyList) {
                // Photo (affiche le chemin si aucune image réelle n'est utilisée)
                table.addCell(new Cell().add(new Paragraph(history.getPhoto() != null ? history.getPhoto() : "N/A")));

                // Date
                table.addCell(new Cell().add(new Paragraph(history.getDate() != null ? history.getDate() : "N/A")));

                // Heure
                table.addCell(new Cell().add(new Paragraph(history.getHeure() != null ? history.getHeure() : "N/A")));

                // Status
                table.addCell(new Cell().add(new Paragraph(history.getStatus() != null ? history.getStatus().toString() : "N/A")));

                // Name
                table.addCell(new Cell().add(new Paragraph(history.getName() != null ? history.getName() : "N/A")));
            }

            // Ajouter le tableau au document
            document.add(table);

            // Fermer le document PDF
            document.close();

            // Afficher un message de succès
            System.out.println("PDF généré avec succès : " + pdfFilePath);

            // Ouvrir automatiquement le fichier PDF
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            java.io.File file = new java.io.File(pdfFilePath);
            if (file.exists()) {
                desktop.open(file);
            }

        } catch (Exception e) {
            e.printStackTrace();

            // Afficher une alerte en cas d'erreur
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Une erreur est survenue lors de la génération du PDF.");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }




}

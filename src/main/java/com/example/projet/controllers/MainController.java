package com.example.projet.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private AnchorPane centerPane;

    public void initialize() {
        loadPage("/com/example/projet/projet/main_layout.fxml");
    }

    public void loadPage(String pageName) {
        try {
            AnchorPane newPage = FXMLLoader.load(getClass().getResource(pageName));
            centerPane.getChildren().setAll(newPage);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading page: " + pageName);
        }
    }

    @FXML
    public void onAddResidentPageClick() {
        loadPage("/com/example/projet/projet/AddResident");
    }

    @FXML
    public void onHomePageClick() {
        loadPage("/com/example/projet/projet/residents");
    }
}

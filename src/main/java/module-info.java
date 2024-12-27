        module com.example.demo {
                requires javafx.controls;
                requires javafx.fxml;
                requires javafx.graphics;
                requires java.sql;

                exports com.example.projet;  // Export the root package if needed
                exports com.example.projet.controllers;  // Specifically export the controllers package
                opens com.example.projet.controllers to javafx.fxml;  // Allow reflection for JavaFX FXML
                }

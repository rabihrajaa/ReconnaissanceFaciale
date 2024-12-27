package com.example.projet;

import com.example.projet.utils.DatabaseHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

    public class Main extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            DatabaseHelper.initializeDatabase();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/projet/projet/admin_dashboard.fxml"));
            primaryStage.setTitle("Admin Dashboard");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
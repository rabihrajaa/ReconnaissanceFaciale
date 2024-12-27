package com.example.projet.utils;

import com.example.projet.models.Admin;
import com.example.projet.models.Resident;
import com.example.projet.models.Foreigner;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:projet_db.sqlite";

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Initialiser la base de données.
     */
    public static void initializeDatabase() {
        String createResidentsTable = """
            CREATE TABLE IF NOT EXISTS residents (
                CIN TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                lastname TEXT NOT NULL,
                phone TEXT,
                dateBirth TEXT,
                photo TEXT,
                status TEXT NOT NULL
            );
        """;

        String createForeignersTable = """
            CREATE TABLE IF NOT EXISTS foreigners (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                photo TEXT
            );
        """;

        String createAdminsTable = """
            CREATE TABLE IF NOT EXISTS admins (
                CIN TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                lastname TEXT NOT NULL,
                phone TEXT,
                dateBirth TEXT,
                photo TEXT,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            );
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createResidentsTable);
            stmt.execute(createForeignersTable);
            stmt.execute(createAdminsTable);

            System.out.println("Base de données initialisée avec succès.");
        } catch (SQLException e) {
            showAlert("Erreur d'initialisation", "Base de données", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /* --- Méthodes pour Resident --- */

    public static boolean addResident(Resident resident) {
        String query = "INSERT INTO residents (CIN, name, lastname, phone, dateBirth, photo, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, resident.getCIN());
            pstmt.setString(2, resident.getName());
            pstmt.setString(3, resident.getLastname());
            pstmt.setString(4, resident.getPhone());
            pstmt.setString(5, resident.getDateBirth().toString());
            pstmt.setString(6, resident.getPhoto());  // Enregistrer le chemin de la photo
            pstmt.setString(7, resident.getStatus().name());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showAlert("Erreur lors de l'ajout", "Resident", e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }


    public static boolean deleteResidentByCIN(String cin) {
        String query = "DELETE FROM residents WHERE CIN = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, cin);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showAlert("Erreur lors de la suppression", "Resident", e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public static List<Resident> getAllResidents() {
        List<Resident> residents = new ArrayList<>();
        String query = "SELECT * FROM residents";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                residents.add(new Resident(
                        rs.getString("CIN"),
                        rs.getString("name"),
                        rs.getString("lastname"),
                        rs.getString("phone"),
                        LocalDate.parse(rs.getString("dateBirth")),
                        rs.getString("photo"),
                        Resident.Status.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            showAlert("Erreur lors de la récupération", "Residents", e.getMessage(), Alert.AlertType.ERROR);
        }
        return residents;
    }

    /* --- Méthodes pour Foreigner --- */

    public static boolean addForeigner(Foreigner foreigner) {
        String query = "INSERT INTO foreigners (photo) VALUES (?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, foreigner.getPhoto());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showAlert("Erreur lors de l'ajout", "Foreigner", e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public static boolean deleteForeignerById(int id) {
        String query = "DELETE FROM foreigners WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showAlert("Erreur lors de la suppression", "Foreigner", e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public static List<Foreigner> getAllForeigners() {
        List<Foreigner> foreigners = new ArrayList<>();
        String query = "SELECT * FROM foreigners";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                foreigners.add(new Foreigner(
                        rs.getInt("id"),
                        rs.getString("photo")
                ));
            }
        } catch (SQLException e) {
            showAlert("Erreur lors de la récupération", "Foreigners", e.getMessage(), Alert.AlertType.ERROR);
        }
        return foreigners;
    }

    /* --- Méthodes pour Admin --- */

    public static Admin getAdmin(String cin) {
        String query = "SELECT * FROM admins WHERE CIN = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, cin);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Construire un objet Admin à partir des données récupérées
                    return new Admin(
                            rs.getString("CIN"),
                            rs.getString("name"),
                            rs.getString("lastname"),
                            rs.getString("phone"),
                            LocalDate.parse(rs.getString("dateBirth")), // Conversion String -> LocalDate
                            rs.getString("photo"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            showAlert("Erreur lors de la récupération", "Admin", e.getMessage(), Alert.AlertType.ERROR);
        }
        return null; // Retourner null si aucun résultat trouvé ou erreur
    }


    public static boolean addAdmin(Admin admin) {
        String query = "INSERT INTO admins (CIN, name, lastname, phone, dateBirth, photo, email, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, admin.getCIN());
            pstmt.setString(2, admin.getName());
            pstmt.setString(3, admin.getLastname());
            pstmt.setString(4, admin.getPhone());
            pstmt.setString(5, admin.getDateBirth().toString());
            pstmt.setString(6, admin.getPhoto());
            pstmt.setString(7, admin.getEmail());
            pstmt.setString(8, admin.getPassword());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showAlert("Erreur lors de l'ajout", "Admin", e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    /* --- Méthode utilitaire pour afficher des alertes --- */
    private static void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

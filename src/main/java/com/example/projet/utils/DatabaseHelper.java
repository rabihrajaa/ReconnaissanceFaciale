package com.example.projet.utils;

import com.example.projet.models.Admin;
import com.example.projet.models.History;
import com.example.projet.models.Resident;
import com.example.projet.models.Foreigner;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    public static final String URL = "jdbc:sqlite:projet_db.sqlite";

    public static Connection connect() throws SQLException {
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

        String createDateEntreTable = """
    CREATE TABLE IF NOT EXISTS date_entre (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        resident_cin TEXT,
        admin_cin TEXT,
        id_foreigner INTEGER,
        entry_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (resident_cin) REFERENCES residents(CIN),
        FOREIGN KEY (admin_cin) REFERENCES admins(CIN),
        FOREIGN KEY (id_foreigner) REFERENCES foreigners(id),
        CHECK (
            (resident_cin IS NOT NULL AND admin_cin IS NULL AND id_foreigner IS NULL) OR 
            (resident_cin IS NULL AND admin_cin IS NOT NULL AND id_foreigner IS NULL) OR 
            (resident_cin IS NULL AND admin_cin IS NULL AND id_foreigner IS NOT NULL)
        )
    );
""";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createResidentsTable);
            stmt.execute(createForeignersTable);
            stmt.execute(createAdminsTable);
            stmt.execute(createDateEntreTable);

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

    public static boolean adminExists(String cin) {
        String query = "SELECT 1 FROM admins WHERE CIN = ?"; // Vérifie si un admin avec ce CIN existe
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, cin); // Remplace le ? par la valeur du CIN
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Retourne true si une ligne est trouvée, sinon false
            }

        } catch (Exception e) {
            e.printStackTrace(); // Affiche l'erreur dans la console
            return false; // Retourne false en cas d'erreur
        }
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
            /*
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
            alert.setTitle("Admin Details ajouuuuuuuuuutee");
            alert.setHeaderText("Information about the admin");
            alert.setContentText(adminDetails);
            alert.showAndWait();
            */
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showAlert("Erreur lors de l'ajout", "Admin", e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public static boolean updateAdmin(Admin admin) {
        /*
        String adminDetails = "Update adminnnn:\n" +
                "Name: " + admin.getName() + "\n" +
                "Lastname: " + admin.getLastname() + "\n" +
                "Email: " + admin.getEmail() + "\n" +
                "CIN: " + admin.getCIN() + "\n" +
                "Phone: " + admin.getPhone() + "\n" +
                "Date of Birth: " + (admin.getDateBirth() != null ? admin.getDateBirth() : "Not Set") + "\n" +
                "Photo: " + (admin.getPhoto() != null && !admin.getPhoto().isEmpty() ? admin.getPhoto() : "No photo");

        // Show the details in an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Admin Update");
        alert.setHeaderText("Information about the admin");
        alert.setContentText(adminDetails);
        alert.showAndWait();
        */
        // Requête SQL pour mettre à jour les informations de l'administrateur
        String query = "UPDATE admins SET name = ?, lastname = ?, email = ?, phone = ?, dateBirth = ?, photo = ? WHERE CIN = ?";

        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // Remplir les paramètres de la requête préparée
            pstmt.setString(1, admin.getName());
            pstmt.setString(2, admin.getLastname());
            pstmt.setString(3, admin.getEmail());
            pstmt.setString(4, admin.getPhone());
            pstmt.setString(5, admin.getDateBirth().toString());
            pstmt.setString(6, admin.getPhoto());
            pstmt.setString(7, admin.getCIN());

            // Exécuter la requête et vérifier le nombre de lignes affectées
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0; // Retourne vrai si au moins une ligne a été modifiée

        } catch (SQLException e) {
            // Afficher les erreurs pour le débogage
            System.err.println("Erreur lors de la mise à jour de l'administrateur : " + e.getMessage());
            e.printStackTrace();
            return false; // Retourne faux en cas d'échec
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


    public static boolean residentExists(String cne) {
        String query = "SELECT COUNT(*) FROM residents WHERE cin = ?"; // Adjust the column name "cin" if needed.
        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, cne);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // If count > 0, resident exists
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Default to not existing if there's an exception
    }


    public static boolean updateResident(Resident resident) {
        String query = "UPDATE residents SET name = ?, lastname = ?, phone = ?, dateBirth = ?, status = ?, photo = ? WHERE CIN = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            // Remplir les paramètres de la requête
            stmt.setString(1, resident.getName());
            stmt.setString(2, resident.getLastname());
            stmt.setString(3, resident.getPhone());
            stmt.setString(4, resident.getDateBirth().toString()); // Conversion LocalDate -> String
            stmt.setString(5, resident.getStatus().toString()); // Enregistrer le statut sous forme de texte
            stmt.setString(6, resident.getPhoto()); // Enregistrer le chemin de la photo
            stmt.setString(7, resident.getCIN()); // Identifier le résident à mettre à jour par son CIN

            // Exécuter la requête
            return stmt.executeUpdate() > 0; // Retourne vrai si au moins une ligne est affectée
        } catch (SQLException e) {
            e.printStackTrace(); // Loguer l'erreur pour debug
            return false; // Retourner faux en cas d'échec
        }
    }


        public static List<History> getHistory() throws Exception {
            List<History> historyList = new ArrayList<>();

            // SQL query to fetch data from residents and foreigners tables
            String query = """
            SELECT r.photo AS photo, CURRENT_DATE AS date, CURRENT_TIME AS heure, r.status AS status, r.name AS name
            FROM residents r
            UNION ALL
            SELECT f.photo AS photo, CURRENT_DATE AS date, CURRENT_TIME AS heure, 'Foreigner' AS status, NULL AS name
            FROM foreigners f;
        """;

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    // Get values from result set
                    String photo = rs.getString("photo");
                    String date = rs.getString("date");
                    String heure = rs.getString("heure");
                    String status = rs.getString("status");
                    String name = rs.getString("name");

                    // Handle null values and set default if necessary
                    if (photo == null) photo = "null";
                    if (date == null) date = "null";
                    if (heure == null) heure = "null";
                    if (status == null) status = "null";
                    if (name == null) name = "null";

                    // Add new History object to the list
                    historyList.add(new History(photo, date, heure, status, name));
                }
            } catch (SQLException e) {
                // Log and rethrow exception if needed
                e.printStackTrace();
                throw new Exception("Error fetching history data: " + e.getMessage());
            }

            return historyList;
        }


    // Méthode pour récupérer un seul admin
    public static Admin getSingleAdmin() {
        String query = "SELECT * FROM admins LIMIT 1";  // Récupère le premier admin de la base de données

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            // Vérifiez si le résultat contient une ligne
            if (rs.next()) {
                // Récupérer les données de la base de données
                String CIN = rs.getString("CIN");
                String name = rs.getString("name");
                String lastname = rs.getString("lastname");
                String phone = rs.getString("phone");
                String dateBirthRaw = rs.getString("dateBirth"); // Récupérer la date brute
                LocalDate dateBirth = dateBirthRaw != null ? LocalDate.parse(dateBirthRaw) : null; // Convertir en LocalDate
                String photo = rs.getString("photo");
                String email = rs.getString("email");
                String password = rs.getString("password");
                /*
                // Affichage des informations pour le débogage
                String debugInfo = String.format(
                        "CIN: %s\nName: %s\nLastname: %s\nPhone: %s\nDate of Birth: %s\nPhoto Path: %s\nEmail: %s\nPassword: %s",
                        CIN, name, lastname, phone, dateBirth != null ? dateBirth.toString() : "NULL", photo, email, password
                );

                // Afficher une alerte contenant les données
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Debug Admin Information");
                alert.setHeaderText("Admin Data Retrieved");
                alert.setContentText(debugInfo);
                alert.showAndWait();
                */
                // Retourner l'admin
                return new Admin(CIN, name, lastname, phone, dateBirth, photo, email, password);
            } else {
                // Si aucun admin trouvé, afficher une alerte
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Debug Admin Information");
                alert.setHeaderText("No Admin Found");
                alert.setContentText("Aucun administrateur n'a été trouvé dans la base de données.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            // Gérer les exceptions SQL et afficher les informations d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Fetching Admin");
            alert.setHeaderText("SQL Error");
            alert.setContentText("Message: " + e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            // Gérer d'autres exceptions, par exemple, des erreurs de conversion
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Fetching Admin");
            alert.setHeaderText("Unexpected Error");
            alert.setContentText("Message: " + e.getMessage());
            alert.showAndWait();
        }

        // Retourner null si aucun admin n'est trouvé ou en cas d'erreur
        return null;
    }



}



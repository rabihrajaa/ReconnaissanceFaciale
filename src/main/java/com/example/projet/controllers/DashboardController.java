package com.example.projet.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import java.sql.*;

public class DashboardController {

    @FXML
    private Label authorizedLabel;

    @FXML
    private Label nonAauthorizedLabel;

    @FXML
    private Label strangersLabel;

    @FXML
    private PieChart pieChart;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    public void initialize() {
        initializeCharts();
        updateDashboard();
    }
    public void updateResidentPercentages(Label authorizedLabel, Label nonAuthorizedLabel, Label strangersLabel) {
        Connection connection = null;
        PreparedStatement authorizedStmt = null;
        PreparedStatement nonAuthorizedStmt = null;
        PreparedStatement strangersStmt = null;
        ResultSet authorizedResult = null;
        ResultSet nonAuthorizedResult = null;
        ResultSet strangersResult = null;

        try {
            // Assuming you already have a connection to your SQLite database
            connection = DriverManager.getConnection("jdbc:sqlite:projet_db.sqlite");

            // SQL query to get the count of authorized residents (status = 'authorized')
            String authorizedQuery = "SELECT COUNT(*) AS authorized_count FROM date_entre d " +
                    "JOIN residents r ON d.resident_cin = r.CIN WHERE r.status = 'authorized'";
            authorizedStmt = connection.prepareStatement(authorizedQuery);
            authorizedResult = authorizedStmt.executeQuery();

            // SQL query to get the count of non-authorized residents (status = 'non-authorized')
            String nonAuthorizedQuery = "SELECT COUNT(*) AS non_authorized_count FROM date_entre d " +
                    "JOIN residents r ON d.resident_cin = r.CIN WHERE r.status = 'non-authorized'";
            nonAuthorizedStmt = connection.prepareStatement(nonAuthorizedQuery);
            nonAuthorizedResult = nonAuthorizedStmt.executeQuery();

            // SQL query to get the count of foreigners
            String strangersQuery = "SELECT COUNT(*) AS strangers_count FROM date_entre d " +
                    "LEFT JOIN residents r ON d.resident_cin = r.CIN WHERE r.status IS NULL";
            strangersStmt = connection.prepareStatement(strangersQuery);
            strangersResult = strangersStmt.executeQuery();

            // Get total count of records in date_entre table (to calculate percentage)
            String totalQuery = "SELECT COUNT(*) AS total_count FROM date_entre";
            Statement totalStmt = connection.createStatement();
            ResultSet totalResult = totalStmt.executeQuery(totalQuery);
            int totalCount = totalResult.getInt("total_count");

            // Get counts for each category
            int authorizedCount = authorizedResult.getInt("authorized_count");
            int nonAuthorizedCount = nonAuthorizedResult.getInt("non_authorized_count");
            int strangersCount = strangersResult.getInt("strangers_count");

            // Calculate percentages
            double authorizedPercentage = (totalCount == 0) ? 0 : (authorizedCount * 100.0) / totalCount;
            double nonAuthorizedPercentage = (totalCount == 0) ? 0 : (nonAuthorizedCount * 100.0) / totalCount;
            double strangersPercentage = (totalCount == 0) ? 0 : (strangersCount * 100.0) / totalCount;

            // Update the labels with the percentages
            authorizedLabel.setText(String.format("Authorized Residents: %.2f%%", authorizedPercentage));
            nonAuthorizedLabel.setText(String.format("Non-Authorized Residents: %.2f%%", nonAuthorizedPercentage));
            strangersLabel.setText(String.format("Foreigners: %.2f%%", strangersPercentage));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (authorizedResult != null) authorizedResult.close();
                if (nonAuthorizedResult != null) nonAuthorizedResult.close();
                if (strangersResult != null) strangersResult.close();
                if (authorizedStmt != null) authorizedStmt.close();
                if (nonAuthorizedStmt != null) nonAuthorizedStmt.close();
                if (strangersStmt != null) strangersStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Initialize the charts with default settings.
     */
    private void initializeCharts() {
        // Initialize Pie Chart with placeholder data
        pieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Authorized", 0),
                new PieChart.Data("Non-Authorized", 0),
                new PieChart.Data("Foreigners", 0)
        ));

        // Clear Line Chart data
        lineChart.getData().clear();
    }

    private void updateDashboard() {
        String DATABASE_URL = "jdbc:sqlite:projet_db.sqlite";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            updatePieChart(connection);
            updateLineChart(lineChart, connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePieChart(Connection connection) {
        try {
            String query = """
                    SELECT 
                        SUM(CASE WHEN r.status = 'Authorized' THEN 1 ELSE 0 END) AS AuthorizedCount,
                        SUM(CASE WHEN r.status = 'Non-Authorized' THEN 1 ELSE 0 END) AS NonAuthorizedCount,
                        SUM(CASE WHEN d.resident_cin IS NULL AND d.id_foreigner IS NOT NULL THEN 1 ELSE 0 END) AS ForeignersCount
                    FROM date_entre d
                    LEFT JOIN residents r ON d.resident_cin = r.CIN
                    """;
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int authorizedCount = resultSet.getInt("AuthorizedCount");
                int nonAuthorizedCount = resultSet.getInt("NonAuthorizedCount");
                int foreignersCount = resultSet.getInt("ForeignersCount");

                int total = authorizedCount + nonAuthorizedCount + foreignersCount;

                // Calculate percentages
                double authorizedPercentage = (total > 0) ? (double) authorizedCount / total * 100 : 0;
                double nonAuthorizedPercentage = (total > 0) ? (double) nonAuthorizedCount / total * 100 : 0;
                double foreignersPercentage = (total > 0) ? (double) foreignersCount / total * 100 : 0;

                // Update labels
                authorizedLabel.setText(String.format("%.1f%%", authorizedPercentage));
                nonAauthorizedLabel.setText(String.format("%.1f%%", nonAuthorizedPercentage));
                strangersLabel.setText(String.format("%.1f%%", foreignersPercentage));

                // Update Pie Chart with percentages
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                        new PieChart.Data("Authorized", authorizedPercentage),
                        new PieChart.Data("Non-Authorized", nonAuthorizedPercentage),
                        new PieChart.Data("Foreigners", foreignersPercentage)
                );
                pieChart.setData(pieChartData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLineChart(LineChart<Number, Number> lineChart, Connection connection) {
        // Clear the previous data from the chart
        lineChart.getData().clear();

        // Create series for residents and foreigners
        XYChart.Series<Number, Number> residentsSeries = new XYChart.Series<>();
        residentsSeries.setName("Residents");

        XYChart.Series<Number, Number> foreignersSeries = new XYChart.Series<>();
        foreignersSeries.setName("Foreigners");

        // Query residents data
        String residentQuery = "SELECT DATE(entry_date) AS entry_date, COUNT(*) AS resident_count FROM date_entre d " +
                "WHERE d.resident_cin IS NOT NULL GROUP BY DATE(entry_date) ORDER BY entry_date";
        try (PreparedStatement stmt = connection.prepareStatement(residentQuery)) {
            ResultSet rsResidents = stmt.executeQuery();

            // Add data for residents to the series
            while (rsResidents.next()) {
                String date = rsResidents.getString("entry_date");
                int count = rsResidents.getInt("resident_count");

                // Convert date to milliseconds (Unix timestamp) for X-axis
                long timeInMillis = Date.valueOf(date).getTime();
                residentsSeries.getData().add(new XYChart.Data<>(timeInMillis, count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Query foreigners data
        String foreignersQuery = "SELECT DATE(entry_date) AS entry_date, COUNT(*) AS foreigner_count " +
                "FROM date_entre d " +
                "WHERE d.resident_cin IS NULL " +
                "GROUP BY DATE(entry_date) ORDER BY entry_date";
        try (PreparedStatement stmt = connection.prepareStatement(foreignersQuery)) {
            ResultSet rsForeigners = stmt.executeQuery();

            // Add data for foreigners to the series
            while (rsForeigners.next()) {
                String date = rsForeigners.getString("entry_date");
                int count = rsForeigners.getInt("foreigner_count");

                // Convert date to milliseconds (Unix timestamp) for X-axis
                long timeInMillis = Date.valueOf(date).getTime();
                foreignersSeries.getData().add(new XYChart.Data<>(timeInMillis, count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add the series to the chart
        lineChart.getData().addAll(residentsSeries, foreignersSeries);
    }
}

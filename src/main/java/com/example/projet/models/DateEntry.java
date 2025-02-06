package com.example.projet.models;

import java.time.LocalDateTime;

public class DateEntry {
    private int id;
    private LocalDateTime dateEntry; // LocalDateTime pour DATETIME
    private String ResidentCIN; // Foreign key vers User (peut être null)
    private Integer foreignerId; // Foreign key vers Foreigner (peut être null)

    // Constructeur complet
    public DateEntry(int id, LocalDateTime dateEntry, String userCIN, Integer foreignerId) {
        this.id = id;
        this.dateEntry = dateEntry;
        this.ResidentCIN = userCIN;
        this.foreignerId = foreignerId;
    }

    // Constructeur sans userCIN (pour les étrangers)
    public DateEntry(int id, LocalDateTime dateEntry, Integer foreignerId) {
        this.id = id;
        this.dateEntry = dateEntry;
        this.foreignerId = foreignerId;
    }

    // Constructeur sans foreignerId (pour les résidents)
    public DateEntry(int id, LocalDateTime dateEntry, String userCIN) {
        this.id = id;
        this.dateEntry = dateEntry;
        this.ResidentCIN = userCIN;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateEntry() {
        return dateEntry;
    }

    public void setDateEntry(LocalDateTime dateEntry) {
        this.dateEntry = dateEntry;
    }

    public String getResidentCIN() {
        return ResidentCIN;
    }

    public void setResidentCIN(String userCIN) {
        this.ResidentCIN = userCIN;
    }

    public Integer getForeignerId() {
        return foreignerId;
    }

    public void setForeignerId(Integer foreignerId) {
        this.foreignerId = foreignerId;
    }
}

package com.example.projet.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class History {

    private final StringProperty photo = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty heure = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();

    // Constructor to initialize all properties
    public History(String photo, String date, String heure, String status, String name) {
        this.photo.set(photo);
        this.date.set(date);
        this.heure.set(heure);
        this.status.set(status);
        this.name.set(name);
    }

    // Getter and setter methods for each property
    public String getPhoto() {
        return photo.get();
    }

    public void setPhoto(String photo) {
        this.photo.set(photo);
    }

    public StringProperty photoProperty() {
        return photo;
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public StringProperty dateProperty() {
        return date;
    }

    public String getHeure() {
        return heure.get();
    }

    public void setHeure(String heure) {
        this.heure.set(heure);
    }

    public StringProperty heureProperty() {
        return heure;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }
}

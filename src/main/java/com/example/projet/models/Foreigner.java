package com.example.projet.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Foreigner {

    // Observable properties
    private final IntegerProperty id;
    private final StringProperty photo;

    // Constructor
    public Foreigner(int id, String photo) {
        this.id = new SimpleIntegerProperty(id);
        this.photo = new SimpleStringProperty(photo);
    }

    // Getters for properties (for binding)
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty photoProperty() {
        return photo;
    }

    // Getters for raw values
    public int getId() {
        return id.get();
    }

    public String getPhoto() {
        return photo.get();
    }

    // Setters for raw values
    public void setId(int id) {
        this.id.set(id);
    }

    public void setPhoto(String photo) {
        this.photo.set(photo);
    }
}

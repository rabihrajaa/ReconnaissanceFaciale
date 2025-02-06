package com.example.projet.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private StringProperty CIN;
    private StringProperty name;
    private StringProperty lastname;
    private StringProperty phone;
    private ObjectProperty<LocalDate> dateBirth;
    private StringProperty photo;
    private List<DateEntry> dateEntries; // Liste des entrées associées

    public User(String CIN, String name, String lastname, String phone, LocalDate dateBirth, String photo) {
        this.CIN = new SimpleStringProperty(CIN);
        this.name = new SimpleStringProperty(name);
        this.lastname = new SimpleStringProperty(lastname);
        this.phone = new SimpleStringProperty(phone);
        this.dateBirth = new SimpleObjectProperty<>(dateBirth);
        this.photo = new SimpleStringProperty(photo);
        this.dateEntries = new ArrayList<>();
    }
    public User() {
        this.CIN = new SimpleStringProperty("");
        this.name = new SimpleStringProperty("");
        this.lastname = new SimpleStringProperty("");
        this.phone = new SimpleStringProperty("");
        this.dateBirth = new SimpleObjectProperty<>(null);  // LocalDate can be null
        this.photo = new SimpleStringProperty("");
        this.dateEntries = new ArrayList<>();
    }


    public String getCIN() {
        return CIN.get();
    }

    public void setCIN(String CIN) {
        this.CIN.set(CIN);
    }

    public StringProperty cinProperty() {
        return CIN;
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

    public String getLastname() {
        return lastname.get();
    }

    public void setLastname(String lastname) {
        this.lastname.set(lastname);
    }

    public StringProperty lastnameProperty() {
        return lastname;
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public StringProperty phoneProperty() {
        return phone;
    }


    public String getPhoto() {
        return photo.get();
    }

    public void setPhoto(String photo) {
        this.photo.set(photo);
    }

    public StringProperty photoProperty() {
        return photo;
    }

    public List<DateEntry> getDateEntries() {
        return dateEntries;
    }

    public void addDateEntry(DateEntry dateEntry) {
        this.dateEntries.add(dateEntry);
    }


    public LocalDate getDateBirth() { return dateBirth.get(); }
    public void setDateBirth(LocalDate dateBirth) { this.dateBirth.set(dateBirth); }
    public ObjectProperty<LocalDate> dateBirthProperty() { return dateBirth; }


}

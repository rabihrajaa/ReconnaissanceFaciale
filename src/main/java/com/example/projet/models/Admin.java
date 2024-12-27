package com.example.projet.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Admin extends User {
    private StringProperty email;
    private StringProperty password;

    // Constructor
    public Admin(String CIN, String name, String lastname, String phone, LocalDate dateBirth, String photo, String email, String password) {
        super(CIN, name, lastname, phone, dateBirth, photo);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
    }

    // Observable Getters and Setters
    public StringProperty emailProperty() {
        return email;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    // Methods
    /**
     * Update the admin profile.
     *
     * @param admin the updated admin data.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateProfile(Admin admin) {
        // Add your update logic here (e.g., update database record)
        return true;
    }

    /**
     * Reset the admin password.
     *
     * @param email the email address to reset the password for.
     * @return true if the password reset was successful, false otherwise.
     */
    public boolean forgotPassword(String email) {
        // Add your password reset logic here
        return true;
    }
}

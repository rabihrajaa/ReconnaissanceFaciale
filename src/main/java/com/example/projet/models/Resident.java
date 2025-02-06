package com.example.projet.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Resident extends User {

    private final ObjectProperty<Status> status;

    public Resident(String CIN, String name, String lastname, String phone, LocalDate dateBirth, String photo, Status status) {
        super(CIN, name, lastname, phone, dateBirth, photo);  // Call to the parent constructor (User)
        this.status = new SimpleObjectProperty<>(status);
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public Status getStatus() {
        return status.get();
    }

    public void setStatus(Status status) {
        this.status.set(status);
    }

    public enum Status {
        Authorized, NotAuthorized
    }
}

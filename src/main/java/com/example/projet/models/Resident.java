package com.example.projet.models;

import java.time.LocalDate;

public class Resident extends User {
    public enum Status { Authorized, NotAuthorized }

    private Status status;
    // Constructor
    public Resident(String CIN, String name, String lastname, String phone, LocalDate dateBirth, String photo, Status status) {
        super(CIN, name, lastname, phone, dateBirth, photo);
        this.status = status;
    }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    // Operations
    public boolean addResident(Resident resident) { return true; }
    public Resident searchByName(String name) { return null; }
    public boolean updateResident(Resident resident) { return true; }
    public boolean deleteResident(Resident resident) { return true; }
}

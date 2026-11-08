/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.model;

import java.time.LocalDateTime;

/**
 *
 * @author USUARIO
 */
public class QuarantineRecord {
    //table: registro_cuarentena
    private int id;
    private int lotId;
    private String reason;
    private String user;
    private LocalDateTime registrationDate;
    private boolean resolved;
    private LocalDateTime resolutionDate;
    private String resolutionNotes;

    public QuarantineRecord(int lotId, String reason, String user) {
        this.lotId = lotId;
        this.reason = reason;
        this.user = user;
    }

    public QuarantineRecord(int id, int lotId, String reason, String user, LocalDateTime registrationDate, boolean resolved, LocalDateTime resolutionDate, String resolutionNotes) {
        this.id = id;
        this.lotId = lotId;
        this.reason = reason;
        this.user = user;
        this.registrationDate = registrationDate;
        this.resolved = resolved;
        this.resolutionDate = resolutionDate;
        this.resolutionNotes = resolutionNotes;
    }

    public int getId() {
        return id;
    }

    public int getLotId() {
        return lotId;
    }

    public String getReason() {
        return reason;
    }

    public String getUser() {
        return user;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public boolean isResolved() {
        return resolved;
    }

    public LocalDateTime getResolutionDate() {
        return resolutionDate;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }   
}

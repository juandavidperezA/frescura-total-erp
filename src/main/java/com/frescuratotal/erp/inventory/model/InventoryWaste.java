/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author USUARIO
 */
public class InventoryWaste {
    private int id;
    private int lotId;
    private Integer inspectionId;
    private BigDecimal lostQuantity;
    private String reason;
    private LocalDateTime registrationDate;
    private String user;

    public InventoryWaste(int lotId, Integer inspectionId, BigDecimal lostQuantity, String reason, String user) {
        this.lotId = lotId;
        this.inspectionId = inspectionId;
        this.lostQuantity = lostQuantity;
        this.reason = reason;
        this.user = user;
    }

    public InventoryWaste(int id, int lotId, Integer inspectionId, BigDecimal lostQuantity, String reason, LocalDateTime registrationDate, String user) {
        this.id = id;
        this.lotId = lotId;
        this.inspectionId = inspectionId;
        this.lostQuantity = lostQuantity;
        this.reason = reason;
        this.registrationDate = registrationDate;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public int getLotId() {
        return lotId;
    }

    public Integer getInspectionId() {
        return inspectionId;
    }

    public BigDecimal getLostQuantity() {
        return lostQuantity;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public String getUser() {
        return user;
    }
    
    
}

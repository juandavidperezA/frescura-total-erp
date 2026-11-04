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
public class QualityInspection {
    private int inspectionId;
    private int lotId;
    private LocalDateTime inspectionDate;
    private String result;
    private String inspectorName;
    private String rejectionReason;
    private String notes;
    
    
    public QualityInspection(int inspectionId, int lotId,
        LocalDateTime inspectionDate, String result,
        String inspectorName, String rejectionReason, String notes) {
        this.inspectionId = inspectionId;
        this.lotId = lotId;
        this.inspectionDate = inspectionDate;
        this.result = result;
        this.inspectorName = inspectorName;
        this.rejectionReason = rejectionReason;
        this.notes = notes;
}
    public QualityInspection(int lotId, String result,
            String inspectorName, String rejectionReason, String notes) {
        this.lotId = lotId;
        this.result = result;
        this.inspectorName = inspectorName;
        this.rejectionReason = rejectionReason;
        this.notes = notes;
    }

    public int getInspectionId() {
        return inspectionId;
    }

    public int getLotId() {
        return lotId;
    }

    public LocalDateTime getInspectionDate() {
        return inspectionDate;
    }

    public String getResult() {
        return result;
    }

    public String getInspectorName() {
        return inspectorName;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getNotes() {
        return notes;
    }
    
    
    
    
}

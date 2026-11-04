/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.dao;

import com.frescuratotal.erp.config.DatabaseConnection;
import com.frescuratotal.erp.inventory.model.QualityInspection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author USUARIO
 */
public class QualityInspectionDAO {
    public int insertInspection(QualityInspection inspection)throws SQLException{
        if (inspection.getLotId() <= 0) {
            throw new IllegalArgumentException("Lot ID must be positive");
        }
        if (inspection == null) {
            throw new IllegalArgumentException("Inspection cannot be null");
        }
        
        if (!"Aprobado".equalsIgnoreCase(inspection.getResult())
            && !"Rechazado".equalsIgnoreCase(inspection.getResult())){
                throw new IllegalArgumentException("Invalid inspection result");
            }
        
        if (inspection.getInspectorName() == null
                || inspection.getInspectorName().isBlank()) {
            throw new IllegalArgumentException("Inspector name cannot be empty");
        }

        if ("Rechazado".equalsIgnoreCase(inspection.getResult())
                && (inspection.getRejectionReason() == null
                || inspection.getRejectionReason().isBlank())) {
            throw new IllegalArgumentException("Rejection reason is required");
        }
        
         String result = inspection.getResult().equalsIgnoreCase("Aprobado") ? "Aprobado" : "Rechazado";
         
        String sql="""
                    INSERT INTO inspecciones_calidad
                    (id_lote, resultado, nombre_inspector, motivo_rechazo, notas)
                    VALUES (?, ?, ?, ?, ?)
                  """;
        
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, inspection.getLotId());
            ps.setString(2, result);
            ps.setString(3, inspection.getInspectorName());
            ps.setString(4, inspection.getRejectionReason());
            ps.setString(5, inspection.getNotes());
            
            ps.executeUpdate();
            
            try(ResultSet rs= ps.getGeneratedKeys()){
                return rs.next() ? rs.getInt(1) :-1;
            }
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.dao;

import com.frescuratotal.erp.config.DatabaseConnection;
import com.frescuratotal.erp.inventory.model.InventoryWaste;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author USUARIO
 */
public class InventoryWasteDAO {
    public int insertWaste(InventoryWaste waste) throws SQLException{
        if (waste == null) {
            throw new IllegalArgumentException("Waste record cannot be null");
        }

        if (waste.getLostQuantity() == null
                || waste.getLostQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Lost quantity must be positive");
        }
        
        if (waste.getLotId() <= 0) {
            throw new IllegalArgumentException("Lot ID must be positive");
        }

        if (waste.getReason() == null || waste.getReason().isBlank()) {
            throw new IllegalArgumentException("Waste reason cannot be empty");
        }
        
        String sql="INSERT INTO registro_mermas  (id_lote, id_inspeccion, cantidad_perdida, motivo, usuario_registro) VALUES (?, ?, ?, ?, ?)";
        String updateSql = """
                            UPDATE lotes_inventario
                            SET cantidad = cantidad - ?
                            WHERE id_lote = ? AND cantidad >= ?
                            """;
        try(Connection conn= DatabaseConnection.getConnection();){
            conn.setAutoCommit(false);
            
            try(PreparedStatement ps= conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psupdateaql= conn.prepareStatement(updateSql)){
                ps.setInt(1, waste.getLotId());
                ps.setObject(2, waste.getInspectionId());
                ps.setBigDecimal(3, waste.getLostQuantity());
                ps.setString(4, waste.getReason());
                ps.setString(5, waste.getUser());
                
                psupdateaql.setBigDecimal(1, waste.getLostQuantity());
                psupdateaql.setInt(2, waste.getLotId());
                psupdateaql.setBigDecimal(3, waste.getLostQuantity());
                
                int updatedRows = psupdateaql.executeUpdate();

                if (updatedRows != 1) {
                    throw new SQLException("Insufficient stock or inventory lot not found");
                }
                
                int insertedRows = ps.executeUpdate();

                if (insertedRows != 1) {
                    throw new SQLException("Failed to register inventory waste");
                }
                
                int generatedId = -1;

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
                if (generatedId <= 0) {
                    throw new SQLException("Failed to retrieve waste ID");
                }
                conn.commit();
                return generatedId;
            }catch (SQLException e) {
            conn.rollback();
            throw e;
        }
        }
    }
}

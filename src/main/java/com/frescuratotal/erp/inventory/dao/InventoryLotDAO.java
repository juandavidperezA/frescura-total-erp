/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.dao;

import com.frescuratotal.erp.config.DatabaseConnection;
import com.frescuratotal.erp.inventory.model.InventoryLot;
import com.frescuratotal.erp.inventory.validation.InventoryLotValidator;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author USUARIO
 */
public class InventoryLotDAO {
    public int insertLot(InventoryLot lot)throws SQLException{
        InventoryLotValidator.validateForInsert(lot);
        String sql="""
                   INSERT INTO lotes_inventario (
                       numero_lote,
                       id_producto,
                       id_proveedor,
                       fecha_vencimiento,
                       cantidad_inicial,
                       cantidad,
                       precio_compra,
                       precio_venta,
                       pasillo,
                       estante,
                       id_estado
                   )
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                   """;
        
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, lot.getLotNumber());
            ps.setInt(2, lot.getProductId());
            ps.setInt(3, lot.getSupplierId());
            ps.setDate(4, Date.valueOf(lot.getExpirationDate()));
            ps.setBigDecimal(5, lot.getInitialQuantity());
            ps.setBigDecimal(6, lot.getQuantity());
            ps.setBigDecimal(7, lot.getPurchasePrice());
            ps.setBigDecimal(8, lot.getSalePrice());
            ps.setString(9, lot.getAisle());
            ps.setString(10, lot.getShelf());
            ps.setInt(11, lot.getStatusId());
            
            ps.executeUpdate();
            
            try(ResultSet rs= ps.getGeneratedKeys()){
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.dao;

import com.frescuratotal.erp.config.DatabaseConnection;
import com.frescuratotal.erp.inventory.model.InventoryLot;
import com.frescuratotal.erp.inventory.validation.InventoryLotValidator;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author USUARIO
 */
public class InventoryLotDAO {
    public int insertLot(InventoryLot lot)throws SQLException{
        InventoryLotValidator.validateForInsert(lot);
        String generatedLotNumber = "LOT-" + UUID.randomUUID();
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
            ps.setString(1, generatedLotNumber);
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
    
    public InventoryLot findById(int id)throws SQLException{
        if(id<=0){
            throw new SQLException("Lot ID must be positive");
        }
        
        String sql="SELECT * FROM lotes_inventario WHERE id_lote= ?";
        
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)){
            ps.setInt(1, id);
            
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    return mapInventoryLot(rs);
                }
            }
        }
        return null;
    }
    
    public List<InventoryLot> findAll()throws SQLException{
        String sql="SELECT * FROM lotes_inventario ORDER BY id_lote";
        
        List<InventoryLot> lots = new ArrayList<>();
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)){
            
            try(ResultSet rs= ps.executeQuery()){
                while(rs.next()){
                    InventoryLot lot= mapInventoryLot(rs);
                    lots.add(lot);
                }
            }
        }return lots;
    }
    
        public List<InventoryLot> findByProductId(int productId) throws SQLException{
        if(productId<=0){
            throw new SQLException("Product ID must be positive");
        }
        
        String sql="SELECT * FROM lotes_inventario WHERE id_producto=? ORDER BY fecha_vencimiento ASC";
        List<InventoryLot> lots= new ArrayList<>();
        
        try(Connection conn= DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)){
            ps.setInt(1, productId);
            
            try(ResultSet rs= ps.executeQuery()){
                while(rs.next()){
                    InventoryLot lot = mapInventoryLot(rs);
                    lots.add(lot);
                }
            }
        }return lots;
    }

        public int updateExpirationStatuses()throws SQLException{
            String sql= """
                        UPDATE lotes_inventario
                             SET id_estado = CASE
                                 WHEN fecha_vencimiento < CURDATE() THEN 3
                                 WHEN fecha_vencimiento <= DATE_ADD(CURDATE(), INTERVAL 7 DAY) THEN 2
                                 ELSE 1
                             END
                             WHERE id_estado <> 4
                               AND id_estado <> CASE
                                   WHEN fecha_vencimiento < CURDATE() THEN 3
                                   WHEN fecha_vencimiento <= DATE_ADD(CURDATE(), INTERVAL 7 DAY) THEN 2
                                   ELSE 1
                             END
                        """;
            
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps= conn.prepareStatement(sql)){
                int affectedRows= ps.executeUpdate();
                return affectedRows;
            }
        }
        
        public int decreaseQuantity(int lotId,BigDecimal quantity)throws SQLException{
            if(lotId<=0){
                throw new SQLException("Lot ID must be positive");
            }
            
            if(quantity==null || quantity.compareTo(BigDecimal.ZERO)<=0){
                throw new SQLException("Quantity must be greater than zero");
            }
            String sql="""
                       UPDATE lotes_inventario
                       SET cantidad = cantidad - ?
                       WHERE id_lote = ?
                         AND cantidad >= ?
                       """;
            
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps= conn.prepareStatement(sql)){
                ps.setBigDecimal(1, quantity);
                ps.setInt(2, lotId);
                ps.setBigDecimal(3, quantity);
                
                int affectedRows= ps.executeUpdate();
                return affectedRows;
            }
        }
        
    private InventoryLot mapInventoryLot(ResultSet rs)throws SQLException{
                    int lotId = rs.getInt("id_lote");
                    String lotNumber = rs.getString("numero_lote");
                    int productId = rs.getInt("id_producto");
                    int supplierId = rs.getInt("id_proveedor");
                    LocalDateTime entryDate = rs.getTimestamp("fecha_ingreso").toLocalDateTime();
                    LocalDate expirationDate= rs.getDate("fecha_vencimiento").toLocalDate();
                    BigDecimal initialQuantity= rs.getBigDecimal("cantidad_inicial");
                    BigDecimal quantity = rs.getBigDecimal("cantidad");
                    BigDecimal purchasePrice = rs.getBigDecimal("precio_compra");
                    BigDecimal salePrice = rs.getBigDecimal("precio_venta");
                    String aisle = rs.getString("pasillo");
                    String shelf = rs.getString("estante");
                    int statusId= rs.getInt("id_estado");
                    
                    InventoryLot lot= new InventoryLot(lotId, lotNumber, productId, supplierId, entryDate, expirationDate, initialQuantity, quantity, purchasePrice, salePrice, aisle, shelf, statusId);
                    return lot;
    }
}

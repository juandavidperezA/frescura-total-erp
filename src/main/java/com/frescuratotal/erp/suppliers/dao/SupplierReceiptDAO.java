/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.dao;

import com.frescuratotal.erp.config.DatabaseConnection;
import com.frescuratotal.erp.suppliers.model.SupplierReceiptDetail;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class SupplierReceiptDAO {
    public List<SupplierReceiptDetail> findReceiptsBySupplierId(int supplierId)throws SQLException{
        if(supplierId<=0){
            throw new SQLException("Supplier ID must be positive");
        }
        
        String sql="""
                SELECT
                    l.numero_lote,
                    p.nombre AS product_name,
                    l.cantidad_inicial,
                    l.precio_compra,
                    l.fecha_ingreso
                    FROM lotes_inventario l
                    INNER JOIN productos p
                    ON p.id_producto = l.id_producto
                    WHERE l.id_proveedor = ?
                    ORDER BY l.fecha_ingreso DESC, l.id_lote DESC
                    """;
        
        List<SupplierReceiptDetail> receipts = new ArrayList<>();
        
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)){
            ps.setInt(1, supplierId);
            
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
                String lotNumber = rs.getString("numero_lote");
                String productName= rs.getString("product_name");
                BigDecimal initialQuantity= rs.getBigDecimal("cantidad_inicial");
                BigDecimal purchasePrice = rs.getBigDecimal("precio_compra");
                LocalDateTime entryDate=rs.getObject("fecha_ingreso", LocalDateTime.class);
                SupplierReceiptDetail receipt = new SupplierReceiptDetail(lotNumber, productName, initialQuantity, purchasePrice, entryDate);
                receipts.add(receipt);
                
            }
        }
        return receipts;
    }
}

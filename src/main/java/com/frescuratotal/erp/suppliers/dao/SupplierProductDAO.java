/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.dao;

import com.frescuratotal.erp.config.DatabaseConnection;
import com.frescuratotal.erp.suppliers.model.SupplierProduct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class SupplierProductDAO {
    public boolean insertSupplierProduct(SupplierProduct supplierproducts)throws SQLException{
        if(supplierproducts ==null){
            throw new SQLException("Supplier product cannot be null");
        }

        if(supplierproducts.getProductId()<=0 || supplierproducts.getSupplierId()<=0){
            throw new SQLException("Product ID and Supplier ID must be positive");
        }

        String sql="INSERT INTO productos_proveedor (id_proveedor, id_producto) VALUES (?,?)";

        try(Connection conn= DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, supplierproducts.getSupplierId());
            ps.setInt(2, supplierproducts.getProductId());

            int affectedRows = ps.executeUpdate();

            if(affectedRows >0){
                return true;
            }else{
                return false;
            }
        }
    }

    public List<SupplierProduct> findProductsBySupplierId(int supplierId) throws SQLException {

        if (supplierId <= 0) {
            throw new SQLException("Supplier ID must be positive");
        }

        String sql="SELECT id_proveedor, id_producto FROM productos_proveedor WHERE id_proveedor= ? ";

        List<SupplierProduct> supplierProducts = new ArrayList<>();

        try(Connection conn= DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)){
            ps.setInt(1, supplierId);

            ResultSet rs=ps.executeQuery();
           while(rs.next()){
               int supplierIdResult = rs.getInt("id_proveedor");
               int productIdResult = rs.getInt("id_producto");
               SupplierProduct supplierProduct = new SupplierProduct(supplierIdResult, productIdResult);
               supplierProducts.add(supplierProduct);
           }
        }
        return supplierProducts;
    }

    public List<SupplierProduct> findSuppliersByProductId(int productId)throws SQLException{
            if (productId <= 0) {
                throw new SQLException("Product ID must be positive");
            }

        String sql="SELECT id_proveedor, id_producto FROM productos_proveedor WHERE id_producto= ? ";

        List<SupplierProduct> supplierProducts = new ArrayList<>();

        try(Connection conn= DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)){
            ps.setInt(1, productId);

            ResultSet rs= ps.executeQuery();
            while(rs.next()){
               int supplierIdResult = rs.getInt("id_proveedor");
               int productIdResult = rs.getInt("id_producto");
               SupplierProduct supplierProduct = new SupplierProduct(supplierIdResult, productIdResult);
               supplierProducts.add(supplierProduct);
            }
        }
        return supplierProducts;
    }
}

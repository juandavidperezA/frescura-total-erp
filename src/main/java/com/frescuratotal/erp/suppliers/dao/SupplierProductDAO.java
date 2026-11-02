/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.dao;

import com.frescuratotal.erp.config.DatabaseConnection;
import com.frescuratotal.erp.suppliers.model.SupplierProduct;
import com.frescuratotal.erp.suppliers.model.SupplierProductDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public List<SupplierProductDetail> findProductsBySupplierId(int supplierId) throws SQLException {

        if (supplierId <= 0) {
            throw new SQLException("Supplier ID must be positive");
        }

        String sql="""
                   SELECT
                       pp.id_proveedor,
                       s.nombre AS supplier_name,
                       pp.id_producto,
                       p.nombre AS product_name
                   FROM productos_proveedor pp
                   INNER JOIN proveedores s
                       ON pp.id_proveedor = s.id_proveedor
                   INNER JOIN productos p
                       ON pp.id_producto = p.id_producto
                   WHERE pp.id_proveedor = ?;
                   """;

        List<SupplierProductDetail> supplierProducts = new ArrayList<>();

        try(Connection conn= DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)){
            ps.setInt(1, supplierId);

            ResultSet rs=ps.executeQuery();
           while(rs.next()){
               int supplierIdResult = rs.getInt("id_proveedor");
               String supplierNameResult= rs.getString("supplier_name");
               int productIdResult = rs.getInt("id_producto");
               String productNameResult= rs.getString("product_name");
               SupplierProductDetail supplierProduct = new SupplierProductDetail(supplierIdResult, supplierNameResult, productIdResult, productNameResult);
               supplierProducts.add(supplierProduct);
           }
        }
        return supplierProducts;
    }

    public List<SupplierProductDetail> findSuppliersByProductId(int productId)throws SQLException{
            if (productId <= 0) {
                throw new SQLException("Product ID must be positive");
            }

        String sql="""
                   SELECT
                       pp.id_proveedor,
                       s.nombre AS supplier_name,
                       pp.id_producto,
                       p.nombre AS product_name
                   FROM productos_proveedor pp
                   INNER JOIN proveedores s
                       ON pp.id_proveedor = s.id_proveedor
                   INNER JOIN productos p
                       ON pp.id_producto = p.id_producto
                   WHERE pp.id_producto = ?;
                   """;

        List<SupplierProductDetail> supplierProducts = new ArrayList<>();

        try(Connection conn= DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)){
            ps.setInt(1, productId);

            ResultSet rs= ps.executeQuery();
            while(rs.next()){
               int supplierIdResult = rs.getInt("id_proveedor");
               String supplierNammeResult= rs.getString("supplier_name");
               int productIdResult = rs.getInt("id_producto");
               String productNameResult  = rs.getString("product_name");
               SupplierProductDetail supplierProduct = new SupplierProductDetail(supplierIdResult, supplierNammeResult, productIdResult, productNameResult);
               supplierProducts.add(supplierProduct);
            }
        }
        return supplierProducts;
    }
}

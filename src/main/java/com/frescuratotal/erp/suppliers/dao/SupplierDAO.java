/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.dao;

import com.frescuratotal.erp.config.DatabaseConnection;
import com.frescuratotal.erp.suppliers.model.Supplier;
import com.frescuratotal.erp.suppliers.validation.SupplierValidator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class SupplierDAO {
    public int insertSupplier(Supplier supplier) throws SQLException{
        SupplierValidator.validate(supplier);


        String sql="INSERT INTO proveedores (nombre, nit, persona_contacto, email, telefono, direccion) VALUES (?,?,?,?,?,?)";

        try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps= conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getNit());
            ps.setString(3, supplier.getContactPerson());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getPhone());
            ps.setString(6, supplier.getAddress());

            ps.executeUpdate();

            try(ResultSet rs= ps.getGeneratedKeys()){
                return rs.next() ? rs.getInt(1) :-1;
            }

        }
    }

    public Supplier findById(int id)throws SQLException{

        if(id<=0){
            throw new SQLException("Supplier ID must be positive");
        }

        String sql="SELECT * from proveedores where id_proveedor = ?";

        try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps= conn.prepareStatement(sql)){
            ps.setInt(1, id);

            try(ResultSet rs= ps.executeQuery()){
                if(rs.next()){
                    int supplierId= rs.getInt("id_proveedor");
                    String name= rs.getString("nombre");
                    String nit = rs.getString("nit");
                    String contactPerson= rs.getString("persona_contacto");
                    String email= rs.getString("email");
                    String phone= rs.getString("telefono");
                    String address= rs.getString("direccion");
                    boolean active= rs.getBoolean("activo");
                    LocalDateTime registrationDate= rs.getObject("fecha_registro", LocalDateTime.class);

                    Supplier supplier = new Supplier(supplierId, name, nit, contactPerson, email, phone, address, active, registrationDate);

                    return supplier;

                }
            }
        }return null;
    }

    public List<Supplier> findAll()throws SQLException{
        String sql="SELECT * FROM proveedores";

        List<Supplier> sup = new ArrayList<>();

        try(Connection conn= DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)){


            try(ResultSet rs= ps.executeQuery()){
                while(rs.next()){

                    int supplierId= rs.getInt("id_proveedor");
                    String name= rs.getString("nombre");
                    String nit = rs.getString("nit");
                    String contactPerson= rs.getString("persona_contacto");
                    String email= rs.getString("email");
                    String phone= rs.getString("telefono");
                    String address= rs.getString("direccion");
                    boolean active= rs.getBoolean("activo");
                    LocalDateTime registrationDate= rs.getObject("fecha_registro", LocalDateTime.class);

                    Supplier supplier = new Supplier(supplierId, name, nit, contactPerson, email, phone, address, active, registrationDate);

                    sup.add(supplier);
                }
            }
            return sup;
        }
    }
    public int updateSupplier(Supplier supplier) throws SQLException{
        SupplierValidator.validate(supplier);
        if (supplier.getId() <= 0) {
            throw new SQLException("Supplier ID must be positive");
        }

        String sql="UPDATE proveedores set nombre=?,nit=?, persona_contacto=?,email=?,telefono =?, direccion=?  where id_proveedor = ?";

        try(Connection conn= DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getNit());
            ps.setString(3, supplier.getContactPerson());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getPhone());
            ps.setString(6, supplier.getAddress());
            ps.setInt(7, supplier.getId());

            int affectedRows = ps.executeUpdate();
            return affectedRows;
        }
    }
}

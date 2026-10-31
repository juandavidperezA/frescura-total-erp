/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.controller;

import com.frescuratotal.erp.suppliers.dao.SupplierDAO;
import com.frescuratotal.erp.suppliers.model.Supplier;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class SupplierController {
    private SupplierDAO dao;
    
    public SupplierController(SupplierDAO pDao){
        dao=pDao;
    }
    
    public Supplier findSupplierById(int id)throws SQLException{
        Supplier sup = dao.findById(id);
        return sup;
    }
    
    public List<Supplier> findAllSuppliers()throws SQLException{
        List<Supplier> supid = dao.findAll();
        return supid;
    }
    
    public int registerSupplier(Supplier supplier)throws SQLException{
        int supplierID= dao.insertSupplier(supplier);
        return supplierID;
    }
    
    public int updateSupplier(Supplier sup)throws SQLException{
        int supplirupdate= dao.updateSupplier(sup);
        return supplirupdate;
    }
    
    public int setSupplierActive(int id, boolean active) throws SQLException{
        int activesupplier= dao.setSupplierActive(id, active);
        return activesupplier;
    }
}

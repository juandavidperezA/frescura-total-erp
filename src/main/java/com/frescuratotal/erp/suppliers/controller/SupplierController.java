/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.controller;

import com.frescuratotal.erp.suppliers.dao.SupplierDAO;
import com.frescuratotal.erp.suppliers.dao.SupplierProductDAO;
import com.frescuratotal.erp.suppliers.model.Supplier;
import com.frescuratotal.erp.suppliers.model.SupplierProduct;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class SupplierController {
    private SupplierDAO dao;
    private SupplierProductDAO supplierProductDAO ;
    
    public SupplierController(SupplierDAO pDao, SupplierProductDAO pSupplierProductDAO){
        dao=pDao;
        supplierProductDAO= pSupplierProductDAO;
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

    public boolean associateSupplierProduct(SupplierProduct supplierProduct)throws SQLException{
        boolean inserted= supplierProductDAO.insertSupplierProduct(supplierProduct);
        return inserted;
    }

    public List<SupplierProduct> findProductsBySupplierId(int supplierId)throws SQLException{
        List<SupplierProduct> supplierProducts= supplierProductDAO.findProductsBySupplierId(supplierId);
        return supplierProducts;
    }

    public List<SupplierProduct> findSuppliersByProductId(int productId)throws SQLException{
        List<SupplierProduct> supplierProducts= supplierProductDAO.findSuppliersByProductId(productId);
        return supplierProducts;
    }
}

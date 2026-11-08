/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.model;

/**
 *
 * @author USUARIO
 */
public class SupplierProductDetail {
    // tables =productos_proveedor, proveedores, productos
    private final int supplierId;
    private final String supplierName;
    private final int productId;
    private final String productName;

    public SupplierProductDetail(int pSupplierId, String pSupplierName,int pProductId, String pProductName){
        supplierId=pSupplierId;
        supplierName= pSupplierName;
        productId=pProductId;
        productName=pProductName;

    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }
}

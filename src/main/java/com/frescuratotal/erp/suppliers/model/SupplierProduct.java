/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.model;

/**
 *
 * @author USUARIO
 */
public class SupplierProduct {
    private final int productId;
    private final int supplierId;

    public SupplierProduct(int supplierId, int productId){
         this.supplierId=supplierId;
         this.productId=productId;
    }

    public int getProductId() {
        return productId;
    }

    public int getSupplierId() {
        return supplierId;
    }
}

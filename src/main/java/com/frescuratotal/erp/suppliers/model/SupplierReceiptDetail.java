/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author USUARIO
 */
public class SupplierReceiptDetail {
    private final String lotNumber;
    private final String productName;
    private final BigDecimal initialQuantity;
    private final BigDecimal purchasePrice;
    private final LocalDateTime entryDate;

    public SupplierReceiptDetail(String lotNumber, String productName, BigDecimal initialQuantity, BigDecimal purchasePrice, LocalDateTime entryDate) {
        this.lotNumber = lotNumber;
        this.productName = productName;
        this.initialQuantity = initialQuantity;
        this.purchasePrice = purchasePrice;
        this.entryDate = entryDate;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getInitialQuantity() {
        return initialQuantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }
    
    
}

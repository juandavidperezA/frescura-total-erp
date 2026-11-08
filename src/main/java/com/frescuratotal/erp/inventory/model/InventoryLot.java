/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author USUARIO
 */
public class InventoryLot {
    //table= lotes_inventario
    private int id;
    private String lotNumber;
    private int productId;
    private int supplierId;
    private LocalDateTime entryDate;
    private LocalDate expirationDate;
    private BigDecimal initialQuantity;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private String aisle;
    private String shelf;
    private int statusId;

    public InventoryLot(String lotNumber, int productId, int supplierId, LocalDate expirationDate, BigDecimal initialQuantity, BigDecimal purchasePrice, BigDecimal salePrice, String aisle, String shelf, int statusId) {
        this.lotNumber = lotNumber;
        this.productId = productId;
        this.supplierId = supplierId;
        this.expirationDate = expirationDate;
        this.initialQuantity = initialQuantity;
        
        this.quantity=initialQuantity;
        
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.aisle = aisle;
        this.shelf = shelf;
        this.statusId = statusId;
    }

    public InventoryLot(int id, String lotNumber, int productId, int supplierId, LocalDateTime entryDate, LocalDate expirationDate, BigDecimal initialQuantity, BigDecimal quantity, BigDecimal purchasePrice, BigDecimal salePrice, String aisle, String shelf, int statusId) {
        this.id = id;
        this.lotNumber = lotNumber;
        this.productId = productId;
        this.supplierId = supplierId;
        this.entryDate = entryDate;
        this.expirationDate = expirationDate;
        this.initialQuantity = initialQuantity;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.aisle = aisle;
        this.shelf = shelf;
        this.statusId = statusId;
    }

    public int getId() {
        return id;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public int getProductId() {
        return productId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public BigDecimal getInitialQuantity() {
        return initialQuantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public String getAisle() {
        return aisle;
    }

    public String getShelf() {
        return shelf;
    }

    public int getStatusId() {
        return statusId;
    }
    
    
}

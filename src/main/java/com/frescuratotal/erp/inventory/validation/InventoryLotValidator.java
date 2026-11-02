/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.validation;

import com.frescuratotal.erp.inventory.model.InventoryLot;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 *
 * @author USUARIO
 */
public class InventoryLotValidator {
    public static void validateForInsert(InventoryLot lot) throws SQLException{
        if(lot ==null){
            throw new SQLException("Inventory lot cannot be null");
        }
        
        if(lot.getLotNumber() ==null || lot.getLotNumber().isBlank()){
            throw new SQLException("Lot number cannot be null or blank");
        }
        
        if(lot.getLotNumber().length()>50){
            throw new SQLException("Lot number cannot exceed 50 characters");
        }
        
        if(lot.getProductId()<=0 || lot.getSupplierId()<=0 || lot.getStatusId()<=0){
            throw new SQLException("Product, supplier and status IDs must be positive");
        }
        
        if(lot.getExpirationDate()==null || lot.getExpirationDate().isBefore(LocalDate.now())){
            throw new SQLException("Expiration date cannot be null or in the past");
        }
        
        if(lot.getInitialQuantity()==null || lot.getInitialQuantity().compareTo(BigDecimal.ZERO) <=0){
            throw new SQLException("Initial quantity must be greater than zero");
        }
        
        if(lot.getQuantity()==null || lot.getQuantity().compareTo(lot.getInitialQuantity())!=0){
            throw new SQLException("Quantity must equal initial quantity when registering a lot");
        }
        
        if(lot.getPurchasePrice()==null || lot.getPurchasePrice().compareTo(BigDecimal.ZERO)<=0){
            throw new SQLException("Purchase price must be greater than zero");
        }
        
        if(lot.getSalePrice()==null || lot.getSalePrice().compareTo(BigDecimal.ZERO)<=0){
            throw new SQLException("Sale price must be greater than zero");
        }
        
        if(lot.getAisle() !=null && lot.getAisle().length()>50){
            throw new SQLException("Aisle cannot exceed 50 characters");
        }
        
        if(lot.getShelf()!=null && lot.getShelf().length()>50){
            throw new SQLException("Shelf cannot exceed 50 characters");
        }
        
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.validation;

import com.frescuratotal.erp.suppliers.model.Supplier;
import java.sql.SQLException;

/**
 *
 * @author USUARIO
 */
public class SupplierValidator {
     public static void validate(Supplier supplier) throws SQLException {

        if (supplier == null) {
            throw new SQLException("Supplier cannot be null");
        }

        if (supplier.getName() == null || supplier.getName().isBlank()) {
            throw new SQLException("Supplier name cannot be empty");
        }

        if (supplier.getName().length() > 255) {
            throw new SQLException("Supplier name cannot exceed 255 characters");
        }

        if (supplier.getNit() == null || supplier.getNit().isBlank()) {
            throw new SQLException("Supplier NIT cannot be empty");
        }

        if (supplier.getNit().length() > 20) {
            throw new SQLException("Supplier NIT cannot exceed 20 characters");
        }

        if (supplier.getEmail() == null || supplier.getEmail().isBlank()) {
            throw new SQLException("Supplier email cannot be empty");
        }

        if (supplier.getEmail().length() > 100) {
            throw new SQLException("Supplier email cannot exceed 100 characters");
        }

        if (supplier.getPhone() != null && supplier.getPhone().length() > 20) {
            throw new SQLException("Supplier phone cannot exceed 20 characters");
        }

        if (supplier.getContactPerson() != null
                && supplier.getContactPerson().length() > 100) {
            throw new SQLException("Supplier contact person cannot exceed 100 characters");
        }
     }
}

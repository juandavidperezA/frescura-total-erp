 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.view;

import com.frescuratotal.erp.suppliers.controller.SupplierController;
import com.frescuratotal.erp.suppliers.dao.SupplierProductDAO;
import com.frescuratotal.erp.suppliers.model.Supplier;
import com.frescuratotal.erp.suppliers.model.SupplierProduct;
import com.frescuratotal.erp.suppliers.model.SupplierProductDetail;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author USUARIO
 */
public class SupplierView {
    Scanner sc= new Scanner(System.in);
    private SupplierController control;
    
    public SupplierView(SupplierController control){
        this.control=control;
    }
    
    public void showMenu(){
        System.out.println("\n\n----------------------------------------------");
        System.out.println("    Supplier Menu   ");
        System.out.println("1. Register supplier");
        System.out.println("2. List all suppliers");
        System.out.println("3. Find supplier by ID");
        System.out.println("4. Update supplier");
        System.out.println("5. Activate/Deactivate supplier");
        System.out.println("6. Associate product with supplier");
        System.out.println("7. List products by supplier");
        System.out.println("8. List suppliers by product");
        System.out.println("9. Return to main menu");
        System.out.println("Please choose an option: ");
        System.out.println("----------------------------------------------");        
    }
    
    public void start(){
    int option;
    
    do{
    showMenu();
    while(!sc.hasNextInt()){
        System.out.println("invalid option");
        sc.next();
        System.out.println("Please choose an option: ");
    }
    option=sc.nextInt();
    
    
        switch (option) {
            case 1:
                sc.nextLine();
                String name;
                do{
                System.out.println("Enter supplier name");
                name=sc.nextLine();
                
                if(name.trim().isEmpty()){
                    System.out.println("Invalid name. Cannot be empty.");
                }
                }while(name.trim().isEmpty());
                
                String nit;
                do{
                    System.out.println("Enter supplier NIT: ");
                    nit=sc.nextLine();
                    
                    if(nit.isBlank()){
                        System.out.println("Invalid nit. Cannot be empty.");
                    }
                }while(nit.isBlank());

                    System.out.println("Enter contact person (optional): ");
                    String contactPerson=sc.nextLine();
                
                String email;
                do{
                    System.out.println("Enter supplier email: ");
                    email=sc.nextLine();
                    
                    if(email.isBlank()){
                        System.out.println("Invalid Email. Cannot be empty.");
                    }
                }while(email.isBlank());
                

                    System.out.println("Enter supplier phone (optional): ");
                    String phone= sc.nextLine();

                    System.out.println("Enter supplier address: ");
                    String address= sc.nextLine();

                Supplier sup= new Supplier(name, nit, contactPerson, email, phone, address);
                try {
                    int supplierId=control.registerSupplier(sup);
                    System.out.println("Supplier registered with ID: "+ supplierId);
                } catch (SQLException e) {
                    System.out.println("Error registering supplier: "+e.getMessage());
                }
                break;
            case 2:
                try{
                List<Supplier> suppliers = control.findAllSuppliers();
                if(suppliers.isEmpty()){
                    System.out.println("No suppliers registered.");
                }
                for(Supplier supplierss: suppliers){
                    System.out.println("ID: "+supplierss.getId()+"| name: "+supplierss.getName());
                }
                }catch(SQLException e){
                    System.out.println("Error listing suppliers: " + e.getMessage());
                }
                break;
                
            case 3:
                System.out.println("Enter supplier ID: ");
                while(!sc.hasNextInt()){
                    System.out.println("ID invalid only numbers");
                    sc.next();
                    System.out.println("Enter supplier ID: ");
                }
                int foundID= sc.nextInt();
                if (foundID <= 0) {
                    System.out.println("Supplier ID must be positive.");
                    break;
                }
                
                try {
                    Supplier sup1= control.findSupplierById(foundID);
                    if (sup1==null){
                        System.out.println("Supplier not found.");
                    }else{
                        System.out.println("ID: "+sup1.getId()+" |  nombre: "+sup1.getName());
                    }
                } catch (SQLException e) {
                    System.out.println("Error finding suppliers: " + e.getMessage());
                }
                break;
                
            case 4:
                System.out.println("Enter supplier ID to update: ");
                while(!sc.hasNextInt()){
                    System.out.println("Invalid ID. Only numbers.");
                    sc.next();
                    System.out.println("Enter supplier ID to update: ");
                }
                int updateId=sc.nextInt();
                sc.nextLine();
                
                if(updateId<=0){
                    System.out.println("Supplier ID must be positive.");
                    break;
                }
                
                try {
                    Supplier existingSupplier = control.findSupplierById(updateId);
                    
                    if(existingSupplier==null){
                        System.out.println("Supplier not found.");
                        break;
                    }
                    System.out.println("Updating supplier: " + existingSupplier.getName());
                    
                    System.out.println("New name ["+existingSupplier.getName()+"]:");
                    String newName= sc.nextLine();
                    
                    if(newName.isBlank()){
                        newName= existingSupplier.getName();
                    }
                    
                    System.out.println("New email ["+existingSupplier.getEmail()+"]: ");
                    String newEmail=sc.nextLine();
                    
                    if(newEmail.isBlank()){
                        newEmail=existingSupplier.getEmail();
                    }
                    
                    
                    System.out.println("New nit ["+existingSupplier.getNit()+"]: ");
                    String newNit=sc.nextLine();
                    
                    if(newNit.isBlank()){
                        newNit= existingSupplier.getNit();    
                    }
                    
                    System.out.println("New contact person["+existingSupplier.getContactPerson()+"]: ");
                    String newcontact=sc.nextLine();
                    
                    if(newcontact.isBlank()){
                        newcontact=existingSupplier.getContactPerson();
                    }
                    
                    System.out.println("New phone["+existingSupplier.getPhone()+"]: ");
                    String newPhone=sc.nextLine();
                    
                    if(newPhone.isBlank()){
                        newPhone=existingSupplier.getPhone();
                    }
                    
                    System.out.println("New address["+existingSupplier.getAddress()+"]: ");
                    String newAddress= sc.nextLine();
                    
                    if(newAddress.isBlank()){
                        newAddress=existingSupplier.getAddress();
                    }
                    
                    Supplier updatedSupplier=new Supplier(updateId, newName, newNit, newcontact, newEmail, newPhone, newAddress, existingSupplier.isActive(), existingSupplier.getRegistrationDate());
                    int affectedRows = control.updateSupplier(updatedSupplier);
                    
                    if(affectedRows>0){
                        System.out.println("Supplier updated successfully.");
                    }else{
                        System.out.println("Supplier was not updated.");
                    }
                    
                } catch (SQLException e) {
                    System.out.println("Error updating supplier: " + e.getMessage());
                }
                break;
                
            case 5:
                System.out.println("Enter supplier ID: ");

                while (!sc.hasNextInt()) {
                    System.out.println("Invalid ID. Only numbers.");
                    sc.next();
                    System.out.println("Enter supplier ID: ");
                }

                int supplierId = sc.nextInt();
                sc.nextLine();

                if (supplierId <= 0) {
                    System.out.println("Supplier ID must be positive.");
                    break;
                }

                try {
                    Supplier existingSupplier = control.findSupplierById(supplierId);

                    if (existingSupplier == null) {
                        System.out.println("Supplier not found.");
                        break;
                    }

                    System.out.println("Current status: "+(existingSupplier.isActive() ? "active" : "inactive"));
                    
                    System.out.println("1. Activate supplier");
                    System.out.println("2. Deactivate supplier");
                    System.out.println("choose an option: ");
                    while(!sc.hasNextInt()){
                        System.out.println("Invalid option. Only numbers.");
                        sc.next();
                        System.out.println("choose an option: ");
                    }
                    int statusOption=sc.nextInt();
                    
                    
                    int affectedRow=0;
                    switch (statusOption) {
                        case 1:
                            affectedRow=control.setSupplierActive(supplierId, true);
                            break;
                        case 2:
                            affectedRow=control.setSupplierActive(supplierId, false);
                            break;
                        default:
                            System.out.println("Invalid status option.");
                            break;
                    }
                    
                    if(statusOption==1 || statusOption==2){
                        if(affectedRow>0){
                        System.out.println("Supplier status updated successfully.");
                    }else{
                        System.out.println("Supplier status was not updated.");
                    }
                    }

                } catch (SQLException e) {
                        System.out.println("Error changing supplier status: " + e.getMessage());
                   }                
                break;
            case 6:
                System.out.println("Enter supplier ID: ");
                while(!sc.hasNextInt()){
                    System.out.println("Invalid ID. Only numbers.");
                    sc.next();
                    System.out.println("Enter supplier ID: ");
                }
                int associationSupplierId =sc.nextInt();
                if(associationSupplierId <=0){
                    System.out.println("ID must be positive.");
                    break;
                }

                System.out.println("Enter product ID: ");
                while(!sc.hasNextInt()){
                    System.out.println("Invalid ID. Only numbers.");
                    sc.next();
                    System.out.println("Enter product ID: ");
                }
                int associationProductId =sc.nextInt();
                if(associationProductId <=0){
                    System.out.println("ID must be positive.");
                    break;
                }
                SupplierProduct supplierProduct= new SupplierProduct(associationSupplierId, associationProductId);
                try {
                    boolean inserted = control.associateSupplierProduct(supplierProduct);
                        if (inserted) {
                            System.out.println("Product associated successfully.");
                        } else {
                            System.out.println("Product could not be associated.");
                        }
                } catch (SQLException e) {
                    System.out.println("error: "+e.getMessage());
                }
                break;
            case 7:
                System.out.println("Enter supplier ID:");

                while (!sc.hasNextInt()) {
                    System.out.println("Invalid ID. Only numbers.");
                    sc.next();
                    System.out.println("Enter supplier ID:");
                }

                int searchSupplierId = sc.nextInt();

                if (searchSupplierId <= 0) {
                    System.out.println("Supplier ID must be positive.");
                    break;
                }

                try {
                    List<SupplierProductDetail> supplierProducts = control.findProductsBySupplierId(searchSupplierId);
                    if(supplierProducts.isEmpty()){
                        System.out.println("No products associated with this supplier.");
                        break;
                    }
                    for(SupplierProductDetail supplierpro: supplierProducts){
                        System.out.println("Product ID: "+supplierpro.getProductId()+" | Product name: "+supplierpro.getProductName());
                    }
                } catch (SQLException e) {
                    System.out.println("error listing supplier products: "+e.getMessage());
                }

                break;
            case 8:
                System.out.println("Enter product ID:");
                while(!sc.hasNextInt()){
                    System.out.println("Invalid ID. Only numbers.");
                    sc.next();
                    System.out.println("Enter product ID:");
                }
                int searchProductId= sc.nextInt();

                if(searchProductId<=0){
                    System.out.println("Product ID must be positive.");
                    break;
                }
                try {
                    List<SupplierProductDetail> productsSupplier= control.findSuppliersByProductId(searchProductId);
                    if(productsSupplier.isEmpty()){
                        System.out.println("No supplier associated with this product");
                        break;
                    }
                    for(SupplierProductDetail proSupplier : productsSupplier){
                        System.out.println("Supplier ID: "+ proSupplier.getSupplierId()+" | Supplier name: "+proSupplier.getSupplierName());
                    }
                } catch (SQLException e) {
                    System.out.println("error listing product suppliers: "+e.getMessage());
                }
                break;
            case 9:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }while(option!=9);
    }
    
}
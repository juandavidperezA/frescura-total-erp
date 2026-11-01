 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.view;

import com.frescuratotal.erp.suppliers.controller.SupplierController;
import com.frescuratotal.erp.suppliers.model.Supplier;
import java.sql.SQLException;
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
        System.out.println("----------------------------------------------");
        System.out.println("    Supplier Menu   ");
        System.out.println("1. Register supplier");
        System.out.println("2. List all suppliers");
        System.out.println("3. Find supplier by ID");
        System.out.println("4. Update supplier");
        System.out.println("5. Activate/Deactivate supplier");
        System.out.println("6. Return to main menu");
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
            case 6:
                System.out.println("Returning to main menu...");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }while(option!=6);
    }
    
}
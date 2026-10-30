/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.suppliers.model;

import java.time.LocalDateTime;

/**
 *
 * @author USUARIO
 */
public class Supplier {
    private int id;
    private String name;
    private String nit;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private boolean active;
    private LocalDateTime registrationDate;
    
    public Supplier(String pName, String pNit, String pContactPerson,
    String pEmail, String pPhone, String pAddress){
        name=pName;
        nit= pNit;
        contactPerson=pContactPerson;
        email=pEmail;
        phone=pPhone;
        address= pAddress;
        this.active=true;
    }

    public Supplier(int id, String name, String nit, String contactPerson, String email, String phone, String address, boolean active, LocalDateTime registrationDate) {
        this.id = id;
        this.name = name;
        this.nit = nit;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.active = active;
        this.registrationDate = registrationDate;
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNit() {
        return nit;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setName(String name){
        this.name=name;
    }
    
    public void setPhone(String phone){
        this.phone=phone;
    }
    
    public void setContactPerson(String contactPerson){
        this.contactPerson=contactPerson;
    }
    
    public void setEmail(String email){
        this.email=email;
    }
    
    public void setAddress(String address){
        this.address=address;
    }
    
    public void setActive(boolean active){
        this.active=active;
    }
    
    public void setNit(String nit){
        this.nit=nit;
    }
}

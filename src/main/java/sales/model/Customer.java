
package com.frescuratotal.erp.sales.model;

import java.time.LocalDateTime;

public class Customer {

    private int idCliente;
    private String nombre;
    private String documento;
    private String email;
    private String telefono;
    private String direccion;
    private int idTipo;
    private LocalDateTime fechaRegistro;

    public Customer() {
    }

    public Customer(int idCliente, String nombre, String documento,
                    String email, String telefono, String direccion,
                    int idTipo, LocalDateTime fechaRegistro) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.documento = documento;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.idTipo = idTipo;
        this.fechaRegistro = fechaRegistro;
    }

    public Customer(String nombre, String documento, String email,
                    String telefono, String direccion, int idTipo) {
        this.nombre = nombre;
        this.documento = documento;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.idTipo = idTipo;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "Customer{"
                + "idCliente=" + idCliente
                + ", nombre='" + nombre + '\''
                + ", documento='" + documento + '\''
                + ", email='" + email + '\''
                + ", telefono='" + telefono + '\''
                + ", direccion='" + direccion + '\''
                + ", idTipo=" + idTipo
                + ", fechaRegistro=" + fechaRegistro
                + '}';
    }
}

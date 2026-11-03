
package sales.model;

import java.time.LocalDate;


public class Customer {
    
    private int Id;
    private String nombre;
    private String documento;
    private String email;
    private String telefono;
    private String direccion;
    private int idTipo;
    private LocalDate fechaRegistro;

    public Customer(int Id, String nombre, String documento, String email, String telefono, String direccion, int idTipo, LocalDate fechaRegistro) {
        this.Id = Id;
        this.nombre = nombre;
        this.documento = documento;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.idTipo = idTipo;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() {
        return Id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    
    
}

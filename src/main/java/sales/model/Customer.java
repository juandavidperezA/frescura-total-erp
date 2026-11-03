package sales.model;


import java.time.LocalDateTime;

public class Customer {

    private int id;
    private String name;
    private String document;
    private String email;
    private String phone;
    private String address;
    private int typeId;
    private LocalDateTime registrationDate;

    public Customer(int id, String name, String document, String email,
                    String phone, String address, int typeId,
                    LocalDateTime registrationDate) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.typeId = typeId;
        this.registrationDate = registrationDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDocument() {
        return document;
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

    public int getTypeId() {
        return typeId;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
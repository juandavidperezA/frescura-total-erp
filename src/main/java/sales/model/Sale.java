
package sales.model;

import java.time.LocalDateTime;


public class Sale {
    //table= ventas
    private int id;
    private String saleNumber;
    private int customerId;
    private LocalDateTime saleDate;
    private double subtotal;
    private double totalDiscount;
    private double totalTax;
    private double totalAmount;
    private int statusId;
    private String registeredBy;

    public Sale(int id, String saleNumber, int customerId,
                LocalDateTime saleDate, double subtotal,
                double totalDiscount, double totalTax,
                double totalAmount, int statusId, String registeredBy) {
        this.id = id;
        this.saleNumber = saleNumber;
        this.customerId = customerId;
        this.saleDate = saleDate;
        this.subtotal = subtotal;
        this.totalDiscount = totalDiscount;
        this.totalTax = totalTax;
        this.totalAmount = totalAmount;
        this.statusId = statusId;
        this.registeredBy = registeredBy;
    }

    public int getId() {
        return id;
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getStatusId() {
        return statusId;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public void setTotalTax(double totalTax) {
        this.totalTax = totalTax;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }
    
    
    
}

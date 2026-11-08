
package sales.model;

import java.time.LocalDateTime;

public class Invoice {
// table= facturas
    private int id;
    private int saleId;
    private String invoiceNumber;
    private String cufe;
    private String qrLink;
    private LocalDateTime issueDate;
    private double subtotal;
    private double totalDiscount;
    private double taxAmount;
    private double totalAmount;

    public Invoice(int saleId, String invoiceNumber, String cufe,
                   String qrLink, double subtotal, double totalDiscount,
                   double taxAmount, double totalAmount) {

        this.saleId = saleId;
        this.invoiceNumber = invoiceNumber;
        this.cufe = cufe;
        this.qrLink = qrLink;
        this.subtotal = subtotal;
        this.totalDiscount = totalDiscount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
    }

    public Invoice(int id, int saleId, String invoiceNumber, String cufe,
                   String qrLink, LocalDateTime issueDate, double subtotal,
                   double totalDiscount, double taxAmount, double totalAmount) {

        this.id = id;
        this.saleId = saleId;
        this.invoiceNumber = invoiceNumber;
        this.cufe = cufe;
        this.qrLink = qrLink;
        this.issueDate = issueDate;
        this.subtotal = subtotal;
        this.totalDiscount = totalDiscount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public int getSaleId() {
        return saleId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getCufe() {
        return cufe;
    }

    public String getQrLink() {
        return qrLink;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}

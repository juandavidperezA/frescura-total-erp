
package sales.model;

public class SaleDetail {
//table=  detalles_venta
    private int id;
    private int saleId;
    private int lotId;
    private double quantitySold;
    private double unitPrice;
    private double discountApplied;
    private double discountAmount;
    private double taxRate;
    private double taxAmount;
    private double subtotal;
    private double lineTotal;

    public SaleDetail(int saleId, int lotId, double quantitySold,
                      double unitPrice, double discountApplied,
                      double discountAmount, double taxRate,
                      double taxAmount, double subtotal, double lineTotal) {
        this.saleId = saleId;
        this.lotId = lotId;
        this.quantitySold = quantitySold;
        this.unitPrice = unitPrice;
        this.discountApplied = discountApplied;
        this.discountAmount = discountAmount;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.subtotal = subtotal;
        this.lineTotal = lineTotal;
    }

    public SaleDetail(int id, int saleId, int lotId, double quantitySold,
                      double unitPrice, double discountApplied,
                      double discountAmount, double taxRate,
                      double taxAmount, double subtotal, double lineTotal) {
        this.id = id;
        this.saleId = saleId;
        this.lotId = lotId;
        this.quantitySold = quantitySold;
        this.unitPrice = unitPrice;
        this.discountApplied = discountApplied;
        this.discountAmount = discountAmount;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.subtotal = subtotal;
        this.lineTotal = lineTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public double getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(double quantitySold) {
        this.quantitySold = quantitySold;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(double discountApplied) {
        this.discountApplied = discountApplied;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }
}

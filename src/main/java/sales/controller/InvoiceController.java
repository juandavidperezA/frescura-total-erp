package sales.controller;

import java.sql.SQLException;
import sales.dao.InvoiceDAO;
import sales.model.Invoice;

public class InvoiceController {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    public Invoice createInvoice(int saleId) throws SQLException {
        return invoiceDAO.createInvoice(saleId);
    }

    public Invoice findInvoiceBySaleId(int saleId) throws SQLException {
        return invoiceDAO.findBySaleId(saleId);
    }
}
package sales.controller;

import java.sql.SQLException;
import java.util.List;
import sales.dao.SaleDAO;
import sales.model.Sale;
import sales.model.SaleDetail;

public class SaleController {

    private final SaleDAO saleDAO = new SaleDAO();

    public Sale findSaleById(int id) throws SQLException {
        return saleDAO.findById(id);
    }

    public List<Sale> findAllSales() throws SQLException {
        return saleDAO.findAll();
    }

    public void findAvailableLotsByProduct(int productId) throws SQLException {
        saleDAO.findAvailableLotsByProduct(productId);
    }

    public int confirmSale(Sale sale, List<SaleDetail> details) throws SQLException {
        return saleDAO.confirmSale(sale, details);
    }

    public int updateSaleStatus(int saleId, int statusId) throws SQLException {
        return saleDAO.updateSaleStatus(saleId, statusId);
    }
}
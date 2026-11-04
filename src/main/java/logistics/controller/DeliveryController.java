package logistics.controller;

import java.sql.SQLException;
import java.util.List;
import logistics.dao.DeliveryDAO;
import sales.model.Sale;

public class DeliveryController {

    private final DeliveryDAO deliveryDAO = new DeliveryDAO();

    public int prepareSale(int saleId) throws SQLException {
        return deliveryDAO.prepareSale(saleId);
    }

    public List<Sale> findSalesInPreparation() throws SQLException {
        return deliveryDAO.findSalesInPreparation();
    }
}
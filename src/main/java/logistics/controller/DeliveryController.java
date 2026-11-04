
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

    public int createRoute(String routeName, String username)
            throws SQLException {
        return deliveryDAO.createRoute(routeName, username);
    }

    public int assignDelivery(int routeId, int vehicleId, int driverId)
            throws SQLException {
        return deliveryDAO.assignDelivery(routeId, vehicleId, driverId);
    }

    public int addSaleToRoute(int routeId, int saleId, int deliveryOrder)
            throws SQLException {
        return deliveryDAO.addSaleToRoute(routeId, saleId, deliveryOrder);
    }

    public int startRoute(int routeId) throws SQLException {
        return deliveryDAO.startRoute(routeId);
    }

    public int completeDelivery(int routeId, int saleId, String receivedBy)
            throws SQLException {
        return deliveryDAO.completeDelivery(routeId, saleId, receivedBy);
    }
}

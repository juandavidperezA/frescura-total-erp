
package sales.dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import sales.model.Sale;

public class SaleDAO {

    public int insertSale(Sale sale) throws SQLException {

        if (sale == null) {
            throw new SQLException("Sale cannot be null");
        }

        if (sale.getSaleNumber() == null || sale.getSaleNumber().isBlank()) {
            throw new SQLException("Sale number cannot be empty");
        }

        if (sale.getCustomerId() <= 0) {
            throw new SQLException("Customer ID must be positive");
        }

        if (sale.getStatusId() <= 0) {
            throw new SQLException("Sale status ID must be positive");
        }

        String sql = "INSERT INTO ventas (numero_venta, id_cliente, subtotal, total_descuento, total_impuesto, monto_total, id_estado, usuario_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sale.getSaleNumber());
            ps.setInt(2, sale.getCustomerId());
            ps.setDouble(3, sale.getSubtotal());
            ps.setDouble(4, sale.getTotalDiscount());
            ps.setDouble(5, sale.getTotalTax());
            ps.setDouble(6, sale.getTotalAmount());
            ps.setInt(7, sale.getStatusId());
            ps.setString(8, sale.getRegisteredBy());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public Sale findById(int id) throws SQLException {

        if (id <= 0) {
            throw new SQLException("Sale ID must be positive");
        }

        String sql = "SELECT * FROM ventas WHERE id_venta = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int saleId = rs.getInt("id_venta");
                    String saleNumber = rs.getString("numero_venta");
                    int customerId = rs.getInt("id_cliente");
                    LocalDateTime saleDate = rs.getObject("fecha_venta", LocalDateTime.class);
                    double subtotal = rs.getDouble("subtotal");
                    double totalDiscount = rs.getDouble("total_descuento");
                    double totalTax = rs.getDouble("total_impuesto");
                    double totalAmount = rs.getDouble("monto_total");
                    int statusId = rs.getInt("id_estado");
                    String registeredBy = rs.getString("usuario_registro");

                    Sale sale = new Sale(
                            saleId,
                            saleNumber,
                            customerId,
                            saleDate,
                            subtotal,
                            totalDiscount,
                            totalTax,
                            totalAmount,
                            statusId,
                            registeredBy
                    );

                    return sale;
                }
            }
        }

        return null;
    }

    public List<Sale> findAll() throws SQLException {

        String sql = "SELECT * FROM ventas";

        List<Sale> sales = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int saleId = rs.getInt("id_venta");
                String saleNumber = rs.getString("numero_venta");
                int customerId = rs.getInt("id_cliente");
                LocalDateTime saleDate = rs.getObject("fecha_venta", LocalDateTime.class);
                double subtotal = rs.getDouble("subtotal");
                double totalDiscount = rs.getDouble("total_descuento");
                double totalTax = rs.getDouble("total_impuesto");
                double totalAmount = rs.getDouble("monto_total");
                int statusId = rs.getInt("id_estado");
                String registeredBy = rs.getString("usuario_registro");

                Sale sale = new Sale(
                        saleId,
                        saleNumber,
                        customerId,
                        saleDate,
                        subtotal,
                        totalDiscount,
                        totalTax,
                        totalAmount,
                        statusId,
                        registeredBy
                );

                sales.add(sale);
            }
        }

        return sales;
    }

    public int updateSaleStatus(int saleId, int statusId) throws SQLException {

        if (saleId <= 0) {
            throw new SQLException("Sale ID must be positive");
        }

        if (statusId <= 0) {
            throw new SQLException("Sale status ID must be positive");
        }

        String sql = "UPDATE ventas SET id_estado = ? WHERE id_venta = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, statusId);
            ps.setInt(2, saleId);

            int affectedRows = ps.executeUpdate();

            return affectedRows;
        }
    }
}



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
import sales.model.SaleDetail;

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

                    return new Sale(
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

                Sale sale = new Sale(
                        rs.getInt("id_venta"),
                        rs.getString("numero_venta"),
                        rs.getInt("id_cliente"),
                        rs.getObject("fecha_venta", LocalDateTime.class),
                        rs.getDouble("subtotal"),
                        rs.getDouble("total_descuento"),
                        rs.getDouble("total_impuesto"),
                        rs.getDouble("monto_total"),
                        rs.getInt("id_estado"),
                        rs.getString("usuario_registro")
                );

                sales.add(sale);
            }
        }

        return sales;
    }

    public int updateSaleStatus(int saleId, int statusId) throws SQLException {

        if (saleId <= 0 || statusId <= 0) {
            throw new SQLException("Sale ID and status ID must be positive");
        }

        String sql = "UPDATE ventas SET id_estado = ? WHERE id_venta = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, statusId);
            ps.setInt(2, saleId);

            return ps.executeUpdate();
        }
    }

    public void findAvailableLotsByProduct(int productId) throws SQLException {

        if (productId <= 0) {
            throw new SQLException("Product ID must be positive");
        }

        String sql = "SELECT id_lote, numero_lote, cantidad, precio_venta, fecha_vencimiento, id_estado "
                + "FROM lotes_inventario "
                + "WHERE id_producto = ? "
                + "AND id_estado IN (1, 2) "
                + "AND fecha_vencimiento >= CURDATE() "
                + "AND cantidad > 0 "
                + "ORDER BY fecha_vencimiento ASC, id_lote ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    System.out.println("----------------------------");
                    System.out.println("Lot ID: " + rs.getInt("id_lote"));
                    System.out.println("Lot number: " + rs.getString("numero_lote"));
                    System.out.println("Available quantity: " + rs.getDouble("cantidad"));
                    System.out.println("Sale price: " + rs.getDouble("precio_venta"));
                    System.out.println("Expiration date: " + rs.getDate("fecha_vencimiento"));
                    System.out.println("Status ID: " + rs.getInt("id_estado"));
                }
            }
        }
    }

    public int confirmSale(Sale sale, List<SaleDetail> details) throws SQLException {

        if (sale == null) {
            throw new SQLException("Sale cannot be null");
        }

        if (sale.getSaleNumber() == null || sale.getSaleNumber().isBlank()) {
            throw new SQLException("Sale number cannot be empty");
        }

        if (sale.getCustomerId() <= 0) {
            throw new SQLException("Customer ID must be positive");
        }

        if (details == null || details.isEmpty()) {
            throw new SQLException("Sale must contain at least one product");
        }

        if (sale.getStatusId() != 1) {
            throw new SQLException("New sale must have Registered status");
        }

        double subtotal = 0;
        double totalDiscount = 0;
        double totalTax = 0;
        double totalAmount = 0;

        for (SaleDetail detail : details) {

            if (detail == null) {
                throw new SQLException("Sale detail cannot be null");
            }

            if (detail.getLotId() <= 0 || detail.getQuantitySold() <= 0) {
                throw new SQLException("Invalid lot ID or quantity");
            }

            if (detail.getUnitPrice() < 0
                    || detail.getDiscountAmount() < 0
                    || detail.getTaxAmount() < 0) {
                throw new SQLException("Sale amounts cannot be negative");
            }

            subtotal += detail.getSubtotal();
            totalDiscount += detail.getDiscountAmount();
            totalTax += detail.getTaxAmount();
            totalAmount += detail.getLineTotal();
        }

        sale.setSubtotal(subtotal);
        sale.setTotalDiscount(totalDiscount);
        sale.setTotalTax(totalTax);
        sale.setTotalAmount(totalAmount);

        String saleSql = "INSERT INTO ventas (numero_venta, id_cliente, subtotal, total_descuento, total_impuesto, monto_total, id_estado, usuario_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String lotSql = "UPDATE lotes_inventario "
                + "SET cantidad = cantidad - ? "
                + "WHERE id_lote = ? "
                + "AND cantidad >= ? "
                + "AND id_estado IN (1, 2) "
                + "AND fecha_vencimiento >= CURDATE()";

        String detailSql = "INSERT INTO detalles_venta (id_venta, id_lote, cantidad_vendida, precio_unitario, descuento_aplicado, monto_descuento, tasa_impuesto, monto_impuesto, subtotal, total_linea) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {
                int saleId;

                try (PreparedStatement ps = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {

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

                        if (!rs.next()) {
                            throw new SQLException("Could not generate sale ID");
                        }

                        saleId = rs.getInt(1);
                    }
                }

                for (SaleDetail detail : details) {

                    try (PreparedStatement ps = conn.prepareStatement(lotSql)) {

                        ps.setDouble(1, detail.getQuantitySold());
                        ps.setInt(2, detail.getLotId());
                        ps.setDouble(3, detail.getQuantitySold());

                        int affectedRows = ps.executeUpdate();

                        if (affectedRows == 0) {
                            throw new SQLException("Lot unavailable or insufficient stock: " + detail.getLotId());
                        }
                    }

                    try (PreparedStatement ps = conn.prepareStatement(detailSql)) {

                        ps.setInt(1, saleId);
                        ps.setInt(2, detail.getLotId());
                        ps.setDouble(3, detail.getQuantitySold());
                        ps.setDouble(4, detail.getUnitPrice());
                        ps.setDouble(5, detail.getDiscountApplied());
                        ps.setDouble(6, detail.getDiscountAmount());
                        ps.setDouble(7, detail.getTaxRate());
                        ps.setDouble(8, detail.getTaxAmount());
                        ps.setDouble(9, detail.getSubtotal());
                        ps.setDouble(10, detail.getLineTotal());

                        ps.executeUpdate();
                    }
                }

                conn.commit();
                return saleId;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}

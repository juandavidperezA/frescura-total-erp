
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

        String sql = "INSERT INTO ventas "
                + "(numero_venta, id_cliente, subtotal, total_descuento, "
                + "total_impuesto, monto_total, id_estado, usuario_registro) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
                    return new Sale(
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
                }
            }
        }

        return null;
    }

    public List<Sale> findAll() throws SQLException {

        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM ventas ORDER BY fecha_venta DESC";

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
            throw new SQLException("Invalid sale or status ID");
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

        String sql = "SELECT l.id_lote, l.numero_lote, l.cantidad, "
                + "l.precio_venta, l.fecha_vencimiento, e.nombre AS estado "
                + "FROM lotes_inventario l "
                + "JOIN estados_lote e ON l.id_estado = e.id_estado "
                + "WHERE l.id_producto = ? "
                + "AND e.nombre IN ('Disponible', 'Proximo a Vencer') "
                + "AND l.fecha_vencimiento >= CURDATE() "
                + "AND l.cantidad > 0 "
                + "ORDER BY l.fecha_vencimiento ASC, l.id_lote ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    System.out.println("----------------------------");
                    System.out.println("Lot ID: " + rs.getInt("id_lote"));
                    System.out.println("Lot number: " + rs.getString("numero_lote"));
                    System.out.println("Quantity: " + rs.getDouble("cantidad"));
                    System.out.println("Price: " + rs.getDouble("precio_venta"));
                    System.out.println("Expiration: " + rs.getDate("fecha_vencimiento"));
                    System.out.println("Status: " + rs.getString("estado"));
                }

                if (!found) {
                    System.out.println("No available lots found.");
                }
            }
        }
    }

    private double findDiscount(Connection conn, int statusId,
                                int categoryId) throws SQLException {

        String sql = "SELECT tasa_descuento FROM promociones "
                + "WHERE activa = TRUE "
                + "AND (aplica_estado_lote IS NULL OR aplica_estado_lote = ?) "
                + "AND (aplica_categoria_producto IS NULL OR aplica_categoria_producto = ?) "
                + "AND (fecha_inicio IS NULL OR fecha_inicio <= CURDATE()) "
                + "AND (fecha_fin IS NULL OR fecha_fin >= CURDATE()) "
                + "ORDER BY tasa_descuento DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, statusId);
            ps.setInt(2, categoryId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    double rate = rs.getDouble("tasa_descuento");

                    if (rate < 0 || rate > 1) {
                        throw new SQLException("Invalid discount rate");
                    }

                    return rate;
                }
            }
        }

        return 0;
    }

    public int confirmSale(Sale sale, List<SaleDetail> details) throws SQLException {

        if (sale == null) {
            throw new SQLException("Sale cannot be null");
        }

        if (sale.getSaleNumber() == null
                || sale.getSaleNumber().isBlank()
                || sale.getSaleNumber().length() > 50) {
            throw new SQLException("Invalid sale number");
        }

        if (sale.getCustomerId() <= 0) {
            throw new SQLException("Invalid customer ID");
        }

        if (details == null || details.isEmpty()) {
            throw new SQLException("Sale must contain products");
        }

        for (SaleDetail detail : details) {

            if (detail == null || detail.getLotId() <= 0) {
                throw new SQLException("Invalid sale detail");
            }

            if (!Double.isFinite(detail.getQuantitySold())
                    || detail.getQuantitySold() <= 0) {
                throw new SQLException("Invalid quantity");
            }

            if (Math.abs(detail.getQuantitySold() * 100
                    - Math.rint(detail.getQuantitySold() * 100)) > 0.000001) {
                throw new SQLException("Quantity must have at most two decimals");
            }
        }

        String lotSql = "SELECT l.id_lote, l.cantidad, l.precio_venta, "
                + "l.id_estado, e.nombre AS estado, p.id_categoria "
                + "FROM lotes_inventario l "
                + "JOIN estados_lote e ON l.id_estado = e.id_estado "
                + "JOIN productos p ON l.id_producto = p.id_producto "
                + "WHERE l.id_producto = ("
                + "SELECT id_producto FROM lotes_inventario WHERE id_lote = ?) "
                + "AND e.nombre IN ('Disponible', 'Proximo a Vencer') "
                + "AND l.fecha_vencimiento >= CURDATE() "
                + "AND l.cantidad > 0 "
                + "ORDER BY l.fecha_vencimiento ASC, l.id_lote ASC "
                + "LIMIT 1 FOR UPDATE";

        String updateSql = "UPDATE lotes_inventario "
                + "SET cantidad = cantidad - ? "
                + "WHERE id_lote = ? AND cantidad >= ?";

        String saleSql = "INSERT INTO ventas "
                + "(numero_venta, id_cliente, subtotal, total_descuento, "
                + "total_impuesto, monto_total, id_estado, usuario_registro) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String detailSql = "INSERT INTO detalles_venta "
                + "(id_venta, id_lote, cantidad_vendida, precio_unitario, "
                + "descuento_aplicado, monto_descuento, tasa_impuesto, "
                + "monto_impuesto, subtotal, total_linea) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String statusSql = "SELECT id_estado FROM estados_venta "
                + "WHERE nombre = 'Registrada'";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                int registeredStatus;

                try (PreparedStatement ps = conn.prepareStatement(statusSql);
                     ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException("Registered status not found");
                    }

                    registeredStatus = rs.getInt("id_estado");
                }

                double subtotal = 0;
                double totalDiscount = 0;
                double totalTax = 0;
                double totalAmount = 0;

                for (SaleDetail detail : details) {

                    try (PreparedStatement ps = conn.prepareStatement(lotSql)) {

                        ps.setInt(1, detail.getLotId());

                        try (ResultSet rs = ps.executeQuery()) {

                            if (!rs.next()) {
                                throw new SQLException("No available lot found");
                            }

                            int firstLotId = rs.getInt("id_lote");

                            if (firstLotId != detail.getLotId()) {
                                throw new SQLException("Select the first FEFO lot: " + firstLotId);
                            }

                            double stock = rs.getDouble("cantidad");
                            double price = rs.getDouble("precio_venta");
                            int statusId = rs.getInt("id_estado");
                            int categoryId = rs.getInt("id_categoria");
                            String status = rs.getString("estado");

                            if (stock < detail.getQuantitySold()) {
                                throw new SQLException("Insufficient stock in lot: " + firstLotId);
                            }

                            if (!Double.isFinite(price) || price < 0) {
                                throw new SQLException("Invalid lot price");
                            }

                            double discountRate = 0;

                            if (status.equals("Proximo a Vencer")) {
                                discountRate = findDiscount(conn, statusId, categoryId);
                            }

                            double lineSubtotal = Math.round(
                                    detail.getQuantitySold() * price * 100.0
                            ) / 100.0;

                            double discountAmount = Math.round(
                                    lineSubtotal * discountRate * 100.0
                            ) / 100.0;

                            double taxRate = 0;
                            double taxAmount = 0;

                            double lineTotal = lineSubtotal - discountAmount;

                            detail.setUnitPrice(price);
                            detail.setDiscountApplied(discountRate);
                            detail.setDiscountAmount(discountAmount);
                            detail.setTaxRate(taxRate);
                            detail.setTaxAmount(taxAmount);
                            detail.setSubtotal(lineSubtotal);
                            detail.setLineTotal(lineTotal);

                            subtotal += lineSubtotal;
                            totalDiscount += discountAmount;
                            totalTax += taxAmount;
                            totalAmount += lineTotal;
                        }
                    }

                    try (PreparedStatement ps = conn.prepareStatement(updateSql)) {

                        ps.setDouble(1, detail.getQuantitySold());
                        ps.setInt(2, detail.getLotId());
                        ps.setDouble(3, detail.getQuantitySold());

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException("Could not update inventory");
                        }
                    }
                }

                sale.setSubtotal(Math.round(subtotal * 100.0) / 100.0);
                sale.setTotalDiscount(Math.round(totalDiscount * 100.0) / 100.0);
                sale.setTotalTax(Math.round(totalTax * 100.0) / 100.0);
                sale.setTotalAmount(Math.round(totalAmount * 100.0) / 100.0);
                sale.setStatusId(registeredStatus);

                int saleId;

                try (PreparedStatement ps = conn.prepareStatement(
                        saleSql, Statement.RETURN_GENERATED_KEYS)) {

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

                sale.setId(saleId);

                return saleId;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}


package sales.dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import sales.model.Invoice;

public class InvoiceDAO {

    public Invoice createInvoice(int saleId) throws SQLException {

        if (saleId <= 0) {
            throw new SQLException("Sale ID must be positive");
        }

        String saleSql = "SELECT subtotal, total_descuento, "
                + "total_impuesto, monto_total "
                + "FROM ventas WHERE id_venta = ? FOR UPDATE";

        String invoiceSql = "INSERT INTO facturas "
                + "(id_venta, numero_factura, cufe, enlace_qr, "
                + "subtotal, total_descuento, monto_impuesto, monto_total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                double subtotal;
                double discount;
                double tax;
                double total;

                try (PreparedStatement ps = conn.prepareStatement(saleSql)) {

                    ps.setInt(1, saleId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Sale not found");
                        }

                        subtotal = rs.getDouble("subtotal");
                        discount = rs.getDouble("total_descuento");
                        tax = rs.getDouble("total_impuesto");
                        total = rs.getDouble("monto_total");
                    }
                }

                Invoice existingInvoice = findBySaleId(conn, saleId);

                if (existingInvoice != null) {
                    conn.commit();
                    return existingInvoice;
                }

                String invoiceNumber = "FAC-" + saleId;
                String cufe = UUID.randomUUID().toString().replace("-", "");
                String qrLink = "FRESCURA-TOTAL|"
                        + invoiceNumber + "|" + cufe;

                try (PreparedStatement ps = conn.prepareStatement(
                        invoiceSql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, saleId);
                    ps.setString(2, invoiceNumber);
                    ps.setString(3, cufe);
                    ps.setString(4, qrLink);
                    ps.setDouble(5, subtotal);
                    ps.setDouble(6, discount);
                    ps.setDouble(7, tax);
                    ps.setDouble(8, total);

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {

                        if (!rs.next()) {
                            throw new SQLException("Invoice ID was not generated");
                        }
                    }
                }

                Invoice invoice = findBySaleId(conn, saleId);

                conn.commit();

                return invoice;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public Invoice findBySaleId(int saleId) throws SQLException {

        if (saleId <= 0) {
            throw new SQLException("Sale ID must be positive");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            return findBySaleId(conn, saleId);
        }
    }

    private Invoice findBySaleId(Connection conn, int saleId)
            throws SQLException {

        String sql = "SELECT * FROM facturas "
                + "WHERE id_venta = ? ORDER BY id_factura LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Invoice(
                            rs.getInt("id_factura"),
                            rs.getInt("id_venta"),
                            rs.getString("numero_factura"),
                            rs.getString("cufe"),
                            rs.getString("enlace_qr"),
                            rs.getTimestamp("fecha_emision").toLocalDateTime(),
                            rs.getDouble("subtotal"),
                            rs.getDouble("total_descuento"),
                            rs.getDouble("monto_impuesto"),
                            rs.getDouble("monto_total")
                    );
                }
            }
        }

        return null;
    }
}

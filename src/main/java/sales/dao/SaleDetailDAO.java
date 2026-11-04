
package sales.dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import sales.model.SaleDetail;

public class SaleDetailDAO {

    public int insertSaleDetail(SaleDetail detail) throws SQLException {

        if (detail == null) {
            throw new SQLException("Sale detail cannot be null");
        }

        if (detail.getSaleId() <= 0 || detail.getLotId() <= 0) {
            throw new SQLException("Sale ID and lot ID must be positive");
        }

        if (detail.getQuantitySold() <= 0) {
            throw new SQLException("Quantity sold must be positive");
        }

        if (detail.getUnitPrice() < 0) {
            throw new SQLException("Unit price cannot be negative");
        }

        String sql = "INSERT INTO detalles_venta (id_venta, id_lote, cantidad_vendida, precio_unitario, descuento_aplicado, monto_descuento, tasa_impuesto, monto_impuesto, subtotal, total_linea) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, detail.getSaleId());
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

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public List<SaleDetail> findBySaleId(int saleId) throws SQLException {

        if (saleId <= 0) {
            throw new SQLException("Sale ID must be positive");
        }

        String sql = "SELECT * FROM detalles_venta WHERE id_venta = ?";

        List<SaleDetail> details = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int id = rs.getInt("id_detalle");
                    int lotId = rs.getInt("id_lote");
                    double quantitySold = rs.getDouble("cantidad_vendida");
                    double unitPrice = rs.getDouble("precio_unitario");
                    double discountApplied = rs.getDouble("descuento_aplicado");
                    double discountAmount = rs.getDouble("monto_descuento");
                    double taxRate = rs.getDouble("tasa_impuesto");
                    double taxAmount = rs.getDouble("monto_impuesto");
                    double subtotal = rs.getDouble("subtotal");
                    double lineTotal = rs.getDouble("total_linea");

                    SaleDetail detail = new SaleDetail(
                            id,
                            saleId,
                            lotId,
                            quantitySold,
                            unitPrice,
                            discountApplied,
                            discountAmount,
                            taxRate,
                            taxAmount,
                            subtotal,
                            lineTotal
                    );

                    details.add(detail);
                }
            }
        }

        return details;
    }
}


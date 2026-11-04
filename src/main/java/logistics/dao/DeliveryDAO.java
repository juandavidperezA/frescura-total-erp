
package logistics.dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sales.model.Sale;

public class DeliveryDAO {

    public int prepareSale(int saleId) throws SQLException {

        if (saleId <= 0) {
            throw new SQLException("Sale ID must be positive");
        }

        String sql = "UPDATE ventas "
                + "SET id_estado = (SELECT id_estado FROM estados_venta "
                + "WHERE nombre = 'En Preparacion') "
                + "WHERE id_venta = ? "
                + "AND id_estado = (SELECT id_estado FROM estados_venta "
                + "WHERE nombre = 'Registrada')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            return ps.executeUpdate();
        }
    }

    public List<Sale> findSalesInPreparation() throws SQLException {

        List<Sale> sales = new ArrayList<>();

        String sql = "SELECT v.* FROM ventas v "
                + "JOIN estados_venta e ON v.id_estado = e.id_estado "
                + "WHERE e.nombre = 'En Preparacion' "
                + "ORDER BY v.fecha_venta ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Sale sale = new Sale(
                        rs.getInt("id_venta"),
                        rs.getString("numero_venta"),
                        rs.getInt("id_cliente"),
                        rs.getTimestamp("fecha_venta").toLocalDateTime(),
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
}

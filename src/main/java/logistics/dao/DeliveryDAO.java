
package logistics.dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    
    public int createRoute(String routeName, String username) throws SQLException {

    if (routeName == null || routeName.isBlank()
            || username == null || username.isBlank()) {
        throw new SQLException("Route name and username are required");
    }

    String sql = "INSERT INTO rutas_entrega "
            + "(nombre_ruta, usuario_creacion) VALUES (?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setString(1, routeName);
        ps.setString(2, username);

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            return rs.next() ? rs.getInt(1) : -1;
        }
    }
}

public int assignDelivery(int routeId, int vehicleId,
                          int driverId) throws SQLException {

    if (routeId <= 0 || vehicleId <= 0 || driverId <= 0) {
        throw new SQLException("Invalid route, vehicle or driver ID");
    }

    String sql = "INSERT INTO asignaciones_entrega "
            + "(id_ruta, id_vehiculo, id_conductor) "
            + "SELECT r.id_ruta, v.id_vehiculo, c.id_conductor "
            + "FROM rutas_entrega r "
            + "JOIN vehiculos v ON v.id_vehiculo = ? "
            + "JOIN conductores c ON c.id_conductor = ? "
            + "WHERE r.id_ruta = ? "
            + "AND r.estado = 'Planificada' "
            + "AND v.estado = 'Activo' "
            + "AND c.activo = TRUE "
            + "AND NOT EXISTS ("
            + "SELECT 1 FROM asignaciones_entrega a "
            + "WHERE a.id_ruta = r.id_ruta)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, vehicleId);
        ps.setInt(2, driverId);
        ps.setInt(3, routeId);

        return ps.executeUpdate();
    }
}

public int addSaleToRoute(int routeId, int saleId,
                          int deliveryOrder) throws SQLException {

    if (routeId <= 0 || saleId <= 0 || deliveryOrder <= 0) {
        throw new SQLException("Invalid route, sale or delivery order");
    }

    String sql = "INSERT INTO ventas_ruta "
            + "(id_ruta, id_venta, orden_entrega, direccion_destino) "
            + "SELECT ?, v.id_venta, ?, c.direccion "
            + "FROM ventas v "
            + "JOIN clientes c ON v.id_cliente = c.id_cliente "
            + "JOIN estados_venta e ON v.id_estado = e.id_estado "
            + "WHERE v.id_venta = ? "
            + "AND e.nombre = 'En Preparacion' "
            + "AND c.direccion IS NOT NULL "
            + "AND TRIM(c.direccion) <> '' "
            + "AND EXISTS ("
            + "SELECT 1 FROM rutas_entrega r "
            + "JOIN asignaciones_entrega a ON a.id_ruta = r.id_ruta "
            + "WHERE r.id_ruta = ? "
            + "AND r.estado = 'Planificada') "
            + "AND NOT EXISTS ("
            + "SELECT 1 FROM ventas_ruta vr "
            + "WHERE vr.id_venta = v.id_venta)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, routeId);
        ps.setInt(2, deliveryOrder);
        ps.setInt(3, saleId);
        ps.setInt(4, routeId);

        return ps.executeUpdate();
    }
}
}

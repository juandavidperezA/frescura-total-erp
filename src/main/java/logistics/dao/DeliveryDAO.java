
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

    public int createRoute(String routeName, String username)
            throws SQLException {

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

    public int assignDelivery(int routeId, int vehicleId, int driverId)
            throws SQLException {

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

    public int addSaleToRoute(int routeId, int saleId, int deliveryOrder)
            throws SQLException {

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

    public int startRoute(int routeId) throws SQLException {

        if (routeId <= 0) {
            throw new SQLException("Route ID must be positive");
        }

        String lockSql = "SELECT id_ruta FROM rutas_entrega "
                + "WHERE id_ruta = ? AND estado = 'Planificada' FOR UPDATE";

        String checkSql = "SELECT COUNT(*) AS total, "
                + "COALESCE(SUM(CASE WHEN e.nombre = 'En Preparacion' "
                + "THEN 1 ELSE 0 END), 0) AS ready "
                + "FROM ventas_ruta vr "
                + "JOIN ventas v ON vr.id_venta = v.id_venta "
                + "JOIN estados_venta e ON v.id_estado = e.id_estado "
                + "WHERE vr.id_ruta = ?";

        String routeSql = "UPDATE rutas_entrega "
                + "SET estado = 'En Transito', hora_salida_real = NOW() "
                + "WHERE id_ruta = ? AND estado = 'Planificada' "
                + "AND EXISTS (SELECT 1 FROM asignaciones_entrega "
                + "WHERE id_ruta = ?)";

        String salesSql = "UPDATE ventas v "
                + "JOIN ventas_ruta vr ON v.id_venta = vr.id_venta "
                + "SET v.id_estado = (SELECT id_estado FROM estados_venta "
                + "WHERE nombre = 'En Transito') "
                + "WHERE vr.id_ruta = ? "
                + "AND v.id_estado = (SELECT id_estado FROM estados_venta "
                + "WHERE nombre = 'En Preparacion')";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                try (PreparedStatement ps = conn.prepareStatement(lockSql)) {

                    ps.setInt(1, routeId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Route is not planned");
                        }
                    }
                }

                int total;
                int ready;

                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {

                    ps.setInt(1, routeId);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        total = rs.getInt("total");
                        ready = rs.getInt("ready");
                    }
                }

                if (total == 0 || total != ready) {
                    throw new SQLException("Route must contain sales in preparation");
                }

                try (PreparedStatement ps = conn.prepareStatement(routeSql)) {

                    ps.setInt(1, routeId);
                    ps.setInt(2, routeId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Route has no delivery assignment");
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(salesSql)) {

                    ps.setInt(1, routeId);

                    if (ps.executeUpdate() != total) {
                        throw new SQLException("Could not update all sales");
                    }
                }

                conn.commit();
                return total;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public int completeDelivery(int routeId, int saleId, String receivedBy)
            throws SQLException {

        if (routeId <= 0 || saleId <= 0
                || receivedBy == null || receivedBy.isBlank()) {
            throw new SQLException("Invalid delivery information");
        }

        String lockSql = "SELECT id_ruta FROM rutas_entrega "
                + "WHERE id_ruta = ? AND estado = 'En Transito' FOR UPDATE";

        String saleSql = "UPDATE ventas v "
                + "JOIN ventas_ruta vr ON v.id_venta = vr.id_venta "
                + "SET v.id_estado = (SELECT id_estado FROM estados_venta "
                + "WHERE nombre = 'Entregada') "
                + "WHERE vr.id_ruta = ? AND v.id_venta = ? "
                + "AND v.id_estado = (SELECT id_estado FROM estados_venta "
                + "WHERE nombre = 'En Transito')";

        String detailSql = "UPDATE ventas_ruta "
                + "SET fecha_entrega = NOW(), recibido_por = ? "
                + "WHERE id_ruta = ? AND id_venta = ? "
                + "AND fecha_entrega IS NULL";

        String pendingSql = "SELECT COUNT(*) AS pending "
                + "FROM ventas_ruta vr "
                + "JOIN ventas v ON vr.id_venta = v.id_venta "
                + "JOIN estados_venta e ON v.id_estado = e.id_estado "
                + "WHERE vr.id_ruta = ? AND e.nombre <> 'Entregada'";

        String routeSql = "UPDATE rutas_entrega "
                + "SET estado = 'Completada', hora_llegada_real = NOW() "
                + "WHERE id_ruta = ? AND estado = 'En Transito'";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                try (PreparedStatement ps = conn.prepareStatement(lockSql)) {

                    ps.setInt(1, routeId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Route is not in transit");
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(saleSql)) {

                    ps.setInt(1, routeId);
                    ps.setInt(2, saleId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Sale cannot be delivered");
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(detailSql)) {

                    ps.setString(1, receivedBy);
                    ps.setInt(2, routeId);
                    ps.setInt(3, saleId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Could not record delivery");
                    }
                }

                int pending;

                try (PreparedStatement ps = conn.prepareStatement(pendingSql)) {

                    ps.setInt(1, routeId);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        pending = rs.getInt("pending");
                    }
                }

                if (pending == 0) {

                    try (PreparedStatement ps = conn.prepareStatement(routeSql)) {

                        ps.setInt(1, routeId);

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException("Could not complete route");
                        }
                    }
                }

                conn.commit();
                return 1;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}

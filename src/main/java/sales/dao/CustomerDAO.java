
package com.frescuratotal.erp.sales.dao;


import com.frescuratotal.erp.sales.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public boolean create(Customer customer) throws SQLException {
        String sql = "INSERT INTO clientes "
                + "(nombre, documento, email, telefono, direccion, id_tipo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, customer.getNombre());
            statement.setString(2, customer.getDocumento());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getTelefono());
            statement.setString(5, customer.getDireccion());
            statement.setInt(6, customer.getIdTipo());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setIdCliente(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

            return false;
        }
    }

    public Customer findById(int idCliente) throws SQLException {
        String sql = "SELECT id_cliente, nombre, documento, email, "
                + "telefono, direccion, id_tipo, fecha_registro "
                + "FROM clientes WHERE id_cliente = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idCliente);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCustomer(resultSet);
                }
            }
        }

        return null;
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT id_cliente, nombre, documento, email, "
                + "telefono, direccion, id_tipo, fecha_registro "
                + "FROM clientes ORDER BY id_cliente";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                customers.add(mapCustomer(resultSet));
            }
        }

        return customers;
    }

    public boolean update(Customer customer) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, documento = ?, "
                + "email = ?, telefono = ?, direccion = ?, id_tipo = ? "
                + "WHERE id_cliente = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customer.getNombre());
            statement.setString(2, customer.getDocumento());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getTelefono());
            statement.setString(5, customer.getDireccion());
            statement.setInt(6, customer.getIdTipo());
            statement.setInt(7, customer.getIdCliente());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int idCliente) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idCliente);

            return statement.executeUpdate() > 0;
        }
    }

    public Customer findByDocument(String documento) throws SQLException {
        String sql = "SELECT id_cliente, nombre, documento, email, "
                + "telefono, direccion, id_tipo, fecha_registro "
                + "FROM clientes WHERE documento = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, documento);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCustomer(resultSet);
                }
            }
        }

        return null;
    }

    private Customer mapCustomer(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();

        customer.setIdCliente(resultSet.getInt("id_cliente"));
        customer.setNombre(resultSet.getString("nombre"));
        customer.setDocumento(resultSet.getString("documento"));
        customer.setEmail(resultSet.getString("email"));
        customer.setTelefono(resultSet.getString("telefono"));
        customer.setDireccion(resultSet.getString("direccion"));
        customer.setIdTipo(resultSet.getInt("id_tipo"));

        Timestamp timestamp = resultSet.getTimestamp("fecha_registro");

        if (timestamp != null) {
            customer.setFechaRegistro(timestamp.toLocalDateTime());
        }

        return customer;
    }
}

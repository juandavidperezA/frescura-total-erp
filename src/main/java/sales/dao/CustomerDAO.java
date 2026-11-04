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
import sales.model.Customer;
import sales.validation.CustomerValidator;

public class CustomerDAO {

    public int insertCustomer(Customer customer) throws SQLException {
        CustomerValidator.validate(customer);

        String sql = "INSERT INTO clientes (nombre, documento, email, telefono, direccion, id_tipo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getDocument());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getAddress());
            ps.setInt(6, customer.getTypeId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public Customer findById(int id) throws SQLException {
        if (id <= 0) {
            throw new SQLException("Customer ID must be positive");
        }

        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    int customerId = rs.getInt("id_cliente");
                    String name = rs.getString("nombre");
                    String document = rs.getString("documento");
                    String email = rs.getString("email");
                    String phone = rs.getString("telefono");
                    String address = rs.getString("direccion");
                    int typeId = rs.getInt("id_tipo");
                    LocalDateTime registrationDate = rs.getObject("fecha_registro", LocalDateTime.class);

                    Customer customer = new Customer(
                            customerId,
                            name,
                            document,
                            email,
                            phone,
                            address,
                            typeId,
                            registrationDate
                    );

                    return customer;
                }
            }
        }

        return null;
    }

    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT * FROM clientes";

        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int customerId = rs.getInt("id_cliente");
                String name = rs.getString("nombre");
                String document = rs.getString("documento");
                String email = rs.getString("email");
                String phone = rs.getString("telefono");
                String address = rs.getString("direccion");
                int typeId = rs.getInt("id_tipo");
                LocalDateTime registrationDate = rs.getObject("fecha_registro", LocalDateTime.class);

                Customer customer = new Customer(
                        customerId,
                        name,
                        document,
                        email,
                        phone,
                        address,
                        typeId,
                        registrationDate
                );

                customers.add(customer);
            }
        }

        return customers;
    }
    
    public int updateCustomer(Customer customer) throws SQLException {
    CustomerValidator.validate(customer);

    if (customer.getId() <= 0) {
        throw new SQLException("Customer ID must be positive");
    }

    String sql = "UPDATE clientes SET nombre=?, documento=?, email=?, telefono=?, direccion=?, id_tipo=? WHERE id_cliente=?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, customer.getName());
        ps.setString(2, customer.getDocument());
        ps.setString(3, customer.getEmail());
        ps.setString(4, customer.getPhone());
        ps.setString(5, customer.getAddress());
        ps.setInt(6, customer.getTypeId());
        ps.setInt(7, customer.getId());

        int affectedRows = ps.executeUpdate();
        return affectedRows;
    }
}
}
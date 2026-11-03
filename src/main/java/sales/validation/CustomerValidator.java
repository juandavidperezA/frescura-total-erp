package sales.validation;

import java.sql.SQLException;
import sales.model.Customer;

public class CustomerValidator {

    public static void validate(Customer customer) throws SQLException {

        if (customer == null) {
            throw new SQLException("Customer cannot be null");
        }

        if (customer.getName() == null || customer.getName().isBlank()) {
            throw new SQLException("Customer name cannot be empty");
        }

        if (customer.getName().length() > 255) {
            throw new SQLException("Customer name cannot exceed 255 characters");
        }

        if (customer.getDocument() == null || customer.getDocument().isBlank()) {
            throw new SQLException("Customer document cannot be empty");
        }

        if (customer.getDocument().length() > 20) {
            throw new SQLException("Customer document cannot exceed 20 characters");
        }

        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            throw new SQLException("Customer email cannot be empty");
        }

        if (customer.getEmail().length() > 100) {
            throw new SQLException("Customer email cannot exceed 100 characters");
        }

        if (customer.getPhone() != null && customer.getPhone().length() > 20) {
            throw new SQLException("Customer phone cannot exceed 20 characters");
        }

        if (customer.getPhone() != null
                && !customer.getPhone().isBlank()
                && !customer.getPhone().matches("[0-9]+")) {
            throw new SQLException("Customer phone must contain only numbers");
        }

        if (customer.getAddress() != null && customer.getAddress().length() > 255) {
            throw new SQLException("Customer address cannot exceed 255 characters");
        }
    }
}
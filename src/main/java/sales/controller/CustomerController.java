
package sales.controller;

import java.sql.SQLException;
import java.util.List;
import sales.dao.CustomerDAO;
import sales.model.Customer;


public class CustomerController {
    private final CustomerDAO customerDAO = new CustomerDAO();

    public int registerCustomer(Customer customer) throws SQLException {
        return customerDAO.insertCustomer(customer);
    }

    public Customer findCustomerById(int id) throws SQLException {
        return customerDAO.findById(id);
    }

    public List<Customer> findAllCustomers() throws SQLException {
        return customerDAO.findAll();
    }

    public int updateCustomer(Customer customer) throws SQLException {
        return customerDAO.updateCustomer(customer);
    }
}

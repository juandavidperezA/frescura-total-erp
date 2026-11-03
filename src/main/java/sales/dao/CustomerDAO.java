
package sales.dao;

import java.sql.SQLException;
import sales.model.Customer;
import sales.validation.CustomerValidator;


public class CustomerDAO {
    public int insertCustomer(Customer customer) throws SQLException{
        CustomerValidator.validate(customer);
        
        String sql="INSERT INTO clientes (nombre, documento, email, telefono, direccion, id_tipo, fecha_registro)";
        
        
    }
}

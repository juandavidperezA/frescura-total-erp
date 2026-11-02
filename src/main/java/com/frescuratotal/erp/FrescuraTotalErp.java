/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.frescuratotal.erp;

import com.frescuratotal.erp.config.DatabaseConnection;
import com.frescuratotal.erp.suppliers.controller.SupplierController;
import com.frescuratotal.erp.suppliers.dao.SupplierDAO;
import com.frescuratotal.erp.suppliers.dao.SupplierProductDAO;
import com.frescuratotal.erp.suppliers.view.SupplierView;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author USUARIO
 */
public class FrescuraTotalErp {

    public static void main(String[] args) {
        try (Connection conn= DatabaseConnection.getConnection()){
            System.out.println("conexion exitosa");
            
        } catch (SQLException e) {
            System.out.println("error de conexion: "+e.getMessage());
        }

        SupplierDAO dao = new SupplierDAO();
        SupplierProductDAO productDAO= new SupplierProductDAO();
        SupplierController controller = new SupplierController(dao,productDAO);
        SupplierView view= new SupplierView(controller);
        view.start();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.frescuratotal.erp;

import com.frescuratotal.erp.config.DatabaseConnection;
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
    }
}

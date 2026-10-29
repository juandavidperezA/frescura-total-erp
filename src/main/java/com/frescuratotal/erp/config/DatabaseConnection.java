/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;
/**
 *
 * @author USUARIO
 */
public class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL= dotenv.get("DB_URL");
    private static final String USER=dotenv.get("DB_USER");
    private static final String PASSWORD=dotenv.get("DB_PASSWORD");
    
    public static Connection getConnection()throws SQLException{
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
    
}

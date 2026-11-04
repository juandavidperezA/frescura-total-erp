/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.dao;

import com.frescuratotal.erp.config.DatabaseConnection;
import com.frescuratotal.erp.inventory.model.QuarantineRecord;
import com.mysql.cj.MysqlType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author USUARIO
 */

public class QuarantineDAO {
    
    public int insertQuarantine(QuarantineRecord record)throws SQLException{
        if(record==null){
            throw new IllegalArgumentException("Quarantine record cannot be null");
        }
        
        if(record.getLotId()<=0){
            throw new IllegalArgumentException("Lot ID must be positive");
        }
        
        if(record.getReason()==null || record.getReason().isBlank()){
            throw new IllegalArgumentException("Quarantine reason cannot be empty");
        }
        
        if(record.getUser()==null || record.getUser().isBlank()){
            throw new IllegalArgumentException("Quarantine User cannot be empty");
        }
        String checkSql = "SELECT id_estado FROM lotes_inventario WHERE id_lote = ? FOR UPDATE";
        String sql="INSERT INTO registro_cuarentena (id_lote, motivo, usuario) VALUES (?,?,?)"; 
        String updatesql= "UPDATE lotes_inventario SET id_estado=? WHERE id_lote=?";
        
        try(Connection conn = DatabaseConnection.getConnection()){
            conn.setAutoCommit(false);
        
            try(PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement psupdatesql= conn.prepareStatement(updatesql);
            PreparedStatement pschecksql= conn.prepareStatement(checkSql)){
            
            ps.setInt(1, record.getLotId());
            ps.setString(2, record.getReason());
            ps.setString(3, record.getUser());
            
            psupdatesql.setInt(1, 4);
            psupdatesql.setInt(2, record.getLotId());
            
            pschecksql.setInt(1, record.getLotId());
            
            try(ResultSet rs= pschecksql.executeQuery()){
                if(!rs.next()){
                throw new SQLException("Inventory lot not found");
                }
                int currentStatus=rs.getInt("id_estado");
                
                if(currentStatus==4){
                    throw new SQLException("Inventory lot is already in quarantine");
                }
            }
            
            int affectedRows = ps.executeUpdate();
            int updateRows= psupdatesql.executeUpdate();
            
            if(affectedRows !=1 || updateRows!=1){
                throw new SQLException("Failed to quarantine inventory lot");
            }
            
            int generatedId= -1;
            
            try(ResultSet rs=ps.getGeneratedKeys()){
                if(rs.next()){
                generatedId=rs.getInt(1);
                }
            }
            if(generatedId<=0){
                throw new SQLException("Failed to retrieve generated quarantine ID");
            }
            conn.commit();
            return generatedId;
        } catch (SQLException e){
            conn.rollback();
            throw e;
        }
            
        }
    }
    
    public List<QuarantineRecord> findActiveQuarantines()throws SQLException{
        String sql="SELECT * FROM registro_cuarentena WHERE resuelta = 0";
        List<QuarantineRecord> quarantines =new ArrayList<>();
        
        try(Connection conn= DatabaseConnection.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)){
            
            try(ResultSet rs= ps.executeQuery()){
                while(rs.next()){
                    int id=rs.getInt("id_registro");
                    int lotId=rs.getInt("id_lote");
                    String reason= rs.getString("motivo");
                    String user=rs.getString("usuario");
                    LocalDateTime registrationDate=rs.getObject("fecha_registro",LocalDateTime.class );
                    boolean resolved = rs.getBoolean("resuelta");
                    LocalDateTime resolutionDate= rs.getObject("fecha_resolucion", LocalDateTime.class);
                    String resolutionNotes= rs.getString("notas_resolucion");
                    QuarantineRecord record= new QuarantineRecord(id, lotId, reason, user, registrationDate, resolved, resolutionDate, resolutionNotes);
                    quarantines.add(record);
                }
            }
        }return quarantines;
    }
}


package reports.dao;


import com.frescuratotal.erp.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import reports.model.Report;


public class reportsDAO {
    
    
    public Report findStats(int idLote, int idProveedor) throws SQLException{
        
        String sql="SELECT l.id_lote, l.cantidad_inicial, e.nombre AS estado_lote, p.id_proveedor, p.nombre AS nombre_proveedor FROM lotes_inventario l INNER JOIN proveedores p ON l.id_proveedor = ? INNER JOIN estados_lote e ON l.id_estado = e.id_estado;";
        
        try (Connection conn= DatabaseConnection.getConnection();
               PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(idLote, idLote);
            
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    int idlote=rs.getInt("id_lote");
                    int cantidadInicial=rs.getInt("id_lote");
                    String nombreestado=rs.getString("nombre");
                    int idproveedor=rs.getInt("id_proveedor");
                    String nombreproveedor=rs.getString("nombre");
                    
                    Report report= new Report(
                            idLote,
                            cantidadInicial,
                            nombreestado,
                            idProveedor,
                            nombreproveedor
                    );
                    return report;
                }
            }
        }
        return null;
    }
    
    
}

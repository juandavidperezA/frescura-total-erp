
package reports.controller;

import java.sql.SQLException;
import reports.dao.reportsDAO;
import reports.model.Report;



public class ReportController {
    
    private final reportsDAO reportsdao=new reportsDAO();
    
    public Report createReport(int idLote, int idProveedor) throws SQLException{
        return reportsdao.findStats(idLote, idProveedor);
    }
    
}


package reports.model;


public class Report {
    private int idLote;
    private int cantidadInicialProductos;
    private String nombreEstadoLote;
    private int idProveedor;
    private String nombreProveedor;

    public Report(int idLote, int cantidadInicialProductos, String nombreEstadoLote, int idProveedor, String nombreProveedor) {
        this.idLote = idLote;
        this.cantidadInicialProductos = cantidadInicialProductos;
        this.nombreEstadoLote = nombreEstadoLote;
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
    }

    public int getIdLote() {
        return idLote;
    }

    public int getCantidadInicialProductos() {
        return cantidadInicialProductos;
    }

    public String getNombreEstadoLote() {
        return nombreEstadoLote;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }
    
    
}

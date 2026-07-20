package com.example.inmia.admin;

public class ReporteProyectoItem {

    private final String proyectoId;
    private final String nombreProyecto;
    private final String distrito;
    private final int numAsesores;
    private final String asesoresNombres;
    private final int numAprobadas;
    private final int numPagadas;
    private final int numPendientes;
    private final int numRechazadas;
    private final double montoTotal;
    private final boolean tieneAsesoresAsignados;

    public ReporteProyectoItem(
            String proyectoId,
            String nombreProyecto,
            String distrito,
            int numAsesores,
            String asesoresNombres,
            int numAprobadas,
            int numPagadas,
            int numPendientes,
            int numRechazadas,
            double montoTotal) {
        this(proyectoId, nombreProyecto, distrito, numAsesores, asesoresNombres,
                numAprobadas, numPagadas, numPendientes, numRechazadas, montoTotal, false);
    }

    public ReporteProyectoItem(
            String proyectoId,
            String nombreProyecto,
            String distrito,
            int numAsesores,
            String asesoresNombres,
            int numAprobadas,
            int numPagadas,
            int numPendientes,
            int numRechazadas,
            double montoTotal,
            boolean tieneAsesoresAsignados) {
        this.proyectoId                = proyectoId != null ? proyectoId : "";
        this.nombreProyecto            = nombreProyecto != null ? nombreProyecto : "";
        this.distrito                  = distrito != null ? distrito : "";
        this.numAsesores               = numAsesores;
        this.asesoresNombres           = asesoresNombres != null ? asesoresNombres : "";
        this.numAprobadas              = numAprobadas;
        this.numPagadas                = numPagadas;
        this.numPendientes             = numPendientes;
        this.numRechazadas             = numRechazadas;
        this.montoTotal                = montoTotal;
        this.tieneAsesoresAsignados    = tieneAsesoresAsignados;
    }

    public String getProyectoId()       { return proyectoId; }
    public String getNombreProyecto()   { return nombreProyecto; }
    public String getDistrito()         { return distrito; }
    public int    getNumAsesores()      { return numAsesores; }
    public String getAsesoresNombres()  { return asesoresNombres; }
    public int    getNumAprobadas()     { return numAprobadas; }
    public int    getNumPagadas()       { return numPagadas; }
    public int    getNumPendientes()    { return numPendientes; }
    public int    getNumRechazadas()    { return numRechazadas; }
    public double getMontoTotal()       { return montoTotal; }
    public boolean isTieneAsesoresAsignados() { return tieneAsesoresAsignados; }

    public int getTotalSeparaciones() {
        return numAprobadas + numPagadas + numPendientes + numRechazadas;
    }
}

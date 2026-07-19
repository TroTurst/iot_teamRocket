package com.example.inmia.admin;

public class ReporteAsesorItem {

    private final String asesorId;
    private final String nombreAsesor;
    private final String zonaTrabajo;
    private final int numProyectos;
    private final String proyectosNombres;
    private final int numAprobadas;
    private final int numPagadas;
    private final int numPendientes;
    private final int numRechazadas;
    private final double montoTotal;
    private final int numCitas;

    public ReporteAsesorItem(
            String asesorId,
            String nombreAsesor,
            String zonaTrabajo,
            int numProyectos,
            String proyectosNombres,
            int numAprobadas,
            int numPagadas,
            int numPendientes,
            int numRechazadas,
            double montoTotal,
            int numCitas) {
        this.asesorId         = asesorId != null ? asesorId : "";
        this.nombreAsesor     = nombreAsesor != null ? nombreAsesor : "";
        this.zonaTrabajo      = zonaTrabajo != null ? zonaTrabajo : "";
        this.numProyectos     = numProyectos;
        this.proyectosNombres = proyectosNombres != null ? proyectosNombres : "";
        this.numAprobadas     = numAprobadas;
        this.numPagadas       = numPagadas;
        this.numPendientes    = numPendientes;
        this.numRechazadas    = numRechazadas;
        this.montoTotal       = montoTotal;
        this.numCitas         = numCitas;
    }

    public String getAsesorId()         { return asesorId; }
    public String getNombreAsesor()     { return nombreAsesor; }
    public String getZonaTrabajo()      { return zonaTrabajo; }
    public int    getNumProyectos()     { return numProyectos; }
    public String getProyectosNombres() { return proyectosNombres; }
    public int    getNumAprobadas()     { return numAprobadas; }
    public int    getNumPagadas()       { return numPagadas; }
    public int    getNumPendientes()    { return numPendientes; }
    public int    getNumRechazadas()    { return numRechazadas; }
    public double getMontoTotal()       { return montoTotal; }
    public int    getNumCitas()         { return numCitas; }

    public int getTotalSeparaciones() {
        return numAprobadas + numPagadas + numPendientes + numRechazadas;
    }
}

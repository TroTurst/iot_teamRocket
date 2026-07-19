package com.example.inmia.admin;

public class SeparacionPendiente {

    private final String docId;
    private final String nombreProyecto;
    private final String ubicacion;
    private final String clienteId;
    private final String clienteNombre;
    private final String tipologia;
    private final String monto;
    private final String fecha;

    public SeparacionPendiente(
            String docId,
            String nombreProyecto,
            String ubicacion,
            String clienteId,
            String clienteNombre,
            String tipologia,
            String monto,
            String fecha) {
        this.docId          = docId != null ? docId : "";
        this.nombreProyecto = nombreProyecto != null ? nombreProyecto : "";
        this.ubicacion      = ubicacion != null ? ubicacion : "";
        this.clienteId      = clienteId != null ? clienteId : "";
        this.clienteNombre  = clienteNombre != null ? clienteNombre : "";
        this.tipologia      = tipologia != null ? tipologia : "";
        this.monto          = monto != null ? monto : "";
        this.fecha          = fecha != null ? fecha : "";
    }

    public String getDocId()          { return docId; }
    public String getNombreProyecto() { return nombreProyecto; }
    public String getUbicacion()      { return ubicacion; }
    public String getClienteId()      { return clienteId; }
    public String getClienteNombre()  { return clienteNombre; }
    public String getTipologia()      { return tipologia; }
    public String getMonto()          { return monto; }
    public String getFecha()          { return fecha; }
}

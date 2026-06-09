package com.example.inmia.asesor;

public class SeparacionItem {

    private final String status;
    private final int statusColorRes;
    private final String project;
    private final String location;
    private final String company;
    private final String actionLabel;
    private final boolean confirmed;
    private final String docId;
    private final String clienteId;
    private final String tipologia;
    private final String fecha;
    private final String monto;

    public SeparacionItem(
        String status,
        int statusColorRes,
        String project,
        String location,
        String company,
        String actionLabel,
        boolean confirmed
    ) {
        this(status, statusColorRes, project, location, company, actionLabel, confirmed, "", "", "", "", "");
    }

    public SeparacionItem(
        String status,
        int statusColorRes,
        String project,
        String location,
        String company,
        String actionLabel,
        boolean confirmed,
        String docId,
        String clienteId,
        String tipologia,
        String fecha,
        String monto
    ) {
        this.status         = status;
        this.statusColorRes = statusColorRes;
        this.project        = project;
        this.location       = location;
        this.company        = company;
        this.actionLabel    = actionLabel;
        this.confirmed      = confirmed;
        this.docId          = docId != null ? docId : "";
        this.clienteId      = clienteId != null ? clienteId : "";
        this.tipologia      = tipologia != null ? tipologia : "";
        this.fecha          = fecha != null ? fecha : "";
        this.monto          = monto != null ? monto : "";
    }

    public String getStatus()       { return status; }
    public int getStatusColorRes()  { return statusColorRes; }
    public String getProject()      { return project; }
    public String getLocation()     { return location; }
    public String getCompany()      { return company; }
    public String getActionLabel()  { return actionLabel; }
    public boolean isConfirmed()    { return confirmed; }
    public String getDocId()        { return docId; }
    public String getClienteId()    { return clienteId; }
    public String getTipologia()    { return tipologia; }
    public String getFecha()        { return fecha; }
    public String getMonto()        { return monto; }
}

package com.example.inmia.asesor;

public class CitaItem {

    private final String status;
    private final int statusColorRes;
    private final float statusAlpha;
    private final String project;
    private final String client;
    private final String location;
    private final String dateTime;
    private final String docId;
    private final String inmobiliariaId;

    public CitaItem(
        String status,
        int statusColorRes,
        float statusAlpha,
        String project,
        String client,
        String location,
        String dateTime
    ) {
        this(status, statusColorRes, statusAlpha, project, client, location, dateTime, "", "");
    }

    public CitaItem(
        String status,
        int statusColorRes,
        float statusAlpha,
        String project,
        String client,
        String location,
        String dateTime,
        String docId,
        String inmobiliariaId
    ) {
        this.status         = status;
        this.statusColorRes = statusColorRes;
        this.statusAlpha    = statusAlpha;
        this.project        = project;
        this.client         = client;
        this.location       = location;
        this.dateTime       = dateTime;
        this.docId          = docId != null ? docId : "";
        this.inmobiliariaId = inmobiliariaId != null ? inmobiliariaId : "";
    }

    public String getStatus()        { return status; }
    public int getStatusColorRes()   { return statusColorRes; }
    public float getStatusAlpha()    { return statusAlpha; }
    public String getProject()       { return project; }
    public String getClient()        { return client; }
    public String getLocation()      { return location; }
    public String getDateTime()      { return dateTime; }
    public String getDocId()         { return docId; }
    public String getInmobiliariaId(){ return inmobiliariaId; }
}

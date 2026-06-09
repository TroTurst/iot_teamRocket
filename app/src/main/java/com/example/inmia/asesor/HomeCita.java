package com.example.inmia.asesor;

public class HomeCita {

    private final String time;
    private final String meridian;
    private final String client;
    private final String project;
    private final String status;
    private final boolean confirmed;
    private final String date;
    private final String docId;

    public HomeCita(String time, String meridian, String client, String project, String status, boolean confirmed) {
        this(time, meridian, client, project, status, confirmed, "", "");
    }

    public HomeCita(String time, String meridian, String client, String project, String status, boolean confirmed, String date) {
        this(time, meridian, client, project, status, confirmed, date, "");
    }

    public HomeCita(String time, String meridian, String client, String project, String status, boolean confirmed, String date, String docId) {
        this.time      = time;
        this.meridian  = meridian;
        this.client    = client;
        this.project   = project;
        this.status    = status;
        this.confirmed = confirmed;
        this.date      = date != null ? date : "";
        this.docId     = docId != null ? docId : "";
    }

    public String getTime()      { return time; }
    public String getMeridian()  { return meridian; }
    public String getClient()    { return client; }
    public String getProject()   { return project; }
    public String getStatus()    { return status; }
    public boolean isConfirmed() { return confirmed; }
    public String getDate()      { return date; }
    public String getDocId()     { return docId; }
}

package com.example.inmia.asesor;

public class CitaItem {

    private final String status;
    private final int statusColorRes;
    private final float statusAlpha;
    private final String project;
    private final String client;
    private final String location;
    private final String dateTime;

    public CitaItem(
        String status,
        int statusColorRes,
        float statusAlpha,
        String project,
        String client,
        String location,
        String dateTime
    ) {
        this.status = status;
        this.statusColorRes = statusColorRes;
        this.statusAlpha = statusAlpha;
        this.project = project;
        this.client = client;
        this.location = location;
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public int getStatusColorRes() {
        return statusColorRes;
    }

    public float getStatusAlpha() {
        return statusAlpha;
    }

    public String getProject() {
        return project;
    }

    public String getClient() {
        return client;
    }

    public String getLocation() {
        return location;
    }

    public String getDateTime() {
        return dateTime;
    }
}

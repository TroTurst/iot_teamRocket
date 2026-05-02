package com.example.inmia.asesor;

public class SeparacionItem {

    private final String status;
    private final int statusColorRes;
    private final String project;
    private final String location;
    private final String company;
    private final String actionLabel;
    private final boolean confirmed;

    public SeparacionItem(
        String status,
        int statusColorRes,
        String project,
        String location,
        String company,
        String actionLabel,
        boolean confirmed
    ) {
        this.status = status;
        this.statusColorRes = statusColorRes;
        this.project = project;
        this.location = location;
        this.company = company;
        this.actionLabel = actionLabel;
        this.confirmed = confirmed;
    }

    public String getStatus() {
        return status;
    }

    public int getStatusColorRes() {
        return statusColorRes;
    }

    public String getProject() {
        return project;
    }

    public String getLocation() {
        return location;
    }

    public String getCompany() {
        return company;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}

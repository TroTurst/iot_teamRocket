package com.example.inmia.admin.model;

public class AdminCompany {

    private final String id;
    private final String name;
    private final String ruc;
    private final String address;
    private final String phone;
    private final String logoUrl;
    private final String status;

    public AdminCompany(String id, String name, String ruc, String address, String phone, String logoUrl, String status) {
        this.id = id;
        this.name = name;
        this.ruc = ruc;
        this.address = address;
        this.phone = phone;
        this.logoUrl = logoUrl;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRuc() {
        return ruc;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getStatus() {
        return status;
    }
}


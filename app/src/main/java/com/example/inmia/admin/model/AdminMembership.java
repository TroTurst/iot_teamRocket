package com.example.inmia.admin.model;

public class AdminMembership {

    private final String id;
    private final String userId;
    private final String companyId;
    private final AdminRole role;
    private final String status;

    public AdminMembership(String id, String userId, String companyId, AdminRole role, String status) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.role = role;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public AdminRole getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}


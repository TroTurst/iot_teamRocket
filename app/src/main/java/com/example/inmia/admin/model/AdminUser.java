package com.example.inmia.admin.model;

public class AdminUser {

    private final String id;
    private final String email;
    private final String displayName;
    private final String status;

    public AdminUser(String id, String email, String displayName, String status) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStatus() {
        return status;
    }
}


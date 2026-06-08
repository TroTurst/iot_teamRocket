package com.example.inmia.admin.model;

public enum AdminRole {
    SUPERADMIN,
    ADMIN_EMPRESA,
    ASESOR,
    CLIENTE;

    public static AdminRole fromRaw(String value) {
        if (value == null) {
            return null;
        }
        try {
            return AdminRole.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}


package com.example.inmia.admin.data;

public final class AdminRepositoryProvider {

    private static AdminRepository instance;

    private AdminRepositoryProvider() {
    }

    public static AdminRepository get() {
        if (instance == null) {
            instance = new AdminRepositoryLocal(new AdminLocalDataSource());
        }
        return instance;
    }
}


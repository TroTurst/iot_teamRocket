package com.example.inmia.superadmin;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME     = "inmia_sa_session";
    private static final String KEY_NOMBRE     = "sa_nombre";
    private static final String KEY_EMAIL      = "sa_email";
    private static final String KEY_NOTIF_COUNT = "sa_notif_count";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void guardarSesion(String nombre, String email) {
        prefs.edit()
                .putString(KEY_NOMBRE, nombre)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public String getNombre() {
        return prefs.getString(KEY_NOMBRE, "Superadmin");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "superadmin@inmia.com");
    }

    public void incrementarNotificaciones() {
        int actual = prefs.getInt(KEY_NOTIF_COUNT, 0);
        prefs.edit().putInt(KEY_NOTIF_COUNT, actual + 1).apply();
    }

    public int getNotificacionCount() {
        return prefs.getInt(KEY_NOTIF_COUNT, 0);
    }

    public void limpiarNotificaciones() {
        prefs.edit().putInt(KEY_NOTIF_COUNT, 0).apply();
    }

    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }
}

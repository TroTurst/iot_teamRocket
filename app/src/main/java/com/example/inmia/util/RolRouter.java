package com.example.inmia.util;

import android.app.Activity;
import android.content.Intent;

import com.example.inmia.admin.AdminHomeActivity;
import com.example.inmia.admin.RegistroInmobiliariaActivity;
import com.example.inmia.asesor.AsesorHomeActivity;
import com.example.inmia.cliente.ClienteHomeActivity;
import com.example.inmia.superadmin.SessionManager;
import com.example.inmia.superadmin.SuperAdminHomeActivity;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Resuelve a qué Activity debe navegar un usuario según el campo "rol" de su
 * documento en usuarios/{uid}. Usado tanto por el login explícito como por el
 * salto automático al reabrir la app con una sesión de Firebase Auth ya activa.
 */
public final class RolRouter {

    private RolRouter() {
    }

    /** Devuelve el Intent al home correspondiente, o null si el rol es desconocido. */
    public static Intent resolverIntentDestino(Activity origen, DocumentSnapshot doc, String email) {
        String rol = doc.getString("rol");

        if ("superadmin".equals(rol)) {
            new SessionManager(origen).guardarSesion("Superadmin", email);
        }

        switch (rol != null ? rol : "") {
            case "cliente":
                return new Intent(origen, ClienteHomeActivity.class);
            case "asesor":
                return new Intent(origen, AsesorHomeActivity.class);
            case "admin":
                Boolean primeraVez = doc.getBoolean("esPrimeraVez");
                return new Intent(origen, Boolean.TRUE.equals(primeraVez)
                        ? RegistroInmobiliariaActivity.class
                        : AdminHomeActivity.class);
            case "superadmin":
                return new Intent(origen, SuperAdminHomeActivity.class);
            default:
                return null;
        }
    }
}

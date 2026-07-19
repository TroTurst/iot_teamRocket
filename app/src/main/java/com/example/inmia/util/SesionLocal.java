package com.example.inmia.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Bandera propia (independiente del estado interno de FirebaseAuth) que indica si el
 * usuario cerró sesión explícitamente. FirebaseAuth persiste su propio estado de forma
 * asíncrona: si el proceso muere justo después de signOut(), esa escritura puede no
 * llegar a completarse y el siguiente arranque vería el usuario anterior otra vez.
 * Aquí se usa commit() (síncrono) al cerrar sesión para que quede garantizado en disco
 * antes de que la app pueda cerrarse.
 */
public final class SesionLocal {

    private static final String PREFS = "inmia_sesion_local";
    private static final String KEY_ACTIVA = "sesion_activa";

    private SesionLocal() {
    }

    /** Se llama justo después de un login exitoso. */
    public static void marcarActiva(Context context) {
        prefs(context).edit().putBoolean(KEY_ACTIVA, true).apply();
    }

    /** Se llama justo antes/junto con FirebaseAuth.signOut() en un logout explícito. */
    public static void marcarCerrada(Context context) {
        prefs(context).edit().putBoolean(KEY_ACTIVA, false).commit();
    }

    public static boolean estaActiva(Context context) {
        return prefs(context).getBoolean(KEY_ACTIVA, false);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}

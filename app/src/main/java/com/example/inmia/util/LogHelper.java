package com.example.inmia.util;

import com.example.inmia.models.Log;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper centralizado para registrar eventos del sistema en la colección
 * "logs" de Firestore. El superadmin los visualiza en {@code LogsActivity}.
 *
 * Estructura de cada documento en "logs":
 *   descripcion   : texto legible del evento
 *   tipo          : uno de Log.TIPO_*  (define ícono/color)
 *   rol           : "cliente" | "asesor" | "admin" | "superadmin"
 *   usuarioNombre : nombre del actor (opcional)
 *   usuarioId     : uid del actor (opcional)
 *   fechaCreacion : Timestamp del servidor (para filtrar por rango)
 *
 * La colección "logs" se crea automáticamente al escribir el primer documento.
 */
public final class LogHelper {

    // Roles de quien origina el evento
    public static final String ROL_CLIENTE    = "cliente";
    public static final String ROL_ASESOR     = "asesor";
    public static final String ROL_ADMIN      = "admin";
    public static final String ROL_SUPERADMIN = "superadmin";

    private LogHelper() { }

    /** Registra un log sin datos del actor. */
    public static void registrar(String descripcion, String tipo, String rol) {
        registrar(descripcion, tipo, rol, "", "");
    }

    /** Registra un log con nombre e id del actor. */
    public static void registrar(String descripcion, String tipo, String rol,
                                 String usuarioNombre, String usuarioId) {
        Map<String, Object> log = new HashMap<>();
        log.put("descripcion",   descripcion != null ? descripcion : "");
        log.put("tipo",          tipo != null ? tipo : Log.TIPO_CUENTA);
        log.put("rol",           rol != null ? rol : "");
        log.put("usuarioNombre", usuarioNombre != null ? usuarioNombre : "");
        log.put("usuarioId",     usuarioId != null ? usuarioId : "");
        log.put("fechaCreacion", FieldValue.serverTimestamp());

        // Escritura "fire and forget": si falla no debe afectar la acción del usuario.
        FirebaseFirestore.getInstance().collection("logs").add(log);
    }
}

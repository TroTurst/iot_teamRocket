package com.example.inmia.asesor;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Revisa en Firestore si hay citas o separaciones nuevas asignadas al asesor
 * desde la última sincronización, y genera una notificación real (local +
 * push del sistema) por cada una encontrada. Reemplaza el seed hardcodeado
 * que antes mostraba datos de ejemplo ficticios.
 */
public final class AsesorNotificacionSyncer {

    private static final String PREFS_NAME = "inmia_asesor_local_storage";
    private static final String KEY_LAST_SYNC = "asesor_notif_last_sync";

    private AsesorNotificacionSyncer() {
    }

    public static void sincronizar(Context context) {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
            ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (uid == null || uid.isEmpty()) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Primera vez: no se notifican citas/separaciones históricas, solo las
        // que se creen a partir de ahora.
        long ultimaSyncMillis = prefs.getLong(KEY_LAST_SYNC, System.currentTimeMillis());
        long ahoraMillis = System.currentTimeMillis();
        Timestamp desde = new Timestamp(ultimaSyncMillis / 1000L, 0);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("citas")
            .whereEqualTo("asesorId", uid)
            .whereGreaterThan("fechaCreacion", desde)
            .get()
            .addOnSuccessListener(snap -> {
                Context appContext = context.getApplicationContext();
                for (QueryDocumentSnapshot doc : snap) {
                    String proyecto = doc.getString("nombreProyecto");
                    AsesorNotificacionHelper.enviar(
                        appContext,
                        "Nueva cita agendada",
                        "Se agendó una visita para " + (proyecto != null ? proyecto : "un proyecto"),
                        AsesorNotificacionStore.TIPO_CITA_CONFIRMADA,
                        AsesorNotificacionStore.TARGET_CITA_DETAIL,
                        doc.getId()
                    );
                }
            });

        db.collection("separaciones")
            .whereEqualTo("asesorId", uid)
            .whereGreaterThan("fechaCreacion", desde)
            .get()
            .addOnSuccessListener(snap -> {
                Context appContext = context.getApplicationContext();
                for (QueryDocumentSnapshot doc : snap) {
                    String proyecto = doc.getString("nombreProyecto");
                    AsesorNotificacionHelper.enviar(
                        appContext,
                        "Nueva separación solicitada",
                        "Se solicitó una separación para " + (proyecto != null ? proyecto : "un proyecto"),
                        AsesorNotificacionStore.TIPO_SEPARACION_SOLICITADA,
                        AsesorNotificacionStore.TARGET_SEPARACION_DETAIL,
                        doc.getId()
                    );
                }
            });

        prefs.edit().putLong(KEY_LAST_SYNC, ahoraMillis).apply();
    }
}

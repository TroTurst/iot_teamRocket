package com.example.inmia.cliente;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class NotificacionHelper {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static void crearNotifMensaje(String usuarioId, String nombreAsesor, String chatId) {
        Map<String, Object> notif = new HashMap<>();
        notif.put("usuarioId", usuarioId);
        notif.put("tipo", "MENSAJE");
        notif.put("titulo", "Nuevo mensaje");
        notif.put("texto", nombreAsesor + " te ha enviado un mensaje");
        notif.put("leido", false);
        notif.put("fechaCreacion", Timestamp.now());
        notif.put("referenciaId", chatId);
        guardar(notif);
    }


    public static void crearNotifSeparacion(String usuarioId, String nombreProyecto,
                                            String estado, String separacionId) {
        boolean esExito = "ACEPTADA".equals(estado);
        Map<String, Object> notif = new HashMap<>();
        notif.put("usuarioId", usuarioId);
        notif.put("tipo", "SEPARACION");
        notif.put("titulo", esExito ? "Separación confirmada" : "Separación rechazada");
        notif.put("texto", esExito
                ? "Tu separación para " + nombreProyecto + " fue aceptada"
                : "Tu separación para " + nombreProyecto + " fue rechazada");
        notif.put("leido", false);
        notif.put("fechaCreacion", Timestamp.now());
        notif.put("referenciaId", separacionId);
        guardar(notif);
    }


    public static void crearNotifCita(String usuarioId, String nombreAsesor,
                                      String fechaCita, String citaId) {
        Map<String, Object> notif = new HashMap<>();
        notif.put("usuarioId", usuarioId);
        notif.put("tipo", "CITA");
        notif.put("titulo", "Recordatorio de visita");
        notif.put("texto", "Mañana tienes una visita con " + nombreAsesor + " a las " + fechaCita);
        notif.put("leido", false);
        notif.put("fechaCreacion", Timestamp.now());
        notif.put("referenciaId", citaId);
        guardar(notif);
    }


    private static void guardar(Map<String, Object> notif) {
        db.collection("notificaciones")
                .add(notif)
                .addOnFailureListener(e ->
                        android.util.Log.e("NotificacionHelper", "Error al guardar notif", e));
    }
}
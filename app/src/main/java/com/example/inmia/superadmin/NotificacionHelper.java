package com.example.inmia.superadmin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.inmia.R;
import com.example.inmia.superadmin.db.AppDatabase;
import com.example.inmia.superadmin.db.NotificacionSAEntity;

public class NotificacionHelper {

    public static final String TIPO_ADMIN_CREADO            = "admin_creado";
    public static final String TIPO_NUEVA_SOLICITUD_ASESOR  = "nueva_solicitud_asesor";
    public static final String TIPO_ASESOR_HABILITADO       = "asesor_habilitado";
    public static final String TIPO_ASESOR_RECHAZADO     = "asesor_rechazado";
    public static final String TIPO_USUARIO_ACTIVADO     = "usuario_activado";
    public static final String TIPO_USUARIO_DESACTIVADO  = "usuario_desactivado";

    private static final String CHANNEL_ID   = "inmia_sa_channel";
    private static final String CHANNEL_NAME = "INMIA Superadmin";
    private static int notifId = 2000;

    public static void crearCanal(Context context) {
        NotificationChannel canal = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        canal.setDescription("Notificaciones del panel de superadministrador");
        NotificationManager manager =
                context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(canal);
    }

    /**
     * Muestra SOLO una notificación del sistema en el dispositivo (sin guardar en
     * Room ni tocar el badge). Se usa para avisar al superadmin de eventos externos,
     * p. ej. una nueva solicitud de asesor. Al tocarla abre la pantalla de solicitudes.
     */
    public static void notificarSistema(Context context, String titulo, String descripcion) {
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, SolicitudesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(titulo)
                .setContentText(descripcion)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(context).notify(notifId++, notification);
    }

    public static void enviar(Context context, String titulo, String descripcion, String tipo) {
        // 1. Guardar en Room (historial in-app)
        NotificacionSAEntity entity = new NotificacionSAEntity();
        entity.titulo       = titulo;
        entity.descripcion  = descripcion;
        entity.tipo         = tipo;
        entity.timestamp    = System.currentTimeMillis();
        entity.leida        = false;
        AppDatabase.getInstance(context).notificacionDao().insertar(entity);

        // 2. Actualizar badge en SharedPreferences
        new SessionManager(context).incrementarNotificaciones();

        // 3. Mostrar notificación del sistema (solo si hay permiso)
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, NotificacionesSuperAdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(titulo)
                .setContentText(descripcion)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(context).notify(notifId++, notification);
    }
}

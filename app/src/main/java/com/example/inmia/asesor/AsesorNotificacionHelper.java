package com.example.inmia.asesor;

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

public final class AsesorNotificacionHelper {

    private static final String CHANNEL_ID = "inmia_asesor_alerts_channel";
    private static final String CHANNEL_NAME = "INMIA Asesor";
    private static int notifId = 3000;

    private AsesorNotificacionHelper() {
    }

    public static void crearCanal(Context context) {
        NotificationChannel canal = new NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        );
        canal.setDescription("Notificaciones del asesor");
        canal.enableVibration(true);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(canal);
        }
    }

    public static void enviar(Context context, String titulo, String descripcion, String tipo) {
        enviar(context, titulo, descripcion, tipo, null, null);
    }

    public static void enviar(Context context, String titulo, String descripcion, String tipo, String targetType, String targetId) {
        crearCanal(context);
        AsesorNotificacionStore.addNotification(context, titulo, descripcion, tipo, targetType, targetId);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, AsesorNotificacionesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(titulo)
            .setContentText(descripcion)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build();

        NotificationManagerCompat.from(context).notify(notifId++, notification);
    }
}
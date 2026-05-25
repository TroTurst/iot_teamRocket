package com.example.inmia.admin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class AdminSimularNotificacionesActivity extends AppCompatActivity {

    private static final String CHANNEL_IMPORTANTE_HIGH = "admin_importante_high";
    private static final String CHANNEL_IMPORTANTE_DEFAULT = "admin_importante_default";
    private static final String CHANNEL_IMPORTANTE_LOW = "admin_importante_low";
    private static final String CHANNEL_IMPORTANCE_MIN = "admin_importance_min";
    private static final String CHANNEL_IMPORTANCE_MIN_SIN_DOT = "admin_importance_min_sin_dot";

    private BottomNavigationView bottomNav;
    private int notificationCounter = 2000;
    private final Handler notificationHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_simular_notificaciones);

        bottomNav = findViewById(R.id.bottomNavAdmin);

        MaterialButton btnMensajeAsesor = findViewById(R.id.btnNotifMensajeAsesor);
        MaterialButton btnUnidadVendida = findViewById(R.id.btnNotifUnidadVendida);
        MaterialButton btnUnidadSeparada = findViewById(R.id.btnNotifUnidadSeparada);
        MaterialButton btnEstadoProyecto = findViewById(R.id.btnNotifEstadoProyecto);
        MaterialButton btnResultadosMes = findViewById(R.id.btnNotifResultadosMes);
        MaterialButton btnDescarga = findViewById(R.id.btnNotifDescarga);
        MaterialButton btnAdministrativa = findViewById(R.id.btnNotifAdministrativa);
        MaterialButton btnPoliticas = findViewById(R.id.btnNotifPoliticas);

        crearCanalesNotificacion();

        btnMensajeAsesor.setOnClickListener(v -> notificar(new NotifSpec(
                "Mensaje con asesor",
                "Tienes un nuevo mensaje del asesor Carlos.",
                CHANNEL_IMPORTANTE_HIGH,
                true)));
        btnUnidadVendida.setOnClickListener(v -> notificar(new NotifSpec(
                "Unidad en proyecto vendida",
                "Se vendio la unidad A-120 del proyecto Palm Living.",
                CHANNEL_IMPORTANTE_HIGH,
                true)));
        btnPoliticas.setOnClickListener(v -> notificar(new NotifSpec(
                "Politicas y Seguridad",
                "Se actualizaron las politicas de seguridad del sistema.",
                CHANNEL_IMPORTANTE_HIGH,
                true)));

        btnUnidadSeparada.setOnClickListener(v -> notificar(new NotifSpec(
                "Unidad en proyecto separada",
                "La unidad B-405 fue separada por un cliente.",
                CHANNEL_IMPORTANTE_DEFAULT,
                true)));
        btnAdministrativa.setOnClickListener(v -> notificar(new NotifSpec(
                "Administrativa",
                "Nueva solicitud administrativa pendiente de revision.",
                CHANNEL_IMPORTANTE_DEFAULT,
                true)));

        btnEstadoProyecto.setOnClickListener(v -> notificar(new NotifSpec(
                "Actualizacion del estado de un proyecto",
                "El proyecto Oasis cambio a estado: En construccion.",
                CHANNEL_IMPORTANTE_LOW,
                true)));

        btnResultadosMes.setOnClickListener(v -> notificar(new NotifSpec(
                "Resultados de este mes",
                "Tus indicadores del mes ya estan disponibles.",
                CHANNEL_IMPORTANCE_MIN,
                true)));
        btnDescarga.setOnClickListener(v -> notificar(new NotifSpec(
                "Descarga",
                "El reporte mensual se descargo correctamente.",
                CHANNEL_IMPORTANCE_MIN_SIN_DOT,
                false)));

        bottomNav.setSelectedItemId(R.id.nav_perfil);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                navegarATab(AdminHomeActivity.class);
                return true;
            } else if (id == R.id.nav_proyectos) {
                navegarATab(AdminProyectosActivity.class);
                return true;
            } else if (id == R.id.nav_asesores) {
                navegarATab(AdminAsesoresActivity.class);
                return true;
            } else if (id == R.id.nav_reportes) {
                navegarATab(AdminReportesActivity.class);
                return true;
            } else if (id == R.id.nav_perfil) {
                navegarATab(AdminPerfilActivity.class);
                return true;
            }
            return false;
        });
    }

    private void crearCanalesNotificacion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager = ContextCompat.getSystemService(this, NotificationManager.class);
        if (manager == null) {
            return;
        }

        NotificationChannel high = new NotificationChannel(
                CHANNEL_IMPORTANTE_HIGH,
                "Importante High",
                NotificationManager.IMPORTANCE_HIGH);
        high.setShowBadge(true);

        NotificationChannel def = new NotificationChannel(
                CHANNEL_IMPORTANTE_DEFAULT,
                "Importante Default",
                NotificationManager.IMPORTANCE_DEFAULT);
        def.setShowBadge(true);

        NotificationChannel low = new NotificationChannel(
                CHANNEL_IMPORTANTE_LOW,
                "Importante Low",
                NotificationManager.IMPORTANCE_LOW);
        low.setShowBadge(true);

        NotificationChannel min = new NotificationChannel(
                CHANNEL_IMPORTANCE_MIN,
                "Importancia Min",
                NotificationManager.IMPORTANCE_MIN);
        min.setShowBadge(true);

        NotificationChannel minSinDot = new NotificationChannel(
                CHANNEL_IMPORTANCE_MIN_SIN_DOT,
                "Importancia Min sin dot",
                NotificationManager.IMPORTANCE_MIN);
        minSinDot.setShowBadge(false);

        manager.createNotificationChannel(high);
        manager.createNotificationChannel(def);
        manager.createNotificationChannel(low);
        manager.createNotificationChannel(min);
        manager.createNotificationChannel(minSinDot);
    }

    private void notificar(NotifSpec spec) {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Toast.makeText(this, "Permite notificaciones para ver la simulacion", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = ContextCompat.getSystemService(this, NotificationManager.class);
            if (manager != null) {
                NotificationChannel channel = manager.getNotificationChannel(spec.channelId);
                if (channel != null) {
                    channel.setShowBadge(spec.showBadge);
                    manager.createNotificationChannel(channel);
                }
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, spec.channelId)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(spec.title)
                .setContentText(spec.message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(spec.message))
                .setAutoCancel(true);

        moveTaskToBack(true);
        notificationHandler.postDelayed(() ->
                NotificationManagerCompat.from(this).notify(notificationCounter++, builder.build()),
                1000);
    }

    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private static class NotifSpec {
        final String title;
        final String message;
        final String channelId;
        final boolean showBadge;

        NotifSpec(String title, String message, String channelId, boolean showBadge) {
            this.title = title;
            this.message = message;
            this.channelId = channelId;
            this.showBadge = showBadge;
        }
    }
}

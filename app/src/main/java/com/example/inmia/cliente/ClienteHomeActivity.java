package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.ArrayList;
import java.util.List;

public class ClienteHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private EditText etSearch;
    private ImageButton btnLocation;
    private Button btnSearch;
    private ChipGroup chipGroupFilters;
    private RecyclerView rvProyectos;
    private TextView tvSearchDummy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_cliente_home2_cliente);

        inicializarVistas();
        configurarListeners();
        createNotificationChannel();
    }

    private void inicializarVistas() {
        etSearch = findViewById(R.id.etSearch);
        btnLocation = findViewById(R.id.btnLocation);
        btnSearch = findViewById(R.id.btnSearch);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        bottomNav = findViewById(R.id.bottomNavCliente);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        RecyclerView rvProyectos = findViewById(R.id.rvProyectos);
        rvProyectos.setLayoutManager(new GridLayoutManager(this, 2));
        List<Proyecto> misProyectos = new ArrayList<>();

        misProyectos.add(new Proyecto("Palm Living", "San Isidro, Lima", "Desde S./85,000", "Planos", R.drawable.onboarding1));
        misProyectos.add(new Proyecto("Verde Living", "Miraflores", "Desde S./120,000", "Planos", R.drawable.onboarding2));
        misProyectos.add(new Proyecto("Park Side", "Pueblo Libre", "Desde S./72,000", "En preventa", R.drawable.onboarding3));
        misProyectos.add(new Proyecto("Ocean View", "Magdalena", "Desde S./95,000", "Venta", R.drawable.onboarding1));
        for (int i = 4; i <= 13; i++) {
            misProyectos.add(new Proyecto(
                    "Proyecto Extra " + i,
                    "Distrito " + i,
                    "Desde S./100,000",
                    "Venta",
                    R.drawable.onboarding1
            ));
        }
        ProyectosAdapter adapter = new ProyectosAdapter(misProyectos);
        rvProyectos.setAdapter(adapter);

        tvSearchDummy = findViewById(R.id.etSearchReal);

    }

    private void configurarListeners() {


        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteBuzonNotificacionesActivity.class));
        });

        etSearch.setFocusable(false);
        etSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteExplorarMapaActivity.class));
        });



        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    return true;
                } else if (id == R.id.nav_citas) {
                    startActivity(new Intent(this, ClienteCitasActivity.class));
                    return true;
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(this, ClienteMensajesActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(this, ClientePerfilClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_separaciones) {
                    startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                    return true;
                }
                return false;
            });
        }
        btnLocation.setOnClickListener(v -> {
            Intent intent = new Intent(ClienteHomeActivity.this, ClienteExplorarMapaActivity.class);
            startActivity(intent);
        });

        etSearch.setFocusable(false);
        etSearch.setOnClickListener(v -> {
            startActivity(new Intent(ClienteHomeActivity.this, ClienteBuscarActivity.class));
        });

        frameNotificaciones.setOnLongClickListener(v -> {
            lanzarNotificacionDemo();
            Toast.makeText(this, "Simulando notificación...", Toast.LENGTH_SHORT).show();
            return true;
        });
    }
    private void lanzarNotificacionDemo() {

        int tipoNotificacion = new java.util.Random().nextInt(3);

        String titulo = "";
        String texto = "";
        Intent intent = null;
        int notifId = 0;


        switch (tipoNotificacion) {
            case 0:
                titulo = "Nuevo mensaje de tu asesor";
                texto = "Carlos Mendoza: NOTIFICACION DE PRUEBA";
                intent = new Intent(this, ClienteMensajesActivity.class);
                notifId = 1001;
                break;
            case 1:
                titulo = "Cita confirmada";
                texto = "NOTIFICACION DE PRUEBA";
                intent = new Intent(this, ClienteCitasActivity.class);
                notifId = 1002;
                break;
            case 2:
                titulo = "Separación actualizada";
                texto = "NOTIFICACION DE PRUEBA";
                intent = new Intent(this, ClienteSeparacionesActivity.class);
                notifId = 1003;
                break;
        }

        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this, notifId, intent,
                android.app.PendingIntent.FLAG_IMMUTABLE | android.app.PendingIntent.FLAG_UPDATE_CURRENT);


        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, "canal_inmia_default")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        androidx.core.app.NotificationManagerCompat notificationManager = androidx.core.app.NotificationManagerCompat.from(this);
        if (androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notifId, builder.build());
        }
    }

    private void abrirDetalleProyecto(String nombreProyecto) {
        Intent intent = new Intent(this, ClienteDetallePropiedadActivity.class);
        intent.putExtra("PROYECTO_NOMBRE", nombreProyecto);
        startActivity(intent);
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    "canal_inmia_default",
                    "Canal de Notificaciones INMIA",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para notificaciones de reservas y mensajes");
            android.app.NotificationManager notificationManager = getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        askPermission();
    }

    private void askPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_DENIED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
    }
}
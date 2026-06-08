package com.example.inmia.cliente;

import com.example.inmia.models.Proyecto;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClienteHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private EditText etSearch;
    private ImageButton btnLocation;
    private Button btnSearch;
    private ChipGroup chipGroupFilters;
    private RecyclerView rvProyectos;
    private TextView tvSearchDummy;

    private FirebaseFirestore db;
    private ProyectosAdapter adapter;
    private List<Proyecto> misProyectos;
    private List<Proyecto> proyectosOriginales = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_cliente_home2_cliente);

        db = FirebaseFirestore.getInstance();

        inicializarVistas();
        configurarListeners();
        createNotificationChannel();

        cargarProyectosDesdeFirestore();
        inyectarProyectoUnico();
    }

    private void inicializarVistas() {
        etSearch = findViewById(R.id.etSearch);
        btnLocation = findViewById(R.id.btnLocation);
        btnSearch = findViewById(R.id.btnSearch);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        bottomNav = findViewById(R.id.bottomNavCliente);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvSearchDummy = findViewById(R.id.etSearchReal);

        rvProyectos = findViewById(R.id.rvProyectos);
        rvProyectos.setLayoutManager(new GridLayoutManager(this, 2));

        misProyectos = new ArrayList<>();
        adapter = new ProyectosAdapter(misProyectos);
        rvProyectos.setAdapter(adapter);
    }

    private void cargarProyectosDesdeFirestore() {
        db.collection("proyectos")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.w("FirestoreError", "Error al escuchar proyectos.", error);
                        Toast.makeText(this, "Error al cargar proyectos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshot != null) {
                        misProyectos.clear();
                        proyectosOriginales.clear(); // Limpiamos también el respaldo

                        for (QueryDocumentSnapshot doc : snapshot) {
                            com.example.inmia.models.Proyecto nuevoProyecto = new com.example.inmia.models.Proyecto();

                            nuevoProyecto.setId(doc.getId());
                            nuevoProyecto.setNombre(doc.getString("nombre"));

                            Map<String, Object> ubicacionMap = (Map<String, Object>) doc.get("ubicacion");
                            nuevoProyecto.setUbicacion((ubicacionMap != null && ubicacionMap.containsKey("direccion"))
                                    ? (String) ubicacionMap.get("direccion") : "Ubicación no disponible");

                            String estadoRaw = doc.getString("estado");
                            if ("en_planos".equals(estadoRaw)) nuevoProyecto.setEstadoProyecto("Planos");
                            else if ("en_preventa".equals(estadoRaw)) nuevoProyecto.setEstadoProyecto("Preventa");
                            else nuevoProyecto.setEstadoProyecto("Venta");

                            nuevoProyecto.setImagenHeroPrincipal(R.drawable.onboarding1);

                            List<Map<String, Object>> tipologiasData = (List<Map<String, Object>>) doc.get("tipologias");
                            List<com.example.inmia.models.Tipologia> listaTipologias = new ArrayList<>();

                            if (tipologiasData != null) {
                                for (Map<String, Object> map : tipologiasData) {
                                    com.example.inmia.models.Tipologia t = new com.example.inmia.models.Tipologia();
                                    if (map.containsKey("precio")) {
                                        t.setPrecio(String.valueOf(map.get("precio")));
                                    }
                                    listaTipologias.add(t);
                                }
                            }
                            nuevoProyecto.setTipologias(listaTipologias);

                            misProyectos.add(nuevoProyecto);
                            proyectosOriginales.add(nuevoProyecto); // <-- Guardamos en el respaldo
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void configurarListeners() {
        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteBuzonNotificacionesActivity.class));
        });

        // Buscador Dinámico en tiempo real
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textoBusqueda = s.toString().toLowerCase().trim();
                misProyectos.clear(); // Limpiamos la lista actual mostrada

                if (textoBusqueda.isEmpty()) {
                    // Si el buscador está vacío, mostramos todos
                    misProyectos.addAll(proyectosOriginales);
                } else {
                    // Filtramos buscando coincidencias en el nombre
                    for (com.example.inmia.models.Proyecto p : proyectosOriginales) {
                        if (p.getNombre() != null && p.getNombre().toLowerCase().contains(textoBusqueda)) {
                            misProyectos.add(p);
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // Actualizamos el RecyclerView
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // El botón de ubicación abre el mapa
        btnLocation.setOnClickListener(v -> {
            Intent intent = new Intent(ClienteHomeActivity.this, ClienteExplorarMapaActivity.class);
            startActivity(intent);
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

    private void inyectarProyectoUnico() {
        db.collection("proyectos").limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                java.util.Map<String, Object> proyectoPrueba = new java.util.HashMap<>();
                proyectoPrueba.put("nombre", "Palm Living");
                proyectoPrueba.put("estado", "en_venta");

                java.util.Map<String, Object> ubicacion = new java.util.HashMap<>();
                ubicacion.put("direccion", "San Isidro, Lima");
                proyectoPrueba.put("ubicacion", ubicacion);

                java.util.Map<String, Object> tipologia = new java.util.HashMap<>();
                tipologia.put("precio", 85000.0);
                proyectoPrueba.put("tipologias", java.util.Arrays.asList(tipologia));

                db.collection("proyectos").add(proyectoPrueba)
                        .addOnSuccessListener(documentReference -> {
                            android.widget.Toast.makeText(this, "¡Proyecto semilla creado en Firebase!", android.widget.Toast.LENGTH_LONG).show();
                        });
            }
        });
    }
}
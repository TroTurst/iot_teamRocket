package com.example.inmia.cliente;

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
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClienteHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private FrameLayout frameEscanerQR;
    private EditText etSearch;
    private ImageButton btnLocation;
    private Button btnSearch;
    private ChipGroup chipGroupFilters;
    private RecyclerView rvProyectos;
    private TextView tvSeeAll;

    private FirebaseFirestore db;
    private ProyectosAdapter adapter;
    private List<Proyecto> misProyectos = new ArrayList<>();
    private List<Proyecto> proyectosOriginales = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_cliente_home2_cliente);

        db = FirebaseFirestore.getInstance();

        inicializarVistas();
        configurarListeners();
        createNotificationChannel();
        cargarProyectosDesdeFirestore();
        iniciarEscuchaNotificaciones();
    }


    private void inicializarVistas() {
        etSearch            = findViewById(R.id.etSearch);
        btnLocation         = findViewById(R.id.btnLocation);
        btnSearch           = findViewById(R.id.btnSearch);
        chipGroupFilters    = findViewById(R.id.chipGroupFilters);
        bottomNav           = findViewById(R.id.bottomNavCliente);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        frameEscanerQR      = findViewById(R.id.frameEscanerQR);
        tvSeeAll            = findViewById(R.id.tvSeeAll);

        rvProyectos = findViewById(R.id.rvProyectos);
        rvProyectos.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProyectosAdapter(misProyectos);
        rvProyectos.setAdapter(adapter);
    }


    private void configurarListeners() {

        tvSeeAll.setOnClickListener(v ->
                startActivity(new Intent(this, ClienteVerTodosActivity.class)));

        frameNotificaciones.setOnClickListener(v ->
                startActivity(new Intent(this, ClienteBuzonNotificacionesActivity.class)));

        frameEscanerQR.setOnClickListener(v -> abrirEscanerQR());

        btnLocation.setOnClickListener(v ->
                startActivity(new Intent(this, ClienteExplorarMapaActivity.class)));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                misProyectos.clear();
                if (query.isEmpty()) {
                    misProyectos.addAll(proyectosOriginales);
                } else {
                    for (Proyecto p : proyectosOriginales) {
                        if (p.getNombre() != null && p.getNombre().toLowerCase().contains(query)) {
                            misProyectos.add(p);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if      (id == R.id.nav_inicio)       return true;
                else if (id == R.id.nav_citas)        startActivity(new Intent(this, ClienteCitasActivity.class));
                else if (id == R.id.nav_chat)         startActivity(new Intent(this, ClienteMensajesActivity.class));
                else if (id == R.id.nav_perfil)       startActivity(new Intent(this, ClientePerfilClienteActivity.class));
                else if (id == R.id.nav_separaciones) startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                return id != R.id.nav_inicio;
            });
        }
    }

    private void cargarProyectosDesdeFirestore() {
        db.collection("proyectos")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.w("ClienteHome", "Error al escuchar proyectos.", error);
                        Toast.makeText(this, "Error al cargar proyectos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snapshot == null) return;

                    misProyectos.clear();
                    proyectosOriginales.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Proyecto p = new Proyecto();
                        p.setId(doc.getId());
                        p.setNombre(doc.getString("nombre"));

                        Map<String, Object> ubicacionMap = (Map<String, Object>) doc.get("ubicacion");
                        p.setUbicacion(ubicacionMap != null && ubicacionMap.containsKey("direccion")
                                ? (String) ubicacionMap.get("direccion")
                                : "Ubicación no disponible");

                        String estadoRaw = doc.getString("estado");
                        if ("en_planos".equals(estadoRaw))        p.setEstadoProyecto("Planos");
                        else if ("en_preventa".equals(estadoRaw)) p.setEstadoProyecto("Preventa");
                        else                                       p.setEstadoProyecto("Venta");

                        List<String> imagenesUrls = (List<String>) doc.get("imagenesUrls");
                        if (imagenesUrls != null && !imagenesUrls.isEmpty()
                                && !imagenesUrls.get(0).isEmpty()) {
                            p.setImagenesUrls(imagenesUrls);
                            p.setImagenHeroPrincipal(0);
                        } else {
                            p.setImagenesUrls(null);
                            p.setImagenHeroPrincipal(R.drawable.onboarding1);
                        }

                        List<Map<String, Object>> tipologiasData = (List<Map<String, Object>>) doc.get("tipologias");
                        List<Tipologia> listaTipologias = new ArrayList<>();
                        if (tipologiasData != null) {
                            for (Map<String, Object> map : tipologiasData) {
                                Tipologia t = new Tipologia();
                                if (map.containsKey("precio")) t.setPrecio(String.valueOf(map.get("precio")));
                                listaTipologias.add(t);
                            }
                        }
                        p.setTipologias(listaTipologias);

                        misProyectos.add(p);
                        proyectosOriginales.add(p);
                    }

                    adapter.notifyDataSetChanged();
                });
    }


    private void abrirEscanerQR() {
        com.google.mlkit.vision.codescanner.GmsBarcodeScanner scanner =
                com.google.mlkit.vision.codescanner.GmsBarcodeScanning.getClient(this);

        scanner.startScan()
                .addOnSuccessListener(barcode -> {
                    String nombre = barcode.getRawValue();
                    if (nombre != null && !nombre.isEmpty()) {
                        Toast.makeText(this, "Encontrado: " + nombre, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, ClienteDetallePropiedadActivity.class);
                        intent.putExtra("PROYECTO_NOMBRE", nombre);
                        startActivity(intent);
                    }
                })
                .addOnCanceledListener(() ->
                        Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al escanear: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    "canal_inmia_default",
                    "Canal de Notificaciones INMIA",
                    android.app.NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para notificaciones de reservas y mensajes");
            android.app.NotificationManager nm = getSystemService(android.app.NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
        askPermission();
    }

    private void askPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                androidx.core.content.ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.POST_NOTIFICATIONS)
                        == android.content.pm.PackageManager.PERMISSION_DENIED) {
            androidx.core.app.ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
    }

    private void iniciarEscuchaNotificaciones() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        db.collection("citas")
                .whereEqualTo("clienteId", user.getUid())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.MODIFIED) {
                            String nuevoEstado = dc.getDocument().getString("estado");
                            String idCita = dc.getDocument().getId();

                            if ("confirmada".equals(nuevoEstado)) {
                                NotificacionHelper.crearNotifCita(user.getUid(), "Asesor Inmia",
                                        "confirmada", idCita);
                            }
                        }
                    }
                });

        db.collection("separaciones")
                .whereEqualTo("clienteId", user.getUid())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.MODIFIED) {
                            String nuevoEstado = dc.getDocument().getString("estado");
                            String nombre = dc.getDocument().getString("nombreProyecto");

                            if ("aprobada".equals(nuevoEstado)) {
                                NotificacionHelper.crearNotifSeparacion(user.getUid(), nombre, "ACEPTADA", dc.getDocument().getId());
                            }
                        }
                    }
                });
    }
}
package com.example.inmia.superadmin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Usuario;
import com.example.inmia.superadmin.db.AppDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private MaterialCardView cardSolicitudes;
    private MaterialButton btnVerSolicitudes;
    private TextView tvGreeting;

    // Contadores del dashboard
    private TextView tvContadorInmobiliarias;
    private TextView tvContadorUsuarios;
    private TextView tvContadorReservas;

    // RecyclerView nuevos usuarios
    private RecyclerView recyclerNuevosUsuarios;
    private NuevoUsuarioAdapter nuevoUsuarioAdapter;
    private List<Usuario> listaNuevosUsuarios;

    private int totalSolicitudes = 3;

    private FirebaseFirestore db;

    private final ActivityResultLauncher<String> permisosLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> NotificacionHelper.crearCanal(this)
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_home_superadmin);

        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        bottomNav               = findViewById(R.id.bottomNavSuperAdmin);
        frameNotificaciones     = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif            = findViewById(R.id.tvBadgeNotif);
        cardSolicitudes         = findViewById(R.id.cardSolicitudes);
        btnVerSolicitudes       = findViewById(R.id.btnVerSolicitudes);
        recyclerNuevosUsuarios  = findViewById(R.id.recyclerNuevosUsuarios);
        tvGreeting              = findViewById(R.id.tvGreeting);
        tvContadorInmobiliarias = findViewById(R.id.tvContadorInmobiliarias);
        tvContadorUsuarios      = findViewById(R.id.tvContadorUsuarios);
        tvContadorReservas      = findViewById(R.id.tvContadorReservas);

        // Solicitar permiso de notificaciones (Android 13+)
        solicitarPermisoNotificaciones();

        // Configurar badge y solicitudes
        configurarBadge();
        configurarSolicitudes();

        // Configurar RecyclerView
        listaNuevosUsuarios = new ArrayList<>();
        recyclerNuevosUsuarios.setLayoutManager(new LinearLayoutManager(this));
        nuevoUsuarioAdapter = new NuevoUsuarioAdapter(this, listaNuevosUsuarios);
        recyclerNuevosUsuarios.setAdapter(nuevoUsuarioAdapter);

        // Cargar datos reales de Firebase
        cargarDashboard();
        cargarNombreSuperAdmin();

        // Nuevos usuarios — datos de ejemplo
        inicializarNuevosUsuarios();

        // Campanita → ir a la vista de notificaciones
        frameNotificaciones.setOnClickListener(v ->
                startActivity(new Intent(this, NotificacionesSuperAdminActivity.class)));

        // Card solicitudes → ir a Gestión de Usuarios
        cardSolicitudes.setOnClickListener(v -> irAGestionUsuarios());

        // Botón "Ver solicitudes →"
        btnVerSolicitudes.setOnClickListener(v -> irSolicitudes());

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_usuarios) {
                irAGestionUsuarios();
                return true;
            } else if (id == R.id.nav_reportes) {
                startActivity(new Intent(this, ReportesActivity.class));
                return true;
            } else if (id == R.id.nav_logs) {
                startActivity(new Intent(this, LogsActivity.class));
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }

            return false;
        });
    }

    // ── Carga de datos desde Firestore ──────────────────────────────────────

    private void cargarDashboard() {
        db.collection("usuarios").get()
            .addOnSuccessListener(q ->
                tvContadorUsuarios.setText(String.valueOf(q.size())))
            .addOnFailureListener(e ->
                tvContadorUsuarios.setText("—"));

        db.collection("inmobiliarias").get()
            .addOnSuccessListener(q ->
                tvContadorInmobiliarias.setText(String.valueOf(q.size())))
            .addOnFailureListener(e ->
                tvContadorInmobiliarias.setText("—"));

        db.collection("separaciones").get()
            .addOnSuccessListener(q ->
                tvContadorReservas.setText(String.valueOf(q.size())))
            .addOnFailureListener(e ->
                tvContadorReservas.setText("—"));
    }

    private void inicializarNuevosUsuarios() {
        listaNuevosUsuarios.add(new Usuario(
                "Juan Pérez", "INMIA Miraflores", "JP", true, "admin", "Hace 2 días"));
        listaNuevosUsuarios.add(new Usuario(
                "María García", "INMIA San Isidro", "MG", true, "asesor", "Hace 5 días"));
        listaNuevosUsuarios.add(new Usuario(
                "Carlos Rodríguez", "Sin inmobiliaria", "CR", true, "cliente", "Hace 1 semana"));
        nuevoUsuarioAdapter.notifyDataSetChanged();
    }

    private void cargarNombreSuperAdmin() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    String nombres = doc.getString("nombres");
                    if (nombres != null && !nombres.isEmpty()) {
                        tvGreeting.setText("¡Hola, " + nombres + "!");
                    }
                }
            });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void irAGestionUsuarios() {
        startActivity(new Intent(this, GestionUsuariosActivity.class));
    }

    private void irSolicitudes() {
        startActivity(new Intent(this, SolicitudesActivity.class));
    }

    private void configurarSolicitudes() {
        TextView tvTotal = findViewById(R.id.tvTotalSolicitudes);
        TextView tvDesc  = findViewById(R.id.tvDescSolicitudes);

        tvTotal.setText(String.valueOf(totalSolicitudes));

        if (totalSolicitudes == 1) {
            tvDesc.setText("asesor de ventas\nespera ser habilitado");
        } else {
            tvDesc.setText("asesores de ventas\nesperan ser habilitados");
        }

        if (totalSolicitudes == 0) {
            cardSolicitudes.setVisibility(View.GONE);
        }
    }

    private void configurarBadge() {
        int count = AppDatabase.getInstance(this)
                .notificacionDao().contarNoLeidas();
        if (count > 0) {
            tvBadgeNotif.setText(String.valueOf(count));
            tvBadgeNotif.setVisibility(View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(View.GONE);
        }
    }

    private void solicitarPermisoNotificaciones() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificacionHelper.crearCanal(this);
        } else {
            permisosLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarBadge();
    }
}

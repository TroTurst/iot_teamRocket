package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private MaterialCardView cardSolicitudes;
    private MaterialButton btnVerSolicitudes;

    // ← NUEVO: RecyclerView nuevos usuarios
    private RecyclerView recyclerNuevosUsuarios;
    private NuevoUsuarioAdapter nuevoUsuarioAdapter;
    private List<Usuario> listaNuevosUsuarios;

    // Hardcodeado — luego vendrá de Firebase
    private int totalNotificaciones = 3;
    private int totalSolicitudes    = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_home_superadmin);

        // Vincular vistas
        bottomNav              = findViewById(R.id.bottomNavSuperAdmin);
        frameNotificaciones    = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif           = findViewById(R.id.tvBadgeNotif);
        cardSolicitudes        = findViewById(R.id.cardSolicitudes);
        btnVerSolicitudes      = findViewById(R.id.btnVerSolicitudes);
        recyclerNuevosUsuarios = findViewById(R.id.recyclerNuevosUsuarios);

        // Configurar badge y solicitudes
        configurarBadge();
        configurarSolicitudes();

        // ← NUEVO: Configurar RecyclerView de nuevos usuarios
        inicializarNuevosUsuarios();
        recyclerNuevosUsuarios.setLayoutManager(
                new LinearLayoutManager(this));
        nuevoUsuarioAdapter = new NuevoUsuarioAdapter(
                this, listaNuevosUsuarios);
        recyclerNuevosUsuarios.setAdapter(nuevoUsuarioAdapter);

        // Campanita
        frameNotificaciones.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Tienes " + totalNotificaciones + " notificaciones",
                    Toast.LENGTH_SHORT).show();
            limpiarBadge();
        });

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

    // ── Datos hardcodeados nuevos usuarios ──────────────────────────────────

    private void inicializarNuevosUsuarios() {
        listaNuevosUsuarios = new ArrayList<>();
        listaNuevosUsuarios.add(new Usuario(
                "Juan Pérez",
                "INMIA Miraflores",
                "JP", true,
                "admin",
                "Hace 2 días"));
        listaNuevosUsuarios.add(new Usuario(
                "María García",
                "INMIA San Isidro",
                "MG", true,
                "asesor",
                "Hace 5 días"));
        listaNuevosUsuarios.add(new Usuario(
                "Carlos Rodríguez",
                "Sin inmobiliaria",
                "CR", true,
                "cliente",
                "Hace 1 semana"));
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
        if (totalNotificaciones > 0) {
            tvBadgeNotif.setText(String.valueOf(totalNotificaciones));
            tvBadgeNotif.setVisibility(View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(View.GONE);
        }
    }

    private void limpiarBadge() {
        totalNotificaciones = 0;
        tvBadgeNotif.setVisibility(View.GONE);
    }
}
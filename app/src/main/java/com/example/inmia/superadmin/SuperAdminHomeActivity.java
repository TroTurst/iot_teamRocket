package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class SuperAdminHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private MaterialCardView cardSolicitudes;
    private MaterialButton btnVerSolicitudes;

    // Hardcodeado — luego vendrá de Firebase
    private int totalNotificaciones = 3;
    private int totalSolicitudes    = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_super_admin_home);

        bottomNav           = findViewById(R.id.bottomNavSuperAdmin);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif        = findViewById(R.id.tvBadgeNotif);
        cardSolicitudes     = findViewById(R.id.cardSolicitudes);
        btnVerSolicitudes   = findViewById(R.id.btnVerSolicitudes);

        configurarBadge();
        configurarSolicitudes();

        // Campanita
        frameNotificaciones.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Tienes " + totalNotificaciones + " notificaciones",
                    Toast.LENGTH_SHORT).show();
            limpiarBadge();
        });

        // Card solicitudes → ir directo a Gestión de Usuarios
        cardSolicitudes.setOnClickListener(v -> irAGestionUsuarios());

        // Botón "Ver solicitudes →"
        btnVerSolicitudes.setOnClickListener(v -> irAGestionUsuarios());

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_usuarios) {
                irAGestionUsuarios();
                return true;
            } else if (id == R.id.nav_reportes) {
                // ← CAMBIO: navega a ReportesActivity
                startActivity(new Intent(this, ReportesActivity.class));
                return true;
            } else if (id == R.id.nav_logs) {
                // TODO: navegar a LogsActivity
                Toast.makeText(this, "Logs del sistema", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_perfil) {
                // TODO: navegar a PerfilActivity
                Toast.makeText(this, "Mi perfil", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    private void irAGestionUsuarios() {
        startActivity(new Intent(this, GestionUsuariosActivity.class));
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
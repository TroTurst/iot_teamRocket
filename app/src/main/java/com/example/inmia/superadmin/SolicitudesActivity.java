package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class SolicitudesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    // Cards de solicitudes
    private MaterialCardView cardSolicitud1, cardSolicitud2, cardSolicitud3;

    // Botones aceptar
    private MaterialButton btnAceptar1, btnAceptar2, btnAceptar3;

    // Botones rechazar
    private MaterialButton btnRechazar1, btnRechazar2, btnRechazar3;

    // Conteo de solicitudes pendientes
    private int totalPendientes = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_solicitudes_superadmin);

        // Vincular vistas
        bottomNav      = findViewById(R.id.bottomNavSuperAdmin);
        cardSolicitud1 = findViewById(R.id.cardSolicitud1);
        cardSolicitud2 = findViewById(R.id.cardSolicitud2);
        cardSolicitud3 = findViewById(R.id.cardSolicitud3);
        btnAceptar1    = findViewById(R.id.btnAceptar1);
        btnAceptar2    = findViewById(R.id.btnAceptar2);
        btnAceptar3    = findViewById(R.id.btnAceptar3);
        btnRechazar1   = findViewById(R.id.btnRechazar1);
        btnRechazar2   = findViewById(R.id.btnRechazar2);
        btnRechazar3   = findViewById(R.id.btnRechazar3);

        // Botón atrás
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // ── ACEPTAR solicitudes ──
        btnAceptar1.setOnClickListener(v ->
                mostrarDialogoHabilitar("María García López", cardSolicitud1));

        btnAceptar2.setOnClickListener(v ->
                mostrarDialogoHabilitar("Carlos Ramos Torres", cardSolicitud2));

        btnAceptar3.setOnClickListener(v ->
                mostrarDialogoHabilitar("Juan Sánchez Pérez", cardSolicitud3));

        // ── RECHAZAR solicitudes ──
        btnRechazar1.setOnClickListener(v ->
                mostrarDialogoRechazar("María García López", cardSolicitud1));

        btnRechazar2.setOnClickListener(v ->
                mostrarDialogoRechazar("Carlos Ramos Torres", cardSolicitud2));

        btnRechazar3.setOnClickListener(v ->
                mostrarDialogoRechazar("Juan Sánchez Pérez", cardSolicitud3));

        // Bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_usuarios);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                finish();
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

    private void mostrarDialogoHabilitar(String nombreAsesor,
                                         MaterialCardView card) {
        new AlertDialog.Builder(this)
                .setTitle("¿Habilitar asesor?")
                .setMessage("¿Estás seguro de habilitar a "
                        + nombreAsesor
                        + " como asesor de ventas?")
                .setPositiveButton("Habilitar", (dialog, which) -> {
                    // Ocultar la tarjeta de la solicitud
                    card.setVisibility(View.GONE);
                    totalPendientes--;

                    // TODO: actualizar estado en Firebase

                    Toast.makeText(this,
                            nombreAsesor + " ha sido habilitado como asesor",
                            Toast.LENGTH_SHORT).show();

                    // Si no quedan solicitudes, volver a gestión
                    if (totalPendientes == 0) {
                        irAGestionUsuarios();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoRechazar(String nombreAsesor,
                                        MaterialCardView card) {
        new AlertDialog.Builder(this)
                .setTitle("¿Rechazar solicitud?")
                .setMessage("¿Estás seguro de rechazar la solicitud de "
                        + nombreAsesor + "? Esta acción no se puede deshacer.")
                .setPositiveButton("Rechazar", (dialog, which) -> {
                    // Ocultar la tarjeta de la solicitud
                    card.setVisibility(View.GONE);
                    totalPendientes--;

                    // TODO: actualizar estado en Firebase

                    Toast.makeText(this,
                            "Solicitud de " + nombreAsesor + " rechazada",
                            Toast.LENGTH_SHORT).show();

                    // Si no quedan solicitudes, volver a gestión
                    if (totalPendientes == 0) {
                        irAGestionUsuarios();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void irAGestionUsuarios() {
        Intent intent = new Intent(this, GestionUsuariosActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
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

    // ← NUEVO: Botones ver perfil
    private MaterialButton btnVerPerfil1, btnVerPerfil2, btnVerPerfil3;

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

        // ← NUEVO: Vincular botones ver perfil
        btnVerPerfil1  = findViewById(R.id.btnVerPerfil1);
        btnVerPerfil2  = findViewById(R.id.btnVerPerfil2);
        btnVerPerfil3  = findViewById(R.id.btnVerPerfil3);

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

        // ← NUEVO: VER PERFIL de cada asesor
        btnVerPerfil1.setOnClickListener(v -> verPerfilAsesor(
                "María García López",
                "INMIA San Isidro",
                "DNI · 45678901",
                "15/03/1995",
                "m.garcia@inmia.com",
                "+51 987 654 321",
                "Av. Javier Prado 1234, San Isidro"
        ));

        btnVerPerfil2.setOnClickListener(v -> verPerfilAsesor(
                "Carlos Ramos Torres",
                "INMIA Miraflores",
                "DNI · 32156789",
                "22/07/1990",
                "c.ramos@inmia.com",
                "+51 912 345 678",
                "Calle Las Flores 567, Miraflores"
        ));

        btnVerPerfil3.setOnClickListener(v -> verPerfilAsesor(
                "Juan Sánchez Pérez",
                "INMIA Surco",
                "DNI · 78234561",
                "08/11/1988",
                "j.sanchez@inmia.com",
                "+51 956 789 012",
                "Jr. Los Pinos 890, Surco"
        ));

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

    // ── VER PERFIL ──────────────────────────────────────────────────────────

    private void verPerfilAsesor(String nombre, String inmobiliaria,
                                 String documento, String fechaNac,
                                 String correo, String telefono,
                                 String domicilio) {
        Intent intent = new Intent(this, PerfilAsesorActivity.class);
        intent.putExtra(PerfilAsesorActivity.EXTRA_NOMBRE,       nombre);
        intent.putExtra(PerfilAsesorActivity.EXTRA_INMOBILIARIA, inmobiliaria);
        intent.putExtra(PerfilAsesorActivity.EXTRA_DOCUMENTO,    documento);
        intent.putExtra(PerfilAsesorActivity.EXTRA_FECHA_NAC,    fechaNac);
        intent.putExtra(PerfilAsesorActivity.EXTRA_CORREO,       correo);
        intent.putExtra(PerfilAsesorActivity.EXTRA_TELEFONO,     telefono);
        intent.putExtra(PerfilAsesorActivity.EXTRA_DOMICILIO,    domicilio);
        startActivity(intent);
    }

    // ── DIÁLOGOS ─────────────────────────────────────────────────────────────

    private void mostrarDialogoHabilitar(String nombreAsesor,
                                         MaterialCardView card) {
        new AlertDialog.Builder(this)
                .setTitle("¿Habilitar asesor?")
                .setMessage("¿Estás seguro de habilitar a "
                        + nombreAsesor + " como asesor de ventas?")
                .setPositiveButton("Habilitar", (dialog, which) -> {
                    card.setVisibility(View.GONE);
                    totalPendientes--;

                    // TODO: actualizar estado en Firebase

                    Toast.makeText(this,
                            nombreAsesor + " ha sido habilitado como asesor",
                            Toast.LENGTH_SHORT).show();

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
                    card.setVisibility(View.GONE);
                    totalPendientes--;

                    // TODO: actualizar estado en Firebase

                    Toast.makeText(this,
                            "Solicitud de " + nombreAsesor + " rechazada",
                            Toast.LENGTH_SHORT).show();

                    if (totalPendientes == 0) {
                        irAGestionUsuarios();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ── NAVEGACIÓN ───────────────────────────────────────────────────────────

    private void irAGestionUsuarios() {
        Intent intent = new Intent(this, GestionUsuariosActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.LoginActivity;
import com.example.inmia.R;
import com.example.inmia.superadmin.GestionUsuariosActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AsesorPerfilActivity extends AppCompatActivity {

    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private LinearLayout layoutCerrarSesion;
    private LinearLayout layoutCambiarPassword;
    private LinearLayout layoutNotificaciones;
    private BottomNavigationView bottomNav;

    private int totalNotificaciones = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_perfil);

        // Vincular vistas
        frameNotificaciones   = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif          = findViewById(R.id.tvBadgeNotif);
        layoutCerrarSesion    = findViewById(R.id.layoutCerrarSesion);
        layoutCambiarPassword = findViewById(R.id.layoutCambiarPassword);
        layoutNotificaciones  = findViewById(R.id.layoutNotificaciones);
        bottomNav             = findViewById(R.id.bottomNavAsesor);

        configurarBadge();

        // Campanita
        frameNotificaciones.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Tienes " + totalNotificaciones + " notificaciones",
                    Toast.LENGTH_SHORT).show();
            limpiarBadge();
        });

        // Cambiar contraseña
        layoutCambiarPassword.setOnClickListener(v ->
                Toast.makeText(this, "Cambiar contraseña",
                        Toast.LENGTH_SHORT).show());

        // Notificaciones
        layoutNotificaciones.setOnClickListener(v ->
                Toast.makeText(this, "Configurar notificaciones",
                        Toast.LENGTH_SHORT).show());

        // Cerrar sesión
        layoutCerrarSesion.setOnClickListener(v ->
                mostrarDialogoCerrarSesion());

        // Bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_inicio);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, AsesorHomeActivity.class));
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, AsesorChatActivity.class));
                return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(this, AsesorCitasActivity.class));
                return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, AsesorSeparacionesActivity.class));
                return true;
            }
            return false;
        });
    }

    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
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
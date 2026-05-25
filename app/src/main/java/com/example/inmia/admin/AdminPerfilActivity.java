package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.LoginActivity;
import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminPerfilActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private LinearLayout layoutCerrarSesion;
    private LinearLayout layoutCambiarPassword;
    private LinearLayout layoutNotificaciones;
    private LinearLayout layoutSimularNotificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_perfil);

        bottomNav = findViewById(R.id.bottomNavAdmin);
        layoutCerrarSesion = findViewById(R.id.layoutCerrarSesion);
        layoutCambiarPassword = findViewById(R.id.layoutCambiarPassword);
        layoutNotificaciones = findViewById(R.id.layoutNotificaciones);
        layoutSimularNotificacion = findViewById(R.id.layoutSimularNotificacion);

        // Cerrar sesion - AlertDialog de confirmacion
        layoutCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());

        // Cambiar contrasena
        layoutCambiarPassword.setOnClickListener(v ->
                Toast.makeText(this, "Cambiar contraseña", Toast.LENGTH_SHORT).show());

        // Notificaciones
        layoutNotificaciones.setOnClickListener(v ->
                Toast.makeText(this, "Configurar notificaciones", Toast.LENGTH_SHORT).show());

        // Simular notificacion
        layoutSimularNotificacion.setOnClickListener(v ->
                startActivity(new Intent(this, AdminSimularNotificacionesActivity.class)));

        // Bottom navigation
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
                return true;
            }

            return false;
        });
    }

    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

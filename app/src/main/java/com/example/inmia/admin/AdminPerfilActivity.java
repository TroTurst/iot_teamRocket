package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.inmia.LoginActivity;
import com.example.inmia.R;
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminFirestoreGateway.AdminContext;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminPerfilActivity extends AppCompatActivity {

    private TextView tvNombreUsuario;
    private TextView tvNombre;
    private TextView tvCorreo;
    private TextView tvAvatar;
    private ImageView imgAvatar;

    private AdminFirestoreGateway gateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_perfil);

        gateway = new AdminFirestoreGateway();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavAdmin);
        LinearLayout layoutCerrarSesion = findViewById(R.id.layoutCerrarSesion);
        LinearLayout layoutCambiarPassword = findViewById(R.id.layoutCambiarPassword);
        LinearLayout layoutNotificaciones = findViewById(R.id.layoutNotificaciones);
        LinearLayout layoutSimularNotificacion = findViewById(R.id.layoutSimularNotificacion);

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvNombre = findViewById(R.id.tvNombre);
        tvCorreo = findViewById(R.id.tvCorreo);
        tvAvatar = findViewById(R.id.tvAvatar);
        imgAvatar = findViewById(R.id.imgAvatar);

        cargarPerfilDesdeFirebase();

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
            }
            if (id == R.id.nav_proyectos) {
                navegarATab(AdminProyectosActivity.class);
                return true;
            }
            if (id == R.id.nav_asesores) {
                navegarATab(AdminAsesoresActivity.class);
                return true;
            }
            if (id == R.id.nav_reportes) {
                navegarATab(AdminReportesActivity.class);
                return true;
            }

            return id == R.id.nav_perfil;
        });
    }

    private void cargarPerfilDesdeFirebase() {
        gateway.resolveAdminContextByUserId(FirebaseAuth.getInstance().getUid(), new AdminFirestoreGateway.FirestoreCallback<>() {
            @Override
            public void onSuccess(AdminContext context) {
                if (tvNombreUsuario != null) {
                    tvNombreUsuario.setText(context.getDisplayName());
                }
                if (tvNombre != null) {
                    tvNombre.setText(context.getDisplayName());
                }
                if (tvCorreo != null) {
                    tvCorreo.setText(context.getEmail());
                }
                if (tvAvatar != null) {
                    tvAvatar.setText(obtenerIniciales(context.getDisplayName()));
                }
                // Foto de perfil si existe, sino iniciales
                String fotoUrl = context.getFotoUrl();
                if (imgAvatar != null && fotoUrl != null && !fotoUrl.isEmpty()) {
                    imgAvatar.setVisibility(View.VISIBLE);
                    if (tvAvatar != null) tvAvatar.setVisibility(View.GONE);
                    Glide.with(AdminPerfilActivity.this).load(fotoUrl).circleCrop().into(imgAvatar);
                } else if (imgAvatar != null) {
                    imgAvatar.setVisibility(View.GONE);
                    if (tvAvatar != null) tvAvatar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminPerfilActivity.this,
                        "No se pudo cargar el perfil",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "AD";
        }
        String[] partes = nombre.trim().split("\\s+");
        StringBuilder iniciales = new StringBuilder();
        for (int i = 0; i < partes.length && iniciales.length() < 2; i++) {
            if (!partes[i].isEmpty()) {
                iniciales.append(partes[i].charAt(0));
            }
        }
        return iniciales.length() > 0 ? iniciales.toString().toUpperCase() : "AD";
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

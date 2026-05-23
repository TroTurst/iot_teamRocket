package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.superadmin.db.AppDatabase;
import com.example.inmia.superadmin.db.NotificacionSAEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class NotificacionesSuperAdminActivity extends AppCompatActivity {

    private RecyclerView recyclerNotificaciones;
    private TextView tvSinNotificaciones;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_notificaciones);

        recyclerNotificaciones = findViewById(R.id.recyclerNotificaciones);
        tvSinNotificaciones    = findViewById(R.id.tvSinNotificaciones);
        bottomNav              = findViewById(R.id.bottomNavSuperAdmin);

        // Botón atrás
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Marcar todas como leídas al abrir la vista
        AppDatabase db = AppDatabase.getInstance(this);
        db.notificacionDao().marcarTodasLeidas();

        // Limpiar badge
        new SessionManager(this).limpiarNotificaciones();

        // Cargar lista
        List<NotificacionSAEntity> lista = db.notificacionDao().obtenerTodas();

        if (lista.isEmpty()) {
            tvSinNotificaciones.setVisibility(View.VISIBLE);
            recyclerNotificaciones.setVisibility(View.GONE);
        } else {
            tvSinNotificaciones.setVisibility(View.GONE);
            recyclerNotificaciones.setVisibility(View.VISIBLE);
            recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(this));
            recyclerNotificaciones.setAdapter(
                    new NotificacionSAAdapter(this, lista));
        }

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, SuperAdminHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, GestionUsuariosActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_reportes) {
                startActivity(new Intent(this, ReportesActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_logs) {
                startActivity(new Intent(this, LogsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}

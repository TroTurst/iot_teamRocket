package com.example.inmia.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;

    // Hardcodeado — luego vendrá de Firebase
    private int totalNotificaciones = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_home);

        bottomNav           = findViewById(R.id.bottomNavAdmin);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif        = findViewById(R.id.tvBadgeNotif);

        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Tienes " + totalNotificaciones + " notificaciones",
                    Toast.LENGTH_SHORT).show();
            limpiarBadge();
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_proyectos) {
                Toast.makeText(this, "Proyectos", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_asesores) {
                Toast.makeText(this, "Asesores", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_reportes) {
                Toast.makeText(this, "Reportes", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_perfil) {
                Toast.makeText(this, "Mi perfil", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
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
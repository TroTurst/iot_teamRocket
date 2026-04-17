package com.example.inmia.cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClienteHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;

    // Hardcodeado — luego vendrá de Firebase
    private int totalNotificaciones = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_cliente_home);

        bottomNav          = findViewById(R.id.bottomNavCliente);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif       = findViewById(R.id.tvBadgeNotif);

        // Configurar badge inicial
        configurarBadge();

        // Click en campanita
        frameNotificaciones.setOnClickListener(v -> {
            // TODO: ir a pantalla de notificaciones
            Toast.makeText(this,
                    "Tienes " + totalNotificaciones + " notificaciones",
                    Toast.LENGTH_SHORT).show();

            // Al ver notificaciones, ocultar el badge
            limpiarBadge();
        });

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_explorar) {
                Toast.makeText(this, "Explorar propiedades", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_citas) {
                Toast.makeText(this, "Mis citas", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_historial) {
                Toast.makeText(this, "Historial", Toast.LENGTH_SHORT).show();
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
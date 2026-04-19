package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AsesorSeparacionDetailActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private TextView tvEstadoDetalle;
    private TextView btnCancelarSeparacion;
    private FrameLayout framePerfil;

    private int totalNotificaciones = 2;
    private boolean separacionCancelada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_separacion_detail);

        bottomNav = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        tvEstadoDetalle = findViewById(R.id.tvEstadoSeparacionDetalle);
        btnCancelarSeparacion = findViewById(R.id.btnCancelarSeparacion);
        framePerfil = findViewById(R.id.framePerfil);

        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        bottomNav.setSelectedItemId(R.id.nav_separaciones);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, AsesorHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, AsesorChatActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(this, AsesorCitasActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, AsesorSeparacionesActivity.class));
                finish();
                return true;
            }

            return false;
        });
    }

    public void onHablarCliente(android.view.View view) {
        Toast.makeText(this, "Abrir chat con cliente", Toast.LENGTH_SHORT).show();
    }

    public void onCancelarSeparacion(android.view.View view) {
        if (separacionCancelada) {
            return;
        }
        new AlertDialog.Builder(this)
            .setTitle("Cancelar separacion")
            .setMessage("Estas seguro de cancelar la separacion del inmueble?")
            .setPositiveButton("Cancelar", (dialog, which) -> {
                separacionCancelada = true;
                tvEstadoDetalle.setText("Cancelada");
                tvEstadoDetalle.setTextColor(ContextCompat.getColor(this, R.color.inmia_danger));
                btnCancelarSeparacion.setText("Cancelada");
                btnCancelarSeparacion.setEnabled(false);
                btnCancelarSeparacion.setAlpha(0.6f);
            })
            .setNegativeButton("Volver", null)
            .show();
    }

    private void configurarBadge() {
        if (totalNotificaciones > 0) {
            tvBadgeNotif.setText(String.valueOf(totalNotificaciones));
            tvBadgeNotif.setVisibility(android.view.View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(android.view.View.GONE);
        }
    }

    private void limpiarBadge() {
        totalNotificaciones = 0;
        tvBadgeNotif.setVisibility(android.view.View.GONE);
    }
}

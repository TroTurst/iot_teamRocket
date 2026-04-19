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

public class AsesorSeparacionesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private TextView tvEstadoPendiente;
    private TextView btnConfirmar;
    private FrameLayout framePerfil;

    private int totalNotificaciones = 2;
    private boolean separacionConfirmada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_separaciones);

        bottomNav = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        tvEstadoPendiente = findViewById(R.id.tvEstadoSeparacionPendiente);
        btnConfirmar = findViewById(R.id.btnConfirmarSeparacion);
        framePerfil = findViewById(R.id.framePerfil);

        TextView btnDetallesAprobada = findViewById(R.id.btnDetallesSeparacionAprobada);

        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        btnConfirmar.setOnClickListener(v -> {
            if (separacionConfirmada) {
                openDetallesSeparacion();
                return;
            }
            showConfirmDialog();
        });

        btnDetallesAprobada.setOnClickListener(v -> openDetallesSeparacion());

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
                return true;
            }

            return false;
        });
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Confirmar separacion")
            .setMessage("Estas seguro de confirmar la separacion del inmueble?")
            .setPositiveButton("Confirmar", (dialog, which) -> {
                separacionConfirmada = true;
                tvEstadoPendiente.setText("Aprobada");
                tvEstadoPendiente.setTextColor(ContextCompat.getColor(this, R.color.inmia_success));
                btnConfirmar.setText("Detalles");
                Toast.makeText(this, "Separacion confirmada", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void openDetallesSeparacion() {
        startActivity(new Intent(this, AsesorSeparacionDetailActivity.class));
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

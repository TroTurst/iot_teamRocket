package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AsesorSeparacionesActivity extends AppCompatActivity implements SeparacionItemAdapter.Listener {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private FrameLayout framePerfil;
    private RecyclerView recyclerSeparacionesPendientes;
    private RecyclerView recyclerSeparacionesAprobadas;
    private SeparacionItemAdapter pendientesAdapter;
    private SeparacionItemAdapter aprobadasAdapter;
    private List<SeparacionItem> separacionesPendientes;

    private int totalNotificaciones = 2;

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
        framePerfil = findViewById(R.id.framePerfil);
        recyclerSeparacionesPendientes = findViewById(R.id.recyclerSeparacionesPendientes);
        recyclerSeparacionesAprobadas = findViewById(R.id.recyclerSeparacionesAprobadas);

        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        separacionesPendientes = buildMockPendientes();
        pendientesAdapter = new SeparacionItemAdapter(separacionesPendientes, this);
        recyclerSeparacionesPendientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerSeparacionesPendientes.setAdapter(pendientesAdapter);

        aprobadasAdapter = new SeparacionItemAdapter(buildMockAprobadas(), this);
        recyclerSeparacionesAprobadas.setLayoutManager(new LinearLayoutManager(this));
        recyclerSeparacionesAprobadas.setAdapter(aprobadasAdapter);

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

    @Override
    public void onAction(SeparacionItem item, int position) {
        if (item.isConfirmed()) {
            openDetallesSeparacion();
            return;
        }
        showConfirmDialog(item, position);
    }

    private void showConfirmDialog(SeparacionItem item, int position) {
        new AlertDialog.Builder(this)
            .setTitle("Confirmar separacion")
            .setMessage("Estas seguro de confirmar la separacion del inmueble?")
            .setPositiveButton("Confirmar", (dialog, which) -> {
                SeparacionItem updated = new SeparacionItem(
                    "Aprobada",
                    R.color.inmia_success,
                    item.getProject(),
                    item.getLocation(),
                    item.getCompany(),
                    "Detalles",
                    true
                );
                separacionesPendientes.set(position, updated);
                pendientesAdapter.notifyItemChanged(position);
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

    private List<SeparacionItem> buildMockPendientes() {
        List<SeparacionItem> items = new ArrayList<>();
        items.add(new SeparacionItem(
            "Por confirmar",
            R.color.inmia_teal_dark,
            "Palm Living",
            "San Isidro, Lima",
            "Galeon Inmobiliaria",
            "Confirmar",
            false
        ));
        return items;
    }

    private List<SeparacionItem> buildMockAprobadas() {
        List<SeparacionItem> items = new ArrayList<>();
        items.add(new SeparacionItem(
            "Aprobada",
            R.color.inmia_success,
            "Palm Living",
            "San Isidro, Lima",
            "Galeon Inmobiliaria",
            "Detalles",
            true
        ));
        return items;
    }
}

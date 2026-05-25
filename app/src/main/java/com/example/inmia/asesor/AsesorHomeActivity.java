package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AsesorHomeActivity extends AppCompatActivity implements HomeCitaAdapter.Listener {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private FrameLayout framePerfil;
    private RecyclerView recyclerHomeCitas;
    private HomeCitaAdapter homeCitaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_home);

        bottomNav           = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif        = findViewById(R.id.tvBadgeNotif);
        framePerfil         = findViewById(R.id.framePerfil);
        recyclerHomeCitas    = findViewById(R.id.recyclerHomeCitas);

        AsesorCitaStore.seedIfEmpty(this);
        AsesorNotificacionStore.seedIfEmpty(this);
        configurarBadge();

        // Click en campanita
        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        homeCitaAdapter = new HomeCitaAdapter(AsesorCitaStore.getItems(this), this);
        recyclerHomeCitas.setLayoutManager(new LinearLayoutManager(this));
        recyclerHomeCitas.setAdapter(homeCitaAdapter);

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
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

    private void configurarBadge() {
        int totalNotificaciones = AsesorNotificacionStore.getBadgeCount(this);
        if (totalNotificaciones > 0) {
            tvBadgeNotif.setText(String.valueOf(totalNotificaciones));
            tvBadgeNotif.setVisibility(View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(View.GONE);
        }
    }

    private void limpiarBadge() {
        AsesorNotificacionStore.clearBadge(this);
        tvBadgeNotif.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (homeCitaAdapter != null) {
            homeCitaAdapter.updateItems(AsesorCitaStore.getItems(this));
        }
        configurarBadge();
    }

    @Override
    public void onCitaSelected(HomeCita item) {
        Intent intent = new Intent(this, AsesorCitaDetailActivity.class);
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_CLIENTE, item.getClient());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_PROYECTO, item.getProject());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_ESTADO, item.getStatus());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_CONFIRMADA, item.isConfirmed());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_KEY, AsesorCitaStore.buildKey(item.getClient(), item.getProject()));
        startActivity(intent);
    }
}
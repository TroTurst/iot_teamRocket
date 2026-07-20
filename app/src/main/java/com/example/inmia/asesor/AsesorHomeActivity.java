package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

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
    private TextView tvCitasHoyCount;
    private TextView tvSeparacionesCount;
    private TextView tvProyectosCount;
    private HomeCitaAdapter homeCitaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_asesor_home);

        bottomNav           = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif        = findViewById(R.id.tvBadgeNotif);
        framePerfil         = findViewById(R.id.framePerfil);
        recyclerHomeCitas   = findViewById(R.id.recyclerHomeCitas);
        tvCitasHoyCount     = findViewById(R.id.tvCitasHoyCount);
        tvSeparacionesCount = findViewById(R.id.tvSeparacionesCount);
        tvProyectosCount    = findViewById(R.id.tvProyectosCount);

        AsesorNotificacionSyncer.sincronizar(this);
        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });
        framePerfil.setOnClickListener(v -> startActivity(new Intent(this, AsesorPerfilActivity.class)));

        homeCitaAdapter = new HomeCitaAdapter(new ArrayList<>(), this);
        recyclerHomeCitas.setLayoutManager(new LinearLayoutManager(this));
        recyclerHomeCitas.setAdapter(homeCitaAdapter);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, AsesorChatActivity.class)); finish(); return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(this, AsesorCitasActivity.class)); finish(); return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, AsesorSeparacionesActivity.class)); finish(); return true;
            }
            return false;
        });

        cargarDashboardFirestore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDashboardFirestore();
        AsesorNotificacionSyncer.sincronizar(this);
        configurarBadge();
    }

    private void cargarDashboardFirestore() {
        AsesorFirestoreRepository repo = AsesorFirestoreRepository.get();

        // Contador "Citas hoy"
        repo.getCitasHoy(items -> {
            if (tvCitasHoyCount != null) tvCitasHoyCount.setText(String.valueOf(items.size()));
        });

        // Lista "Próximas citas" → todas las citas del mes calendario actual
        repo.getCitasDelMes(items -> {
            if (homeCitaAdapter != null) homeCitaAdapter.updateItems(items);
        });

        // Separaciones: aprobadas y total
        repo.getSeparaciones(items -> {
            int aprobadas = 0;
            int total     = items.size();
            for (SeparacionItem item : items) {
                if ("Aprobada".equalsIgnoreCase(item.getStatus())) aprobadas++;
            }
            if (tvSeparacionesCount != null) tvSeparacionesCount.setText(String.valueOf(aprobadas));
            if (tvProyectosCount    != null) tvProyectosCount.setText(String.valueOf(total));
        });
    }

    private void configurarBadge() {
        int total = AsesorNotificacionStore.getBadgeCount(this);
        if (total > 0) {
            tvBadgeNotif.setText(String.valueOf(total));
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
    public void onCitaSelected(HomeCita item) {
        Intent intent = new Intent(this, AsesorCitaDetailActivity.class);
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_KEY,        item.getDocId());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_CLIENTE,    item.getClient());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_PROYECTO,   item.getProject());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_ESTADO,     item.getStatus());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_CONFIRMADA, item.isConfirmed());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_FECHA,      item.getDate() + " " + item.getTime() + " " + item.getMeridian());
        startActivity(intent);
    }
}

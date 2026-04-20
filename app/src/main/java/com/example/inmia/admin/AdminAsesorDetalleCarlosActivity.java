package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminAsesorDetalleCarlosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_asesor_detalle_carlos);

        View btnBack = findViewById(R.id.btnBackDetalleCarlos);
        View btnDetalleCita = findViewById(R.id.btnDetalleCitaCarlos);
        View btnGestionProyectos = findViewById(R.id.btnGestionProyectosCarlos);
        View layoutProyectosActivos = findViewById(R.id.layoutProyectosActivosCarlos);
        View btnDetallesProyecto1 = findViewById(R.id.btnDetallesProyectoCarlos1);
        View btnDetallesProyecto2 = findViewById(R.id.btnDetallesProyectoCarlos2);
        View btnDetallesProyecto3 = findViewById(R.id.btnDetallesProyectoCarlos3);
        NestedScrollView scrollView = findViewById(R.id.scrollViewAsesorDetalleCarlos);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavAdmin);

        bottomNav.setSelectedItemId(R.id.nav_asesores);

        btnBack.setOnClickListener(v -> finish());

        btnDetalleCita.setOnClickListener(v ->
                Toast.makeText(this, "Detalle de cita proximamente", Toast.LENGTH_SHORT).show());

        btnGestionProyectos.setOnClickListener(v -> {
            layoutProyectosActivos.setVisibility(layoutProyectosActivos.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            if (layoutProyectosActivos.getVisibility() == View.VISIBLE) {
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
        });

        View.OnClickListener abrirDetalleProyecto = v ->
                startActivity(new Intent(this, AdminProyectoDetalleActivity.class));

        btnDetallesProyecto1.setOnClickListener(abrirDetalleProyecto);
        btnDetallesProyecto2.setOnClickListener(abrirDetalleProyecto);
        btnDetallesProyecto3.setOnClickListener(abrirDetalleProyecto);

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
                navegarATab(AdminPerfilActivity.class);
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
}



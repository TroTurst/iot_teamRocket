package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.admin.data.AdminProyectoRepositoryMock;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminProyectosActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;

    // Hardcodeado - luego vendra de Firebase
    private int totalNotificaciones = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_proyectos);

        bottomNav = findViewById(R.id.bottomNavAdmin);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        EditText etBuscarProyecto = findViewById(R.id.etBuscarProyecto);
        ImageView btnFiltroMock = findViewById(R.id.btnFiltroMock);

        // NUEVO: Inicializar RecyclerView
        RecyclerView recyclerViewProyectos = findViewById(R.id.recyclerViewProyectos);

        // Configurar LayoutManager (vertical scrolling)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewProyectos.setLayoutManager(layoutManager);

        // Crear lista de proyectos con datos mock (fuente única compartida con el detalle)
        List<Proyecto> proyectos = AdminProyectoRepositoryMock.getProyectos();

        // Crear y asignar adapter
        AdminProyectoAdapter adapter = new AdminProyectoAdapter(this, proyectos);
        recyclerViewProyectos.setAdapter(adapter);

        // Botón nueva proyecto
        findViewById(R.id.btnNuevoProyecto).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminProyectoNuevoActivity.class));
        });

        configurarBadge();
        bottomNav.setSelectedItemId(R.id.nav_proyectos);

        frameNotificaciones.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Tienes " + totalNotificaciones + " notificaciones",
                    Toast.LENGTH_SHORT).show();
            limpiarBadge();
        });

        etBuscarProyecto.setOnClickListener(v ->
                Toast.makeText(this, "Busqueda habilitada", Toast.LENGTH_SHORT).show());

        etBuscarProyecto.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Toast.makeText(this, "Escribe para buscar proyectos", Toast.LENGTH_SHORT).show();
            }
        });

        btnFiltroMock.setOnClickListener(v ->
                Toast.makeText(this, "Filtros proximamente", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnNuevoProyecto).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminProyectoNuevoActivity.class));
        });


        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                navegarATab(AdminHomeActivity.class);
                return true;
            } else if (id == R.id.nav_proyectos) {
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

    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // Datos mock se obtienen desde AdminProyectoRepositoryMock
}



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
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminFirestoreGateway.AdminContext;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.example.inmia.models.Proyecto;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdminProyectosActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;

    private int totalNotificaciones;

    private AdminFirestoreGateway gateway;
    private String companyId;
    private AdminProyectoAdapter adapter;
    private final List<Proyecto> proyectos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_proyectos);

        gateway = new AdminFirestoreGateway();

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

        adapter = new AdminProyectoAdapter(this, proyectos);
        recyclerViewProyectos.setAdapter(adapter);

        // Botón nueva proyecto
        findViewById(R.id.btnNuevoProyecto).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminProyectoNuevoActivity.class));
        });

        configurarBadge();
        bottomNav.setSelectedItemId(R.id.nav_proyectos);

        gateway.resolveAdminContextByEmail(AdminSessionDefaults.DEFAULT_ADMIN_EMAIL, new AdminFirestoreGateway.FirestoreCallback<AdminContext>() {
            @Override
            public void onSuccess(AdminContext context) {
                companyId = context.getCompanyId();

                gateway.observeUnreadNotifications(context.getUserId(), new AdminFirestoreGateway.FirestoreCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer count) {
                        totalNotificaciones = count != null ? count : 0;
                        configurarBadge();
                    }

                    @Override
                    public void onError(Exception e) {
                        totalNotificaciones = 0;
                        configurarBadge();
                    }
                });

                gateway.observeProjectsByCompany(companyId, context.getCompanyName(), new AdminFirestoreGateway.FirestoreListCallback<Proyecto>() {
                    @Override
                    public void onSuccess(List<Proyecto> value) {
                        proyectos.clear();
                        if (value != null) {
                            proyectos.addAll(value);
                        }
                        adapter.setProyectos(proyectos);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AdminProyectosActivity.this,
                                "Error al cargar proyectos",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminProyectosActivity.this,
                        "No se pudo resolver la inmobiliaria",
                        Toast.LENGTH_SHORT).show();
            }
        });

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

    // Datos mock se obtienen desde AdminRepository (local) hasta conectar Firebase.
}

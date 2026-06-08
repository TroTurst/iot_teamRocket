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
import com.example.inmia.models.Asesor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdminAsesoresActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;

    private int totalNotificaciones;
    private AdminFirestoreGateway gateway;
    private String companyId;
    private final List<Asesor> asesores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_asesores);

        gateway = new AdminFirestoreGateway();

        bottomNav = findViewById(R.id.bottomNavAdmin);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        ImageView btnBackAsesores = findViewById(R.id.btnBackAsesores);
        View btnNuevoAsesor = findViewById(R.id.btnNuevoAsesor);
        EditText etBuscarAsesor = findViewById(R.id.etBuscarAsesor);
        ImageView btnFiltroAsesores = findViewById(R.id.btnFiltroAsesores);

        RecyclerView recyclerViewAsesores = findViewById(R.id.recyclerViewAsesores);
        recyclerViewAsesores.setLayoutManager(new LinearLayoutManager(this));
        AdminAsesorAdapter adapter = new AdminAsesorAdapter(this, asesores, asesor -> {
            Intent intent = new Intent(this, AdminAsesorDetalleCarlosActivity.class);
            intent.putExtra("asesor_id", asesor.getId());
            startActivity(intent);
        });
        recyclerViewAsesores.setAdapter(adapter);

        configurarBadge();
        bottomNav.setSelectedItemId(R.id.nav_asesores);

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

                gateway.observeAdvisorsByCompany(companyId, new AdminFirestoreGateway.FirestoreListCallback<Asesor>() {
                    @Override
                    public void onSuccess(List<Asesor> value) {
                        asesores.clear();
                        if (value != null) {
                            asesores.addAll(value);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AdminAsesoresActivity.this,
                                "Error al cargar asesores",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminAsesoresActivity.this,
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

        btnBackAsesores.setOnClickListener(v -> navegarATab(AdminHomeActivity.class));

        btnNuevoAsesor.setOnClickListener(v ->
                startActivity(new Intent(this, AdminAsesorNuevoActivity.class)));

        etBuscarAsesor.setOnClickListener(v ->
                Toast.makeText(this, "Busqueda de asesores habilitada", Toast.LENGTH_SHORT).show());

        etBuscarAsesor.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Toast.makeText(this, "Escribe para buscar asesores", Toast.LENGTH_SHORT).show();
            }
        });

        btnFiltroAsesores.setOnClickListener(v ->
                Toast.makeText(this, "Filtros de asesores proximamente", Toast.LENGTH_SHORT).show());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                navegarATab(AdminHomeActivity.class);
                return true;
            } else if (id == R.id.nav_proyectos) {
                navegarATab(AdminProyectosActivity.class);
                return true;
            } else if (id == R.id.nav_asesores) {
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
}

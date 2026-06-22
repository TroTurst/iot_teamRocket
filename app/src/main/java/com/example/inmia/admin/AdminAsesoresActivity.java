package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.inmia.models.AsesorFilter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminAsesoresActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;

    private int totalNotificaciones;
    private AdminFirestoreGateway gateway;
    private String companyId;
    private final List<Asesor> asesores = new ArrayList<>();
    private final List<Asesor> asesoresFiltrados = new ArrayList<>();
    private AsesorFilter filtroActual;
    private AdminAsesorAdapter adapter;
    private View btnFiltroAsesores;
    private EditText etBuscarAsesor;
    private TextView tvBadgeFiltros = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_asesores);

        gateway = new AdminFirestoreGateway();
        filtroActual = new AsesorFilter();

        bottomNav = findViewById(R.id.bottomNavAdmin);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        ImageView btnBackAsesores = findViewById(R.id.btnBackAsesores);
        View btnNuevoAsesor = findViewById(R.id.btnNuevoAsesor);
        etBuscarAsesor = findViewById(R.id.etBuscarAsesor);
        btnFiltroAsesores = findViewById(R.id.btnFiltroAsesores);
        tvBadgeFiltros = findViewById(R.id.tvBadgeFiltrosAsesores);

        RecyclerView recyclerViewAsesores = findViewById(R.id.recyclerViewAsesores);
        recyclerViewAsesores.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminAsesorAdapter(this, asesoresFiltrados, asesor -> {
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
                        aplicarFiltros();
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

        etBuscarAsesor.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filtroActual.setBuscarNombre(s.toString().trim());
                aplicarFiltros();
            }
        });

        btnFiltroAsesores.setOnClickListener(v -> {
            BottomSheetFiltrosAsesores bottomSheet = BottomSheetFiltrosAsesores.newInstance(filtroActual);
            bottomSheet.setListener(filter -> {
                filtroActual = filter;
                aplicarFiltros();
                actualizarBadgeFiltros();
            });
            bottomSheet.show(getSupportFragmentManager(), "filtros_asesores");
        });

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

    private void aplicarFiltros() {
        asesoresFiltrados.clear();
        for (Asesor a : asesores) {
            if (filtroActual.matches(a)) {
                asesoresFiltrados.add(a);
            }
        }
        java.util.Comparator<Asesor> comparator = filtroActual.getComparator();
        if (comparator != null) {
            java.util.Collections.sort(asesoresFiltrados, comparator);
        }
        adapter.notifyDataSetChanged();
    }

    private void actualizarBadgeFiltros() {
        if (tvBadgeFiltros == null) return;
        if (filtroActual.hasActiveFilters()) {
            tvBadgeFiltros.setVisibility(View.VISIBLE);
        } else {
            tvBadgeFiltros.setVisibility(View.GONE);
        }
    }

    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

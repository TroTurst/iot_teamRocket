package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminFirestoreGateway.AdminContext;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.example.inmia.models.Asesor;
import com.example.inmia.models.CitaAsesor;
import com.example.inmia.models.Log;
import com.example.inmia.models.Proyecto;
import com.example.inmia.util.LogHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminAsesorDetalleCarlosActivity extends AppCompatActivity {

    private AdminFirestoreGateway gateway;
    private String companyId;
    private String asesorId;
    private Asesor currentAsesor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_asesor_detalle_carlos);

        gateway = new AdminFirestoreGateway();

        View btnBack = findViewById(R.id.btnBackDetalleCarlos);
        View btnDetalleCita = findViewById(R.id.btnDetalleCitaCarlos);
        View btnGestionProyectos = findViewById(R.id.btnGestionProyectosCarlos);
        View btnAsignarMetas = findViewById(R.id.btnAsignarMetasCarlos);
        View layoutProyectosActivos = findViewById(R.id.layoutProyectosActivosCarlos);
        View layoutCitas = findViewById(R.id.layoutCitasCarlos);
        NestedScrollView scrollView = findViewById(R.id.scrollViewAsesorDetalleCarlos);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavAdmin);

        asesorId = getIntent().getStringExtra("asesor_id");
        if (TextUtils.isEmpty(asesorId)) {
            Toast.makeText(this, "No se encontró asesor", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecyclerView recyclerViewProyectos = findViewById(R.id.recyclerViewProyectosCarlos);
        recyclerViewProyectos.setLayoutManager(new LinearLayoutManager(this));
        AdminProyectoAdapter adapter = new AdminProyectoAdapter(this, new ArrayList<>());
        recyclerViewProyectos.setAdapter(adapter);

        RecyclerView recyclerViewCitas = findViewById(R.id.recyclerViewCitasCarlos);
        recyclerViewCitas.setLayoutManager(new LinearLayoutManager(this));
        List<CitaAsesor> citas = new ArrayList<>();
        AdminCitasAdapter citasAdapter = new AdminCitasAdapter(citas);
        recyclerViewCitas.setAdapter(citasAdapter);

        bottomNav.setSelectedItemId(R.id.nav_asesores);

        gateway.resolveAdminContextByEmail(AdminSessionDefaults.DEFAULT_ADMIN_EMAIL, new AdminFirestoreGateway.FirestoreCallback<>() {
            @Override
            public void onSuccess(AdminContext context) {
                companyId = context.getCompanyId();

                gateway.observeUnreadNotifications(context.getUserId(), new AdminFirestoreGateway.FirestoreCallback<>() {
                    @Override
                    public void onSuccess(Integer count) {
                        View badge = findViewById(R.id.tvBadgeNotif);
                        if (badge instanceof TextView) {
                            ((TextView) badge).setText(String.valueOf(count != null ? count : 0));
                            badge.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        View badge = findViewById(R.id.tvBadgeNotif);
                        if (badge instanceof TextView) {
                            badge.setVisibility(View.GONE);
                        }
                    }
                });

                gateway.observeAdvisorById(asesorId, new AdminFirestoreGateway.FirestoreCallback<>() {
                    @Override
                    public void onSuccess(Asesor asesor) {
                        currentAsesor = asesor;
                        poblarPerfil(asesor);

                        gateway.observeProjectsByCompany(companyId, new AdminFirestoreGateway.FirestoreListCallback<>() {
                            @Override
                            public void onSuccess(List<Proyecto> value) {
                                List<Proyecto> filtrados = new ArrayList<>();
                                if (value != null) {
                                    for (Proyecto proyecto : value) {
                                        if (proyecto.getVendedores() != null && proyecto.getVendedores().contains(asesorId)) {
                                            filtrados.add(proyecto);
                                        }
                                    }
                                }
                                adapter.setProyectos(filtrados);
                            }

                            @Override
                            public void onError(Exception e) {
                                adapter.setProyectos(new ArrayList<>());
                            }
                        });

                        gateway.observeCitasByAdvisor(asesorId, new AdminFirestoreGateway.FirestoreListCallback<>() {
                            @Override
                            public void onSuccess(List<CitaAsesor> value) {
                                citasAdapter.setCitas(value);
                            }

                            @Override
                            public void onError(Exception e) {
                                citasAdapter.setCitas(new ArrayList<>());
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AdminAsesorDetalleCarlosActivity.this, "No se encontró asesor", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminAsesorDetalleCarlosActivity.this, "No se pudo cargar el asesor", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnBack.setOnClickListener(v -> finish());

        btnDetalleCita.setOnClickListener(v -> {
            layoutCitas.setVisibility(layoutCitas.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            if (layoutCitas.getVisibility() == View.VISIBLE) {
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
        });

        btnGestionProyectos.setOnClickListener(v -> {
            layoutProyectosActivos.setVisibility(layoutProyectosActivos.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            if (layoutProyectosActivos.getVisibility() == View.VISIBLE) {
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
        });

        btnAsignarMetas.setOnClickListener(v -> {
            if (currentAsesor != null) {
                mostrarDialogoMetas(currentAsesor);
            } else {
                Toast.makeText(this, "Cargando asesor...", Toast.LENGTH_SHORT).show();
            }
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

    private void poblarPerfil(Asesor asesor) {
        TextView tvNombreHeader = findViewById(R.id.tvNombreHeaderCarlos);
        TextView tvNombre = findViewById(R.id.tvNombreCarlos);
        TextView tvRol = findViewById(R.id.tvRolCarlos);
        TextView tvEmail = findViewById(R.id.tvEmailCarlos);
        TextView tvTelefono = findViewById(R.id.tvTelefonoCarlos);
        TextView tvDni = findViewById(R.id.tvDniCarlos);
        TextView tvEstado = findViewById(R.id.tvEstadoCarlos);
        TextView tvZonaTrabajo = findViewById(R.id.tvZonaTrabajoCarlos);
        TextView tvCitas = findViewById(R.id.tvCitasCarlos);
        TextView tvVentas = findViewById(R.id.tvVentasCarlos);
        TextInputEditText etMetaVentas = findViewById(R.id.etMetaVentasCarlos);
        TextInputEditText etMetaCitas = findViewById(R.id.etMetaCitasCarlos);
        TextInputEditText etMetaGanancias = findViewById(R.id.etMetaGananciasCarlos);
        ImageView imgFoto = findViewById(R.id.imgFotoCarlos);

        tvNombreHeader.setText(asesor.getNombre());
        tvNombre.setText(asesor.getNombre());
        tvRol.setText(asesor.getRol());
        tvEmail.setText(asesor.getEmail());
        tvTelefono.setText(asesor.getTelefono());
        tvDni.setText(asesor.getDni());
        tvEstado.setText(asesor.getEstado());
        tvZonaTrabajo.setText(asesor.getZonaTrabajo());
        tvCitas.setText(String.valueOf(asesor.getCitasMensualActual()));
        tvVentas.setText(String.valueOf(asesor.getVentasMensualActual()));
        etMetaVentas.setText(String.valueOf(asesor.getMetaVentasMensual()));
        etMetaCitas.setText(String.valueOf(asesor.getMetaCitasMensual()));
        etMetaGanancias.setText(String.valueOf(asesor.getMetaGananciasMensual()));

        if (asesor.getFotoResId() != 0) {
            imgFoto.setImageResource(asesor.getFotoResId());
        }
    }

    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void mostrarDialogoMetas(Asesor asesor) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_asignar_metas, null);
        TextInputEditText etMetaVentas = contentView.findViewById(R.id.etMetaVentas);
        TextInputEditText etMetaCitas = contentView.findViewById(R.id.etMetaCitas);
        TextInputEditText etMetaGanancias = contentView.findViewById(R.id.etMetaGanancias);

        etMetaVentas.setText(String.valueOf(asesor.getMetaVentasMensual()));
        etMetaCitas.setText(String.valueOf(asesor.getMetaCitasMensual()));
        etMetaGanancias.setText(String.valueOf(asesor.getMetaGananciasMensual()));

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.admin_metas_titulo)
                .setView(contentView)
                .setPositiveButton(R.string.admin_metas_guardar, (dialog, which) -> {
                    int metaVentas = parseIntSafe(etMetaVentas.getText());
                    int metaCitas = parseIntSafe(etMetaCitas.getText());
                    int metaGanancias = parseIntSafe(etMetaGanancias.getText());

                    gateway.updateAsesorMetas(asesor.getId(), metaVentas, metaCitas, metaGanancias,
                            new AdminFirestoreGateway.FirestoreCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            LogHelper.registrar(
                                    "Se actualizaron las metas mensuales de " + asesor.getNombre(),
                                    Log.TIPO_CUENTA,
                                    LogHelper.ROL_ADMIN,
                                    asesor.getNombre(),
                                    asesor.getId() != null ? asesor.getId() : "");
                            runOnUiThread(() -> {
                                Toast.makeText(AdminAsesorDetalleCarlosActivity.this,
                                        "Metas actualizadas",
                                        Toast.LENGTH_SHORT).show();
                                currentAsesor.setMetaVentasMensual(metaVentas);
                                currentAsesor.setMetaCitasMensual(metaCitas);
                                currentAsesor.setMetaGananciasMensual(metaGanancias);
                                poblarPerfil(currentAsesor);
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(AdminAsesorDetalleCarlosActivity.this,
                                        "Error al guardar metas: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton(R.string.admin_metas_cancelar, null)
                .show();
    }

    private int parseIntSafe(CharSequence value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String formatearSoles(int valor) {
        return String.format(Locale.getDefault(), "S/ %,d", valor);
    }

}

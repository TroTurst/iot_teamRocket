package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminFirestoreGateway.AdminContext;
import com.example.inmia.models.Asesor;
import com.example.inmia.models.CitaAsesor;
import com.example.inmia.models.Log;
import com.example.inmia.models.Proyecto;
import com.example.inmia.util.LogHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminAsesorDetalleCarlosActivity extends AppCompatActivity {

    private AdminFirestoreGateway gateway;
    private String companyId;
    private String asesorId;
    private Asesor currentAsesor;
    private boolean autoAssignTriggered = false;
    private List<Proyecto> allCompanyProjects = new ArrayList<>();

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
        View btnAsignarProyectos = findViewById(R.id.btnAsignarProyectosCarlos);
        View btnGestionProyectos = findViewById(R.id.btnGestionProyectosCarlos);
        View btnAsignarMetas = findViewById(R.id.btnAsignarMetasCarlos);
        View btnEditarDistritos = findViewById(R.id.btnEditarDistritosCarlos);
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

        gateway.resolveAdminContextByUserId(FirebaseAuth.getInstance().getUid(), new AdminFirestoreGateway.FirestoreCallback<>() {
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
                                if (value != null) {
                                    allCompanyProjects = value;
                                }

                                List<Proyecto> filtrados = new ArrayList<>();
                                if (value != null) {
                                    for (Proyecto proyecto : value) {
                                        if (proyecto.getVendedores() != null && proyecto.getVendedores().contains(asesorId)) {
                                            filtrados.add(proyecto);
                                        }
                                    }
                                }
                                adapter.setProyectos(filtrados);

                                // Auto-assign if empty and advisor has districts (only once)
                                if (filtrados.isEmpty() && !autoAssignTriggered && currentAsesor != null) {
                                    List<String> distritos = currentAsesor.getDistritos();
                                    if (distritos != null && !distritos.isEmpty()) {
                                        autoAssignTriggered = true;
                                        gateway.assignAllProjectsByDistritos(
                                                asesorId, distritos, companyId,
                                                new AdminFirestoreGateway.FirestoreCallback<Integer>() {
                                                    @Override
                                                    public void onSuccess(Integer count) {
                                                        // Snapshot listener will auto-refresh
                                                    }

                                                    @Override
                                                    public void onError(Exception e) { }
                                                });
                                    }
                                }
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

        btnEditarDistritos.setOnClickListener(v -> {
            if (currentAsesor != null) {
                mostrarDialogoDistritos(currentAsesor);
            } else {
                Toast.makeText(this, "Cargando asesor...", Toast.LENGTH_SHORT).show();
            }
        });

        btnAsignarProyectos.setOnClickListener(v -> {
            if (currentAsesor != null) {
                mostrarDialogoAsignarProyectos(currentAsesor);
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
        TextView tvDistritos = findViewById(R.id.tvDistritosCarlos);
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

        List<String> distritos = asesor.getDistritos();
        if (distritos != null && !distritos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < distritos.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(distritos.get(i));
            }
            tvDistritos.setText(sb.toString());
        } else {
            tvDistritos.setText(R.string.admin_asesor_distritos_vacio);
        }

        // Descripción dinámica de proyectos
        TextView tvProyectosDesc = findViewById(R.id.tvProyectosDescCarlos);
        tvProyectosDesc.setText(getString(R.string.admin_asesor_proyectos_activos_desc, asesor.getNombre()));

        // Foto de perfil real si existe, sino el avatar por defecto
        String fotoUrl = asesor.getFotoUrl();
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(this).load(fotoUrl).circleCrop().into(imgFoto);
        } else if (asesor.getFotoResId() != 0) {
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

    private void mostrarDialogoDistritos(Asesor asesor) {
        ScrollView contentView = (ScrollView) LayoutInflater.from(this).inflate(R.layout.dialog_asignar_distritos, null);

        List<String> selectedDistritos = asesor.getDistritos();
        int[] checkBoxIds = {
                R.id.cbAncon, R.id.cbAte, R.id.cbBarranco, R.id.cbBrena,
                R.id.cbCarabayllo, R.id.cbCercadoLima, R.id.cbChorrillos, R.id.cbComas,
                R.id.cbElAgustino, R.id.cbIndependencia, R.id.cbJesusMaria, R.id.cbLaMolina,
                R.id.cbLaVictoria, R.id.cbLince, R.id.cbLosOlivos, R.id.cbMagdalena,
                R.id.cbMiraflores, R.id.cbPuebloLibre, R.id.cbPuentePiedra, R.id.cbRimac,
                R.id.cbSanBorja, R.id.cbSanIsidro, R.id.cbSanJuanLurigancho, R.id.cbSanJuanMiraflores,
                R.id.cbSanLuis, R.id.cbSanMartin, R.id.cbSanMiguel, R.id.cbSantaAnita,
                R.id.cbSantiagoSurco, R.id.cbSurquillo, R.id.cbVillaElSalvador, R.id.cbVillaMariaTriunfo
        };

        // Pre-select checkboxes based on current districts
        for (int id : checkBoxIds) {
            CheckBox cb = contentView.findViewById(id);
            if (cb != null) {
                String districtName = cb.getText().toString();
                if (selectedDistritos != null && selectedDistritos.contains(districtName)) {
                    cb.setChecked(true);
                }
            }
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.admin_asesor_distritos_editar)
                .setView(contentView)
                .setPositiveButton(R.string.admin_asesor_distritos_guardar, (dialog, which) -> {
                    List<String> newDistritos = new ArrayList<>();
                    for (int id : checkBoxIds) {
                        CheckBox cb = contentView.findViewById(id);
                        if (cb != null && cb.isChecked()) {
                            newDistritos.add(cb.getText().toString());
                        }
                    }

                    gateway.updateAsesorDistritos(asesor.getId(), newDistritos,
                            new AdminFirestoreGateway.FirestoreCallback<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    gateway.assignAllProjectsByDistritos(asesor.getId(), newDistritos, companyId,
                                            new AdminFirestoreGateway.FirestoreCallback<Integer>() {
                                                @Override
                                                public void onSuccess(Integer assignedCount) {
                                                    runOnUiThread(() -> {
                                                        Toast.makeText(AdminAsesorDetalleCarlosActivity.this,
                                                                "Distritos actualizados" +
                                                                        (assignedCount > 0 ? ". Se asignaron " + assignedCount + " proyectos automáticamente" : ""),
                                                                Toast.LENGTH_LONG).show();

                                                        // Refresh advisor data
                                                        gateway.observeAdvisorById(asesor.getId(), new AdminFirestoreGateway.FirestoreCallback<Asesor>() {
                                                            @Override
                                                            public void onSuccess(Asesor updatedAsesor) {
                                                                currentAsesor = updatedAsesor;
                                                                poblarPerfil(updatedAsesor);
                                                            }

                                                            @Override
                                                            public void onError(Exception e) {
                                                                // Silently fail, just re-show current
                                                            }
                                                        });

                                                        // Also refresh the projects list
                                                        gateway.observeProjectsByCompany(companyId, new AdminFirestoreGateway.FirestoreListCallback<Proyecto>() {
                                                            @Override
                                                            public void onSuccess(List<Proyecto> value) {
                                                                List<Proyecto> filtrados = new ArrayList<>();
                                                                if (value != null) {
                                                                    for (Proyecto proyecto : value) {
                                                                        if (proyecto.getVendedores() != null && proyecto.getVendedores().contains(asesor.getId())) {
                                                                            filtrados.add(proyecto);
                                                                        }
                                                                    }
                                                                }
                                                                runOnUiThread(() -> {
                                                                    RecyclerView rv = findViewById(R.id.recyclerViewProyectosCarlos);
                                                                    if (rv != null && rv.getAdapter() instanceof AdminProyectoAdapter) {
                                                                        ((AdminProyectoAdapter) rv.getAdapter()).setProyectos(filtrados);
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onError(Exception e) { }
                                                        });
                                                    });
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    runOnUiThread(() -> {
                                                        Toast.makeText(AdminAsesorDetalleCarlosActivity.this,
                                                                "Distritos actualizados, pero error al asignar proyectos",
                                                                Toast.LENGTH_LONG).show();
                                                    });
                                                }
                                            });
                                }

                                @Override
                                public void onError(Exception e) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(AdminAsesorDetalleCarlosActivity.this,
                                                "Error al guardar distritos: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                })
                .setNegativeButton(R.string.admin_asesor_distritos_cancelar, null)
                .show();
    }

    private void mostrarDialogoAsignarProyectos(Asesor asesor) {
        if (allCompanyProjects == null || allCompanyProjects.isEmpty()) {
            Toast.makeText(this, "No hay proyectos disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        int size = allCompanyProjects.size();
        CharSequence[] projectNames = new CharSequence[size];
        String[] projectIds = new String[size];
        boolean[] checkedItems = new boolean[size];

        for (int i = 0; i < size; i++) {
            Proyecto p = allCompanyProjects.get(i);
            String distrito = p.getDistrito();
            if (distrito != null && !distrito.isEmpty()) {
                projectNames[i] = p.getNombre() + " (" + distrito + ")";
            } else {
                projectNames[i] = p.getNombre();
            }
            projectIds[i] = p.getId();
            checkedItems[i] = p.getVendedores() != null && p.getVendedores().contains(asesor.getId());
        }

        // Guardar copia del estado inicial para detectar cambios
        final boolean[] originalChecked = new boolean[size];
        System.arraycopy(checkedItems, 0, originalChecked, 0, size);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.admin_asesor_asignar_proyectos_titulo)
                .setMultiChoiceItems(projectNames, checkedItems, (dialog, which, isChecked) -> {
                    checkedItems[which] = isChecked;
                })
                .setPositiveButton(R.string.admin_asesor_asignar_proyectos_guardar, (dialog, which) -> {
                    // Find what changed
                    List<Integer> toUpdate = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        if (checkedItems[i] != originalChecked[i]) {
                            toUpdate.add(i);
                        }
                    }

                    if (toUpdate.isEmpty()) {
                        Toast.makeText(this, "Sin cambios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final int[] completed = {0};
                    for (int idx : toUpdate) {
                        List<String> asesoresIds = new ArrayList<>();
                        if (allCompanyProjects.get(idx).getVendedores() != null) {
                            asesoresIds.addAll(allCompanyProjects.get(idx).getVendedores());
                        }

                        if (checkedItems[idx] && !asesoresIds.contains(asesor.getId())) {
                            asesoresIds.add(asesor.getId());
                        } else if (!checkedItems[idx]) {
                            asesoresIds.remove(asesor.getId());
                        }

                        gateway.updateProjectAsesores(projectIds[idx], asesoresIds,
                                new AdminFirestoreGateway.FirestoreCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        completed[0]++;
                                        if (completed[0] == toUpdate.size()) {
                                            runOnUiThread(() -> {
                                                Toast.makeText(AdminAsesorDetalleCarlosActivity.this,
                                                        "Proyectos actualizados", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        completed[0]++;
                                        if (completed[0] == toUpdate.size()) {
                                            runOnUiThread(() -> {
                                                Toast.makeText(AdminAsesorDetalleCarlosActivity.this,
                                                        "Error al actualizar algunos proyectos", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton(R.string.admin_asesor_asignar_proyectos_cancelar, null)
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

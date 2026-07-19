package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminFirestoreGateway.AdminContext;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.example.inmia.models.ReporteFilter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

public class AdminReportesActivity extends AppCompatActivity
        implements BottomSheetFiltrosReporte.OnFiltrosAplicadosListener {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;

    private RecyclerView recyclerAprobacionesPendientes;
    private TextView tvAprobacionesVacio;
    private SeparacionPendienteAdapter separacionesAdapter;
    private ListenerRegistration separacionesListener;

    private RecyclerView recyclerReporteNuevo;
    private ReporteAdapter reporteAdapter;
    private TextView tvReporteConteo;
    private ProgressBar pbReporteCargando;
    private FrameLayout btnFiltrosReporte;
    private TextView tvBadgeFiltrosReporte;
    private TextView tvFiltrosAplicados;

    private final List<Long> periodosDesde = new ArrayList<>();
    private final List<Long> periodosHasta = new ArrayList<>();
    private final List<String> distritosDisponibles = new ArrayList<>();
    private final List<String> asesoresDisponibles = new ArrayList<>();
    private final List<String> asesoresNombres      = new ArrayList<>();

    private ReporteFilter filtroActual;

    private int totalNotificaciones;
    private AdminFirestoreGateway gateway;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_reportes);

        gateway = new AdminFirestoreGateway();
        filtroActual = new ReporteFilter();

        bottomNav = findViewById(R.id.bottomNavAdmin);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);

        recyclerAprobacionesPendientes = findViewById(R.id.recyclerAprobacionesPendientes);
        tvAprobacionesVacio = findViewById(R.id.tvAprobacionesVacio);

        recyclerReporteNuevo    = findViewById(R.id.recyclerReporteNuevo);
        tvReporteConteo         = findViewById(R.id.tvReporteConteo);
        pbReporteCargando       = findViewById(R.id.pbReporteCargando);
        btnFiltrosReporte       = findViewById(R.id.btnFiltrosReporte);
        tvBadgeFiltrosReporte   = findViewById(R.id.tvBadgeFiltrosReporte);
        tvFiltrosAplicados      = findViewById(R.id.tvFiltrosAplicados);

        recyclerReporteNuevo.setLayoutManager(new LinearLayoutManager(this));
        reporteAdapter = new ReporteAdapter();
        recyclerReporteNuevo.setAdapter(reporteAdapter);

        recyclerAprobacionesPendientes.setLayoutManager(new LinearLayoutManager(this));
        separacionesAdapter = new SeparacionPendienteAdapter(new SeparacionPendienteAdapter.OnAccionClick() {
            @Override
            public void onAprobar(SeparacionPendiente item) {
                gateway.aprobarSeparacion(item.getDocId(), new AdminFirestoreGateway.FirestoreCallback<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        Toast.makeText(AdminReportesActivity.this, "Separación aprobada", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AdminReportesActivity.this, "Error al aprobar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onRechazar(SeparacionPendiente item) {
                gateway.rechazarSeparacion(item.getDocId(), new AdminFirestoreGateway.FirestoreCallback<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        Toast.makeText(AdminReportesActivity.this, "Separación rechazada", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AdminReportesActivity.this, "Error al rechazar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        recyclerAprobacionesPendientes.setAdapter(separacionesAdapter);
        separacionesListener = gateway.observeSeparacionesPendientes(new AdminFirestoreGateway.FirestoreListCallback<SeparacionPendiente>() {
            @Override
            public void onSuccess(List<SeparacionPendiente> value) {
                List<SeparacionPendiente> items = value != null ? value : new ArrayList<>();
                separacionesAdapter.update(items);
                tvAprobacionesVacio.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onError(Exception e) {
                separacionesAdapter.update(new ArrayList<>());
                tvAprobacionesVacio.setVisibility(View.VISIBLE);
            }
        });

        btnFiltrosReporte.setOnClickListener(v -> abrirFiltrosReporte());

        configurarBadge();
        bottomNav.setSelectedItemId(R.id.nav_reportes);

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

                inicializarPeriodos();
                cargarFiltrosDinamicos(companyId);
                cargarReporte(companyId);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminReportesActivity.this, "No se pudo cargar el reporte", Toast.LENGTH_SHORT).show();
            }
        });

        frameNotificaciones.setOnClickListener(v -> {
            Toast.makeText(this,
                    getString(R.string.admin_notificaciones_toast, totalNotificaciones),
                    Toast.LENGTH_SHORT).show();
            limpiarBadge();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (separacionesListener != null) {
            separacionesListener.remove();
            separacionesListener = null;
        }
    }

    private void inicializarPeriodos() {
        Calendar now = Calendar.getInstance();
        Calendar inicioMes = Calendar.getInstance();
        Calendar inicio3Meses = Calendar.getInstance();
        Calendar inicioAnio = Calendar.getInstance();

        inicioMes.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), 1, 0, 0, 0);
        inicioMes.set(Calendar.MILLISECOND, 0);
        inicio3Meses.add(Calendar.MONTH, -3);
        inicio3Meses.set(Calendar.HOUR_OF_DAY, 0);
        inicio3Meses.set(Calendar.MINUTE, 0);
        inicio3Meses.set(Calendar.SECOND, 0);
        inicio3Meses.set(Calendar.MILLISECOND, 0);
        inicioAnio.set(now.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        inicioAnio.set(Calendar.MILLISECOND, 0);

        long ahora = System.currentTimeMillis();

        periodosDesde.clear();
        periodosDesde.add(inicioMes.getTimeInMillis());
        periodosDesde.add(inicio3Meses.getTimeInMillis());
        periodosDesde.add(inicioAnio.getTimeInMillis());
        periodosDesde.add(0L);

        periodosHasta.clear();
        periodosHasta.add(ahora);
        periodosHasta.add(ahora);
        periodosHasta.add(ahora);
        periodosHasta.add(ahora);
    }

    private void cargarFiltrosDinamicos(String companyId) {
        gateway.observeAdvisorsByCompany(companyId, new AdminFirestoreGateway.FirestoreListCallback<com.example.inmia.models.Asesor>() {
            @Override
            public void onSuccess(List<com.example.inmia.models.Asesor> value) {
                asesoresDisponibles.clear();
                asesoresNombres.clear();
                asesoresDisponibles.add("");
                asesoresNombres.add("Todos los asesores");
                if (value != null) {
                    for (com.example.inmia.models.Asesor a : value) {
                        asesoresDisponibles.add(a.getId());
                        String nom = a.getNombre() != null ? a.getNombre() : "Sin nombre";
                        asesoresNombres.add(nom);
                    }
                }
            }
            @Override
            public void onError(Exception e) { }
        });

        FirebaseFirestore.getInstance().collection("proyectos")
                .whereEqualTo("inmobiliariaId", companyId)
                .get()
                .addOnSuccessListener(snap -> {
                    TreeSet<String> distritos = new TreeSet<>();
                    if (snap != null) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot d : snap) {
                            String dis = d.getString("distrito");
                            if (dis != null && !dis.trim().isEmpty()) distritos.add(dis.trim());
                        }
                    }
                    distritosDisponibles.clear();
                    distritosDisponibles.add("Todos los distritos");
                    distritosDisponibles.addAll(distritos);
                })
                .addOnFailureListener(e -> { });
    }

    private void cargarReporte(String companyId) {
        if (filtroActual == null || periodosDesde.isEmpty()) return;

        int idxPeriodo = Math.max(0, Math.min(filtroActual.getPeriodo(), periodosDesde.size() - 1));
        long desde = periodosDesde.get(idxPeriodo);
        long hasta = periodosHasta.get(idxPeriodo);

        pbReporteCargando.setVisibility(View.VISIBLE);
        tvReporteConteo.setText("Cargando...");

        if (filtroActual.getVista() == ReporteFilter.VISTA_POR_ASESOR) {
            gateway.getReportePorAsesor(companyId, desde, hasta,
                    filtroActual.getDistrito(),
                    filtroActual.getAsesorId(),
                    new AdminFirestoreGateway.FirestoreListCallback<ReporteAsesorItem>() {
                        @Override
                        public void onSuccess(List<ReporteAsesorItem> value) {
                            pbReporteCargando.setVisibility(View.GONE);
                            reporteAdapter.setAsesores(value);
                            int total = value == null ? 0 : value.size();
                            tvReporteConteo.setText(total + " asesor" + (total == 1 ? "" : "es"));
                        }
                        @Override
                        public void onError(Exception e) {
                            pbReporteCargando.setVisibility(View.GONE);
                            reporteAdapter.setAsesores(new ArrayList<>());
                            tvReporteConteo.setText("Error al cargar");
                        }
                    });
        } else {
            gateway.getReportePorProyecto(companyId, desde, hasta,
                    filtroActual.getDistrito(),
                    new AdminFirestoreGateway.FirestoreListCallback<ReporteProyectoItem>() {
                        @Override
                        public void onSuccess(List<ReporteProyectoItem> value) {
                            pbReporteCargando.setVisibility(View.GONE);
                            reporteAdapter.setProyectos(value);
                            int total = value == null ? 0 : value.size();
                            tvReporteConteo.setText(total + " proyecto" + (total == 1 ? "" : "s"));
                        }
                        @Override
                        public void onError(Exception e) {
                            pbReporteCargando.setVisibility(View.GONE);
                            reporteAdapter.setProyectos(new ArrayList<>());
                            tvReporteConteo.setText("Error al cargar");
                        }
                    });
        }
    }

    private void abrirFiltrosReporte() {
        BottomSheetFiltrosReporte bottomSheet = BottomSheetFiltrosReporte.newInstance(
                filtroActual,
                distritosDisponibles,
                asesoresDisponibles,
                asesoresNombres);
        bottomSheet.setListener(this);
        bottomSheet.show(getSupportFragmentManager(), "filtros_reporte");
    }

    @Override
    public void onFiltrosAplicados(ReporteFilter filter) {
        filtroActual = filter;
        tvBadgeFiltrosReporte.setVisibility(filtroActual.hasActiveFilters() ? View.VISIBLE : View.GONE);
        String descripcion = filtroActual.describeActiveFilters();
        if (descripcion.isEmpty()) {
            tvFiltrosAplicados.setVisibility(View.GONE);
        } else {
            tvFiltrosAplicados.setVisibility(View.VISIBLE);
            tvFiltrosAplicados.setText(descripcion);
        }
        cargarReporte(companyId);
    }
}

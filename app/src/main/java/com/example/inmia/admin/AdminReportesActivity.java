package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminFirestoreGateway.AdminContext;
import com.example.inmia.admin.data.AdminFirestoreGateway.ReportSnapshot;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.example.inmia.models.Asesor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminReportesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private TextView tvPeriodoReporte;
    private ProgressBar pbMetaVentas;
    private ProgressBar pbTasaCierre;
    private ProgressBar pbLeads;
    private TextView tvMetaVentasPct;
    private TextView tvTasaCierrePct;
    private TextView tvLeadsPct;
    private RecyclerView recyclerViewReportes;
    private ImageView imgAsesorDelMes;
    private TextView tvNombreAsesorDelMes;
    private TextView tvZonaAsesorDelMes;
    private TextView tvVentasAsesorDelMes;
    private TextView tvCitasAsesorDelMes;
    private TextView tvMontoAsesorDelMes;

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

        bottomNav = findViewById(R.id.bottomNavAdmin);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        tvPeriodoReporte = findViewById(R.id.tvPeriodoReporte);
        pbMetaVentas = findViewById(R.id.pbMetaVentas);
        pbTasaCierre = findViewById(R.id.pbTasaCierre);
        pbLeads = findViewById(R.id.pbLeads);
        tvMetaVentasPct = findViewById(R.id.tvMetaVentasPct);
        tvTasaCierrePct = findViewById(R.id.tvTasaCierrePct);
        tvLeadsPct = findViewById(R.id.tvLeadsPct);
        recyclerViewReportes = findViewById(R.id.recyclerViewReportes);
        imgAsesorDelMes = findViewById(R.id.imgAsesorDelMes);
        tvNombreAsesorDelMes = findViewById(R.id.tvNombreAsesorDelMes);
        tvZonaAsesorDelMes = findViewById(R.id.tvZonaAsesorDelMes);
        tvVentasAsesorDelMes = findViewById(R.id.tvVentasAsesorDelMes);
        tvCitasAsesorDelMes = findViewById(R.id.tvCitasAsesorDelMes);
        tvMontoAsesorDelMes = findViewById(R.id.tvMontoAsesorDelMes);

        recyclerViewReportes.setLayoutManager(new LinearLayoutManager(this));

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

                String periodoActual = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

                gateway.saveMonthlyReportHardcoded(companyId, periodoActual, new AdminFirestoreGateway.FirestoreCallback<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        gateway.observeMonthlyReportByCompany(companyId, periodoActual, new AdminFirestoreGateway.FirestoreCallback<ReportSnapshot>() {
                            @Override
                            public void onSuccess(ReportSnapshot snapshot) {
                                if (snapshot == null) {
                                    return;
                                }

                                recyclerViewReportes.setAdapter(new AdminReporteAdapter(snapshot.getItems()));
                                tvPeriodoReporte.setText(getString(R.string.admin_reportes_periodo_demo) + " · " + snapshot.getPeriodLabel());

                                pbMetaVentas.setProgress(snapshot.getMetaVentasPct());
                                pbTasaCierre.setProgress(snapshot.getTasaCierrePct());
                                pbLeads.setProgress(snapshot.getLeadsPct());

                                tvMetaVentasPct.setText(getString(R.string.admin_reportes_pct_format, snapshot.getMetaVentasPct()));
                                tvTasaCierrePct.setText(getString(R.string.admin_reportes_pct_format, snapshot.getTasaCierrePct()));
                                tvLeadsPct.setText(getString(R.string.admin_reportes_pct_format, snapshot.getLeadsPct()));
                            }

                            @Override
                            public void onError(Exception e) {
                                tvPeriodoReporte.setText("Sin reporte disponible");
                                recyclerViewReportes.setAdapter(new AdminReporteAdapter(new java.util.ArrayList<>()));
                                pbMetaVentas.setProgress(0);
                                pbTasaCierre.setProgress(0);
                                pbLeads.setProgress(0);
                                tvMetaVentasPct.setText(getString(R.string.admin_reportes_pct_format, 0));
                                tvTasaCierrePct.setText(getString(R.string.admin_reportes_pct_format, 0));
                                tvLeadsPct.setText(getString(R.string.admin_reportes_pct_format, 0));
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        String periodoActual = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
                        gateway.observeMonthlyReportByCompany(companyId, periodoActual, new AdminFirestoreGateway.FirestoreCallback<ReportSnapshot>() {
                            @Override
                            public void onSuccess(ReportSnapshot snapshot) {
                                if (snapshot == null) {
                                    return;
                                }

                                recyclerViewReportes.setAdapter(new AdminReporteAdapter(snapshot.getItems()));
                                tvPeriodoReporte.setText(getString(R.string.admin_reportes_periodo_demo) + " · " + snapshot.getPeriodLabel());

                                pbMetaVentas.setProgress(snapshot.getMetaVentasPct());
                                pbTasaCierre.setProgress(snapshot.getTasaCierrePct());
                                pbLeads.setProgress(snapshot.getLeadsPct());

                                tvMetaVentasPct.setText(getString(R.string.admin_reportes_pct_format, snapshot.getMetaVentasPct()));
                                tvTasaCierrePct.setText(getString(R.string.admin_reportes_pct_format, snapshot.getTasaCierrePct()));
                                tvLeadsPct.setText(getString(R.string.admin_reportes_pct_format, snapshot.getLeadsPct()));
                            }

                            @Override
                            public void onError(Exception e) {
                                tvPeriodoReporte.setText("Sin reporte disponible");
                                recyclerViewReportes.setAdapter(new AdminReporteAdapter(new java.util.ArrayList<>()));
                                pbMetaVentas.setProgress(0);
                                pbTasaCierre.setProgress(0);
                                pbLeads.setProgress(0);
                                tvMetaVentasPct.setText(getString(R.string.admin_reportes_pct_format, 0));
                                tvTasaCierrePct.setText(getString(R.string.admin_reportes_pct_format, 0));
                                tvLeadsPct.setText(getString(R.string.admin_reportes_pct_format, 0));
                            }
                        });
                    }
                });

                gateway.observeAdvisorsByCompany(companyId, new AdminFirestoreGateway.FirestoreListCallback<Asesor>() {
                    @Override
                    public void onSuccess(List<Asesor> value) {
                        if (value == null || value.isEmpty()) {
                            return;
                        }
                        int randomIndex = (int) (Math.random() * value.size());
                        Asesor asesor = value.get(randomIndex);
                        runOnUiThread(() -> {
                            tvNombreAsesorDelMes.setText(asesor.getNombre());
                            tvZonaAsesorDelMes.setText(asesor.getZonaTrabajo());
                            tvVentasAsesorDelMes.setText(String.valueOf(asesor.getVentasMensualActual()));
                            tvCitasAsesorDelMes.setText(String.valueOf(asesor.getCitasMensualActual()));
                            tvMontoAsesorDelMes.setText(formatSoles(asesor.getGananciasMensualActual()));
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                tvPeriodoReporte.setText("No se pudo cargar el reporte");
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

    private String formatSoles(int monto) {
        if (monto >= 1000000) {
            return "S/ " + String.format(Locale.getDefault(), "%.1fM", monto / 1000000.0);
        } else if (monto >= 1000) {
            return "S/ " + String.format(Locale.getDefault(), "%.1fK", monto / 1000.0);
        } else {
            return "S/ " + monto;
        }
    }
}

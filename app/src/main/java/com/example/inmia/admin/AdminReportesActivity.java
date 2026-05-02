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
import com.example.inmia.admin.data.AdminAsesorRepositoryMock;
import com.example.inmia.models.Asesor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
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

    // Hardcodeado - luego vendra de Firebase
    private int totalNotificaciones = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_reportes);

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

        recyclerViewReportes.setLayoutManager(new LinearLayoutManager(this));

        configurarBadge();
        cargarReporteMock();
        bottomNav.setSelectedItemId(R.id.nav_reportes);

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

    private void cargarReporteMock() {
        List<Asesor> asesores = AdminAsesorRepositoryMock.getAsesores();
        if (asesores == null || asesores.isEmpty()) {
            return;
        }

        int totalMetaVentas = 0;
        int totalMetaCitas = 0;
        int totalMetaGanancias = 0;
        int totalVentas = 0;
        int totalCitas = 0;
        int asesoresActivos = 0;

        Asesor mejorAsesor = asesores.get(0);
        for (Asesor asesor : asesores) {
            totalMetaVentas += asesor.getMetaVentasMensual();
            totalMetaCitas += asesor.getMetaCitasMensual();
            totalMetaGanancias += asesor.getMetaGananciasMensual();
            totalVentas += asesor.getVentasMensualActual();
            totalCitas += asesor.getCitasMensualActual();
            if ("Activo".equalsIgnoreCase(asesor.getEstado())) {
                asesoresActivos++;
            }
            if (asesor.getVentasMensualActual() > mejorAsesor.getVentasMensualActual()) {
                mejorAsesor = asesor;
            }
        }

        int promedioMetaVentas = Math.round(totalMetaVentas / (float) asesores.size());
        int promedioMetaCitas = Math.round(totalMetaCitas / (float) asesores.size());
        int promedioMetaGanancias = Math.round(totalMetaGanancias / (float) asesores.size());

        int pendientes = Math.max(0, totalCitas - totalVentas);

        List<ReporteItem> items = new ArrayList<>();
        items.add(ReporteItem.media(promedioMetaVentas, promedioMetaCitas, formatearSoles(promedioMetaGanancias)));
        items.add(ReporteItem.mejor(mejorAsesor.getNombre(),
                mejorAsesor.getVentasMensualActual(),
                mejorAsesor.getCitasMensualActual(),
                formatearSoles(mejorAsesor.getGananciasMensualActual())));
        items.add(ReporteItem.estado(totalVentas, asesoresActivos, pendientes));

        recyclerViewReportes.setAdapter(new AdminReporteAdapter(items));

        tvPeriodoReporte.setText(getString(R.string.admin_reportes_periodo_demo));

        int metaVentasPct = calcularPorcentaje(totalVentas, totalMetaVentas);
        int tasaCierrePct = calcularPorcentaje(totalVentas, totalCitas);
        int leadsPct = 80;

        pbMetaVentas.setProgress(metaVentasPct);
        pbTasaCierre.setProgress(tasaCierrePct);
        pbLeads.setProgress(leadsPct);
        tvMetaVentasPct.setText(getString(R.string.admin_reportes_pct_format, metaVentasPct));
        tvTasaCierrePct.setText(getString(R.string.admin_reportes_pct_format, tasaCierrePct));
        tvLeadsPct.setText(getString(R.string.admin_reportes_pct_format, leadsPct));
    }

    private int calcularPorcentaje(int actual, int meta) {
        if (meta <= 0) {
            return 0;
        }
        return Math.min(100, Math.round((actual * 100f) / meta));
    }

    private String formatearSoles(int valor) {
        return String.format(Locale.US, "S/ %,d", valor);
    }
}

package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class AdminReportesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private TextView tvPeriodoReporte;
    private TextView tvMejorAsesorNombre;
    private TextView tvMejorAsesorVentas;
    private TextView tvMejorAsesorCitas;
    private TextView tvMejorAsesorConversion;
    private TextView tvMejorAsesorIngresos;
    private TextView tvCasasVendidas;
    private TextView tvAsesoresActivos;
    private TextView tvPendientesCierre;
    private ProgressBar pbMetaVentas;
    private ProgressBar pbTasaCierre;
    private ProgressBar pbLeads;
    private TextView tvMetaVentasPct;
    private TextView tvTasaCierrePct;
    private TextView tvLeadsPct;

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
        tvMejorAsesorNombre = findViewById(R.id.tvMejorAsesorNombre);
        tvMejorAsesorVentas = findViewById(R.id.tvMejorAsesorVentas);
        tvMejorAsesorCitas = findViewById(R.id.tvMejorAsesorCitas);
        tvMejorAsesorConversion = findViewById(R.id.tvMejorAsesorConversion);
        tvMejorAsesorIngresos = findViewById(R.id.tvMejorAsesorIngresos);
        tvCasasVendidas = findViewById(R.id.tvCasasVendidas);
        tvAsesoresActivos = findViewById(R.id.tvAsesoresActivos);
        tvPendientesCierre = findViewById(R.id.tvPendientesCierre);
        pbMetaVentas = findViewById(R.id.pbMetaVentas);
        pbTasaCierre = findViewById(R.id.pbTasaCierre);
        pbLeads = findViewById(R.id.pbLeads);
        tvMetaVentasPct = findViewById(R.id.tvMetaVentasPct);
        tvTasaCierrePct = findViewById(R.id.tvTasaCierrePct);
        tvLeadsPct = findViewById(R.id.tvLeadsPct);

        configurarBadge();
        cargarReporteMock();
        bottomNav.setSelectedItemId(R.id.nav_reportes);

        frameNotificaciones.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Tienes " + totalNotificaciones + " notificaciones",
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
        ReporteMock reporte = new ReporteMock(
                "Abril 2026",
                "Carlos Mendoza",
                7,
                28,
                25,
                654000,
                13,
                9,
                4,
                76,
                64,
                83
        );

        tvPeriodoReporte.setText("Corte: " + reporte.periodo);
        tvMejorAsesorNombre.setText(reporte.mejorAsesorNombre);
        tvMejorAsesorVentas.setText(String.valueOf(reporte.ventasMejorAsesor));
        tvMejorAsesorCitas.setText(String.valueOf(reporte.citasMejorAsesor));
        tvMejorAsesorConversion.setText(reporte.conversionMejorAsesor + "%");
        tvMejorAsesorIngresos.setText(String.format(Locale.US, "S/ %,d", reporte.ingresosMejorAsesor));

        tvCasasVendidas.setText(String.valueOf(reporte.casasVendidasMes));
        tvAsesoresActivos.setText(String.valueOf(reporte.asesoresActivosMes));
        tvPendientesCierre.setText(String.valueOf(reporte.pendientesCierre));

        pbMetaVentas.setProgress(reporte.metaVentasPct);
        pbTasaCierre.setProgress(reporte.tasaCierrePct);
        pbLeads.setProgress(reporte.leadsPct);
        tvMetaVentasPct.setText(reporte.metaVentasPct + "%");
        tvTasaCierrePct.setText(reporte.tasaCierrePct + "%");
        tvLeadsPct.setText(reporte.leadsPct + "%");
    }

    private static class ReporteMock {
        final String periodo;
        final String mejorAsesorNombre;
        final int ventasMejorAsesor;
        final int citasMejorAsesor;
        final int conversionMejorAsesor;
        final int ingresosMejorAsesor;
        final int casasVendidasMes;
        final int asesoresActivosMes;
        final int pendientesCierre;
        final int metaVentasPct;
        final int tasaCierrePct;
        final int leadsPct;

        ReporteMock(String periodo,
                    String mejorAsesorNombre,
                    int ventasMejorAsesor,
                    int citasMejorAsesor,
                    int conversionMejorAsesor,
                    int ingresosMejorAsesor,
                    int casasVendidasMes,
                    int asesoresActivosMes,
                    int pendientesCierre,
                    int metaVentasPct,
                    int tasaCierrePct,
                    int leadsPct) {
            this.periodo = periodo;
            this.mejorAsesorNombre = mejorAsesorNombre;
            this.ventasMejorAsesor = ventasMejorAsesor;
            this.citasMejorAsesor = citasMejorAsesor;
            this.conversionMejorAsesor = conversionMejorAsesor;
            this.ingresosMejorAsesor = ingresosMejorAsesor;
            this.casasVendidasMes = casasVendidasMes;
            this.asesoresActivosMes = asesoresActivosMes;
            this.pendientesCierre = pendientesCierre;
            this.metaVentasPct = metaVentasPct;
            this.tasaCierrePct = tasaCierrePct;
            this.leadsPct = leadsPct;
        }
    }
}



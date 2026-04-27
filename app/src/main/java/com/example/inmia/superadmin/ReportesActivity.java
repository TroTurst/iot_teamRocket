package com.example.inmia.superadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private TextView tabHoy, tabSemana, tabMes, tabAnio, tabRango;
    private TextView tvVerTodo;
    private LineChart lineChart;

    // Fechas del rango seleccionado
    private int diaInicio = -1, mesInicio = -1, anioInicio = -1;
    private int diaFin    = -1, mesFin    = -1, anioFin    = -1;

    // Datos hardcodeados por período — luego vendrán de Firebase
    private final int[][] separacionesPorPeriodo = {
            {12, 19, 15, 22, 18, 25},       // Hoy
            {45, 60, 55, 70, 65, 87},        // Semana
            {120, 180, 150, 200, 170, 340},  // Mes
            {800, 1200, 1000, 1500, 1300, 3200} // Año
    };

    private final int[][] citasPorPeriodo = {
            {8, 14, 10, 18, 12, 20},         // Hoy
            {30, 45, 40, 55, 50, 110},        // Semana
            {90, 140, 120, 160, 130, 420},    // Mes
            {600, 900, 800, 1200, 1000, 4100} // Año
    };

    private final String[][] etiquetasPorPeriodo = {
            {"10h", "12h", "14h", "16h", "18h", "20h"},
            {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"},
            {"S1",  "S2",  "S3",  "S4",  "S5",  "S6"},
            {"Ene", "Feb", "Mar", "Abr", "May", "Jun"}
    };

    private int periodoActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_reportes_superadmin);

        // Vincular vistas
        bottomNav  = findViewById(R.id.bottomNavSuperAdmin);
        tabHoy     = findViewById(R.id.tabHoy);
        tabSemana  = findViewById(R.id.tabSemana);
        tabMes     = findViewById(R.id.tabMes);
        tabAnio    = findViewById(R.id.tabAnio);
        tabRango   = findViewById(R.id.tabRango);
        tvVerTodo  = findViewById(R.id.tvVerTodo);
        lineChart  = findViewById(R.id.lineChart);

        // Configurar gráfica
        configurarGrafica();

        // Filtros de período
        tabHoy.setOnClickListener(v    -> seleccionarPeriodo(0));
        tabSemana.setOnClickListener(v -> seleccionarPeriodo(1));
        tabMes.setOnClickListener(v    -> seleccionarPeriodo(2));
        tabAnio.setOnClickListener(v   -> seleccionarPeriodo(3));

        // ── RANGO: abre DatePicker inicio → luego DatePicker fin ──
        tabRango.setOnClickListener(v  -> seleccionarRango());

        // Ver todo ranking
        tvVerTodo.setOnClickListener(v ->
                startActivity(new Intent(this, RankingCompletoActivity.class)));

        // Bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_reportes);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, SuperAdminHomeActivity.class));
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, GestionUsuariosActivity.class));
                return true;
            } else if (id == R.id.nav_reportes) {
                return true;
            } else if (id == R.id.nav_logs) {
                startActivity(new Intent(this, LogsActivity.class));
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }
            return false;
        });

        // Cargar período inicial
        seleccionarPeriodo(0);
    }

    // ── RANGO DE FECHAS ──────────────────────────────────────────────────────

    private void seleccionarRango() {
        // Primero activar visualmente el tab
        activarTab(tabRango);

        Calendar hoy = Calendar.getInstance();

        // 1. DatePicker — Fecha inicio
        DatePickerDialog pickerInicio = new DatePickerDialog(
                this,
                (view, anio, mes, dia) -> {
                    diaInicio  = dia;
                    mesInicio  = mes;
                    anioInicio = anio;

                    // 2. DatePicker — Fecha fin (mínima = fecha inicio)
                    DatePickerDialog pickerFin = new DatePickerDialog(
                            this,
                            (view2, anio2, mes2, dia2) -> {
                                diaFin  = dia2;
                                mesFin  = mes2;
                                anioFin = anio2;

                                // Actualizar label del tab con el rango
                                String desde = String.format("%02d/%02d", diaInicio, mesInicio + 1);
                                String hasta = String.format("%02d/%02d", diaFin, mesFin + 1);
                                tabRango.setText(desde + " - " + hasta);

                                // TODO: consultar Firebase con el rango seleccionado
                                // Por ahora mostrar datos de "Mes" como placeholder
                                actualizarStats(2);
                                actualizarGrafica(2);

                                Toast.makeText(this,
                                        "Rango: " + desde + " → " + hasta,
                                        Toast.LENGTH_SHORT).show();
                            },
                            hoy.get(Calendar.YEAR),
                            hoy.get(Calendar.MONTH),
                            hoy.get(Calendar.DAY_OF_MONTH)
                    );

                    // Fecha fin no puede ser anterior a fecha inicio
                    Calendar minFin = Calendar.getInstance();
                    minFin.set(anioInicio, mesInicio, diaInicio);
                    pickerFin.getDatePicker().setMinDate(minFin.getTimeInMillis());
                    pickerFin.setTitle("Fecha fin");
                    pickerFin.show();
                },
                hoy.get(Calendar.YEAR),
                hoy.get(Calendar.MONTH),
                hoy.get(Calendar.DAY_OF_MONTH)
        );

        pickerInicio.setTitle("Fecha inicio");
        pickerInicio.show();
    }

    // ── PERÍODOS FIJOS ───────────────────────────────────────────────────────

    private void seleccionarPeriodo(int index) {
        periodoActual = index;

        // Resetear label de rango si cambiamos de tab
        tabRango.setText("Rango");

        // Resetear todos los tabs
        TextView[] tabs = {tabHoy, tabSemana, tabMes, tabAnio, tabRango};
        for (TextView tab : tabs) {
            tab.setBackground(getDrawable(R.drawable.sa_tab_periodo_unselected));
            tab.setTextColor(getColor(R.color.inmia_teal_dark));
        }

        // Activar tab seleccionado
        tabs[index].setBackground(getDrawable(R.drawable.sa_tab_periodo_selected));
        tabs[index].setTextColor(getColor(android.R.color.white));

        actualizarStats(index);
        actualizarGrafica(index);
    }

    private void activarTab(TextView tabSeleccionado) {
        // Resetear todos incluyendo rango
        TextView[] tabs = {tabHoy, tabSemana, tabMes, tabAnio, tabRango};
        for (TextView tab : tabs) {
            tab.setBackground(getDrawable(R.drawable.sa_tab_periodo_unselected));
            tab.setTextColor(getColor(R.color.inmia_teal_dark));
        }
        tabSeleccionado.setBackground(getDrawable(R.drawable.sa_tab_periodo_selected));
        tabSeleccionado.setTextColor(getColor(android.R.color.white));
    }

    private void actualizarStats(int index) {
        TextView tvSep  = findViewById(R.id.tvTotalSeparaciones);
        TextView tvCita = findViewById(R.id.tvTotalCitas);

        int[] sep  = separacionesPorPeriodo[index];
        int[] cita = citasPorPeriodo[index];

        tvSep.setText(String.valueOf(sep[sep.length - 1]));
        tvCita.setText(String.valueOf(cita[cita.length - 1]));
    }

    // ── GRÁFICA ──────────────────────────────────────────────────────────────

    private void configurarGrafica() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setExtraBottomOffset(8f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#A0BFBF"));
        xAxis.setTextSize(9f);
        xAxis.setGranularity(1f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E0EEEE"));
        leftAxis.setTextColor(Color.parseColor("#A0BFBF"));
        leftAxis.setTextSize(9f);
        leftAxis.setAxisMinimum(0f);

        lineChart.getAxisRight().setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
    }

    private void actualizarGrafica(int index) {
        int[] sep       = separacionesPorPeriodo[index];
        int[] cita      = citasPorPeriodo[index];
        String[] etiq   = etiquetasPorPeriodo[index];

        // Dataset separaciones — línea teal
        List<Entry> entriesSep = new ArrayList<>();
        for (int i = 0; i < sep.length; i++) {
            entriesSep.add(new Entry(i, sep[i]));
        }
        LineDataSet dataSetSep = new LineDataSet(entriesSep, "Separaciones");
        dataSetSep.setColor(Color.parseColor("#26C6DA"));
        dataSetSep.setCircleColor(Color.parseColor("#26C6DA"));
        dataSetSep.setLineWidth(2.5f);
        dataSetSep.setCircleRadius(4f);
        dataSetSep.setDrawCircleHole(true);
        dataSetSep.setCircleHoleRadius(2f);
        dataSetSep.setDrawValues(false);
        dataSetSep.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetSep.setDrawFilled(true);
        dataSetSep.setFillColor(Color.parseColor("#1A26C6DA"));
        dataSetSep.setFillAlpha(30);

        // Dataset citas — línea gris suave
        List<Entry> entriesCita = new ArrayList<>();
        for (int i = 0; i < cita.length; i++) {
            entriesCita.add(new Entry(i, cita[i]));
        }
        LineDataSet dataSetCita = new LineDataSet(entriesCita, "Reservas");
        dataSetCita.setColor(Color.parseColor("#B0C4C4"));
        dataSetCita.setCircleColor(Color.parseColor("#B0C4C4"));
        dataSetCita.setLineWidth(2f);
        dataSetCita.setCircleRadius(3.5f);
        dataSetCita.setDrawCircleHole(true);
        dataSetCita.setCircleHoleRadius(1.5f);
        dataSetCita.setDrawValues(false);
        dataSetCita.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetCita.setDrawFilled(true);
        dataSetCita.setFillColor(Color.parseColor("#1AB0C4C4"));
        dataSetCita.setFillAlpha(20);

        // Etiquetas eje X
        lineChart.getXAxis().setValueFormatter(
                new IndexAxisValueFormatter(etiq));

        // Asignar datos y animar
        LineData lineData = new LineData(dataSetSep, dataSetCita);
        lineChart.setData(lineData);
        lineChart.animateX(800);
        lineChart.invalidate();
    }
}
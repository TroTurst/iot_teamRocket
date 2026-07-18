package com.example.inmia.superadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    // Filtros: 0=Semana, 1=Mes, 2=Año, rango manejado aparte
    private TextView tabSemana, tabMes, tabAnio, tabRango;
    private TextView tvVerTodo, tvTotalSeparaciones, tvTotalCitas;
    private TextView tvSubtituloSeparaciones, tvSubtituloCitas;
    private LinearLayout layoutRankingContainer;
    private LineChart lineChart;
    private FirebaseFirestore db;

    private int diaInicio = -1, mesInicio = -1, anioInicio = -1;
    private int diaFin    = -1, mesFin    = -1, anioFin    = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.sa_activity_reportes);

        db = FirebaseFirestore.getInstance();

        bottomNav              = findViewById(R.id.bottomNavSuperAdmin);
        tabSemana              = findViewById(R.id.tabSemana);
        tabMes                 = findViewById(R.id.tabMes);
        tabAnio                = findViewById(R.id.tabAnio);
        tabRango               = findViewById(R.id.tabRango);
        tvVerTodo              = findViewById(R.id.tvVerTodo);
        tvTotalSeparaciones    = findViewById(R.id.tvTotalSeparaciones);
        tvTotalCitas           = findViewById(R.id.tvTotalCitas);
        tvSubtituloSeparaciones = findViewById(R.id.tvSubtituloSeparaciones);
        tvSubtituloCitas        = findViewById(R.id.tvSubtituloCitas);
        layoutRankingContainer = findViewById(R.id.layoutRankingContainer);
        lineChart              = findViewById(R.id.lineChart);

        configurarGrafica();

        tabSemana.setOnClickListener(v -> seleccionarPeriodo(0));
        tabMes.setOnClickListener(v    -> seleccionarPeriodo(1));
        tabAnio.setOnClickListener(v   -> seleccionarPeriodo(2));
        tabRango.setOnClickListener(v  -> seleccionarRango());

        tvVerTodo.setOnClickListener(v ->
                startActivity(new Intent(this, RankingCompletoActivity.class)));

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

        seleccionarPeriodo(0); // inicia en Semana
    }

    // ── SELECCIÓN DE PERÍODO ──────────────────────────────────────────────────

    private void seleccionarPeriodo(int index) {
        tabRango.setText("Rango");
        TextView[] tabs = {tabSemana, tabMes, tabAnio, tabRango};
        for (TextView t : tabs) {
            t.setBackground(getDrawable(R.drawable.sa_tab_periodo_unselected));
            t.setTextColor(getColor(R.color.inmia_teal_dark));
        }
        tabs[index].setBackground(getDrawable(R.drawable.sa_tab_periodo_selected));
        tabs[index].setTextColor(getColor(android.R.color.white));

        Calendar inicio = Calendar.getInstance();
        Calendar fin    = Calendar.getInstance();

        switch (index) {
            case 0: { // Semana — lunes a domingo de la semana actual
                int dow  = inicio.get(Calendar.DAY_OF_WEEK);
                int diff = (dow == Calendar.SUNDAY) ? -6 : Calendar.MONDAY - dow;
                inicio.add(Calendar.DAY_OF_MONTH, diff);
                inicio.set(Calendar.HOUR_OF_DAY, 0);
                inicio.set(Calendar.MINUTE, 0);
                inicio.set(Calendar.SECOND, 0);
                inicio.set(Calendar.MILLISECOND, 0);
                fin = (Calendar) inicio.clone();
                fin.add(Calendar.DAY_OF_MONTH, 6);
                fin.set(Calendar.HOUR_OF_DAY, 23);
                fin.set(Calendar.MINUTE, 59);
                fin.set(Calendar.SECOND, 59);
                fin.set(Calendar.MILLISECOND, 999);
                break;
            }

            case 1: // Mes actual
                inicio.set(Calendar.DAY_OF_MONTH, 1);
                inicio.set(Calendar.HOUR_OF_DAY, 0);
                inicio.set(Calendar.MINUTE, 0);
                inicio.set(Calendar.SECOND, 0);
                inicio.set(Calendar.MILLISECOND, 0);
                fin.set(Calendar.DAY_OF_MONTH, fin.getActualMaximum(Calendar.DAY_OF_MONTH));
                fin.set(Calendar.HOUR_OF_DAY, 23);
                fin.set(Calendar.MINUTE, 59);
                fin.set(Calendar.SECOND, 59);
                fin.set(Calendar.MILLISECOND, 999);
                break;

            case 2: // Año actual
                inicio.set(Calendar.DAY_OF_YEAR, 1);
                inicio.set(Calendar.HOUR_OF_DAY, 0);
                inicio.set(Calendar.MINUTE, 0);
                inicio.set(Calendar.SECOND, 0);
                inicio.set(Calendar.MILLISECOND, 0);
                fin.set(Calendar.MONTH, Calendar.DECEMBER);
                fin.set(Calendar.DAY_OF_MONTH, 31);
                fin.set(Calendar.HOUR_OF_DAY, 23);
                fin.set(Calendar.MINUTE, 59);
                fin.set(Calendar.SECOND, 59);
                fin.set(Calendar.MILLISECOND, 999);
                break;
        }

        actualizarSubtitulosPeriodo(index);
        cargarDatos(inicio, fin, index);
    }

    // Evita que los subtítulos de las cajas ("Separaciones totales") se lean como
    // histórico cuando en realidad muestran solo el período de la pestaña activa.
    private void actualizarSubtitulosPeriodo(int index) {
        String sufijo;
        switch (index) {
            case 0:  sufijo = "esta semana"; break;
            case 1:  sufijo = "este mes";    break;
            case 2:  sufijo = "este año";    break;
            default: sufijo = "en el rango"; break;
        }
        tvSubtituloSeparaciones.setText("Separaciones\n" + sufijo);
        tvSubtituloCitas.setText("Citas\n" + sufijo);
    }

    private void seleccionarRango() {
        TextView[] todos = {tabSemana, tabMes, tabAnio, tabRango};
        for (TextView t : todos) {
            t.setBackground(getDrawable(R.drawable.sa_tab_periodo_unselected));
            t.setTextColor(getColor(R.color.inmia_teal_dark));
        }
        tabRango.setBackground(getDrawable(R.drawable.sa_tab_periodo_selected));
        tabRango.setTextColor(getColor(android.R.color.white));

        Calendar hoy = Calendar.getInstance();
        DatePickerDialog pickerInicio = new DatePickerDialog(this,
                (v, anio, mes, dia) -> {
                    diaInicio = dia; mesInicio = mes; anioInicio = anio;

                    DatePickerDialog pickerFin = new DatePickerDialog(this,
                            (v2, anio2, mes2, dia2) -> {
                                diaFin = dia2; mesFin = mes2; anioFin = anio2;

                                String desde = String.format("%02d/%02d", diaInicio, mesInicio + 1);
                                String hasta = String.format("%02d/%02d", diaFin, mesFin + 1);
                                tabRango.setText(desde + " - " + hasta);

                                Calendar ini = Calendar.getInstance();
                                ini.set(anioInicio, mesInicio, diaInicio, 0, 0, 0);
                                ini.set(Calendar.MILLISECOND, 0);
                                Calendar fn = Calendar.getInstance();
                                fn.set(anioFin, mesFin, diaFin, 23, 59, 59);
                                fn.set(Calendar.MILLISECOND, 999);

                                actualizarSubtitulosPeriodo(3);
                                cargarDatos(ini, fn, 3);
                                Toast.makeText(this, "Rango: " + desde + " → " + hasta,
                                        Toast.LENGTH_SHORT).show();
                            },
                            hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH),
                            hoy.get(Calendar.DAY_OF_MONTH));

                    Calendar minFin = Calendar.getInstance();
                    minFin.set(anioInicio, mesInicio, diaInicio);
                    pickerFin.getDatePicker().setMinDate(minFin.getTimeInMillis());
                    pickerFin.setTitle("Fecha fin");
                    pickerFin.show();
                },
                hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH),
                hoy.get(Calendar.DAY_OF_MONTH));
        pickerInicio.setTitle("Fecha inicio");
        pickerInicio.show();
    }

    // ── FIREBASE ──────────────────────────────────────────────────────────────

    private void cargarDatos(Calendar inicio, Calendar fin, int periodoIndex) {
        Timestamp tsInicio = new Timestamp(inicio.getTime());
        Timestamp tsFin    = new Timestamp(fin.getTime());

        final ArrayList<DocumentSnapshot> separaciones = new ArrayList<>();
        final ArrayList<DocumentSnapshot> citas        = new ArrayList<>();
        final int[] done = {0};

        db.collection("separaciones")
                .whereGreaterThanOrEqualTo("fechaCreacion", tsInicio)
                .whereLessThanOrEqualTo("fechaCreacion", tsFin)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null)
                        separaciones.addAll(task.getResult().getDocuments());
                    if (++done[0] == 2)
                        actualizarUI(separaciones, citas, periodoIndex, inicio, fin);
                });

        db.collection("citas")
                .whereGreaterThanOrEqualTo("fechaCreacion", tsInicio)
                .whereLessThanOrEqualTo("fechaCreacion", tsFin)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null)
                        citas.addAll(task.getResult().getDocuments());
                    if (++done[0] == 2)
                        actualizarUI(separaciones, citas, periodoIndex, inicio, fin);
                });
    }

    private void actualizarUI(List<DocumentSnapshot> separaciones,
                               List<DocumentSnapshot> citas,
                               int periodoIndex, Calendar inicio, Calendar fin) {
        tvTotalSeparaciones.setText(String.valueOf(separaciones.size()));
        tvTotalCitas.setText(String.valueOf(citas.size()));
        actualizarRanking(separaciones);
        actualizarGrafica(separaciones, citas, periodoIndex, inicio, fin);
    }

    // ── RANKING ───────────────────────────────────────────────────────────────

    private void actualizarRanking(List<DocumentSnapshot> separaciones) {
        Map<String, Integer> conteo = new HashMap<>();
        for (DocumentSnapshot doc : separaciones) {
            String nombre = doc.getString("inmobiliariaNombre");
            if (nombre == null || nombre.isEmpty()) continue;
            conteo.put(nombre, conteo.getOrDefault(nombre, 0) + 1);
        }

        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(conteo.entrySet());
        ranking.sort((a, b) -> b.getValue() - a.getValue());

        boolean mostrarVerTodo = ranking.size() > 10;
        tvVerTodo.setVisibility(mostrarVerTodo ? View.VISIBLE : View.GONE);

        List<Map.Entry<String, Integer>> mostrar = mostrarVerTodo
                ? ranking.subList(0, 10) : ranking;

        layoutRankingContainer.removeAllViews();

        if (mostrar.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Sin datos para este período");
            tvEmpty.setTextColor(Color.parseColor("#7A9E9E"));
            tvEmpty.setTextSize(13f);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.topMargin = dpToPx(8);
            lp.bottomMargin = dpToPx(8);
            tvEmpty.setLayoutParams(lp);
            layoutRankingContainer.addView(tvEmpty);
            return;
        }

        for (int i = 0; i < mostrar.size(); i++) {
            if (i > 0) {
                View divider = new View(this);
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                divider.setLayoutParams(dlp);
                divider.setBackgroundColor(Color.parseColor("#F0F0F0"));
                layoutRankingContainer.addView(divider);
            }
            layoutRankingContainer.addView(
                    construirFilaRanking(i + 1,
                            mostrar.get(i).getKey(),
                            mostrar.get(i).getValue()));
        }
    }

    private View construirFilaRanking(int pos, String nombre, int count) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(48)));

        TextView tvBadge = new TextView(this);
        int sz = dpToPx(32);
        tvBadge.setLayoutParams(new LinearLayout.LayoutParams(sz, sz));
        tvBadge.setText(String.valueOf(pos));
        tvBadge.setGravity(Gravity.CENTER);
        tvBadge.setTextSize(13f);
        tvBadge.setTypeface(null, Typeface.BOLD);
        int badgeRes;
        if (pos == 1)      { badgeRes = R.drawable.sa_badge_ranking_1; tvBadge.setTextColor(Color.WHITE); }
        else if (pos == 2) { badgeRes = R.drawable.sa_badge_ranking_2; tvBadge.setTextColor(Color.WHITE); }
        else if (pos == 3) { badgeRes = R.drawable.sa_badge_ranking_3; tvBadge.setTextColor(Color.WHITE); }
        else               { badgeRes = R.drawable.sa_badge_ranking_normal; tvBadge.setTextColor(getColor(R.color.inmia_text)); }
        tvBadge.setBackground(getDrawable(badgeRes));

        TextView tvNombre = new TextView(this);
        LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        nlp.setMarginStart(dpToPx(12));
        tvNombre.setLayoutParams(nlp);
        tvNombre.setText(nombre);
        tvNombre.setTextColor(getColor(R.color.inmia_text));
        tvNombre.setTextSize(14f);
        if (pos <= 3) tvNombre.setTypeface(null, Typeface.BOLD);

        TextView tvCount = new TextView(this);
        tvCount.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tvCount.setText(String.valueOf(count));
        tvCount.setTextColor(getColor(R.color.inmia_text));
        tvCount.setTextSize(15f);
        tvCount.setTypeface(null, Typeface.BOLD);

        fila.addView(tvBadge);
        fila.addView(tvNombre);
        fila.addView(tvCount);
        return fila;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ── GRÁFICA ───────────────────────────────────────────────────────────────

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
        lineChart.getLegend().setEnabled(false);
    }

    private void actualizarGrafica(List<DocumentSnapshot> separaciones,
                                    List<DocumentSnapshot> citas,
                                    int periodoIndex, Calendar inicio, Calendar fin) {
        String[] labels;
        int numBuckets;
        switch (periodoIndex) {
            case 0: // Semana
                labels = new String[]{"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
                numBuckets = 7;
                break;
            case 1: { // Mes — un punto por día real del mes
                int numDias = inicio.getActualMaximum(Calendar.DAY_OF_MONTH);
                numBuckets = numDias;
                labels = new String[numDias];
                for (int d = 1; d <= numDias; d++) {
                    boolean mostrarEtiqueta = (d == 1) || (d == numDias) || (d % 5 == 0);
                    labels[d - 1] = mostrarEtiqueta ? String.valueOf(d) : "";
                }
                break;
            }
            case 2: // Año
                labels = new String[]{"Ene", "Feb", "Mar", "Abr", "May", "Jun",
                                      "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
                numBuckets = 12;
                break;
            default: { // Rango — granularidad según la duración: día, semana o mes
                int spanDias = calcularSpanDias(inicio, fin);
                Calendar cursor = (Calendar) inicio.clone();

                if (spanDias <= 31) {
                    boolean cruzaMes = inicio.get(Calendar.MONTH) != fin.get(Calendar.MONTH)
                            || inicio.get(Calendar.YEAR) != fin.get(Calendar.YEAR);
                    numBuckets = spanDias;
                    labels = new String[numBuckets];
                    int paso = Math.max(1, (int) Math.ceil(numBuckets / 6.0));
                    for (int i = 0; i < numBuckets; i++) {
                        boolean mostrar = (i == 0) || (i == numBuckets - 1) || (i % paso == 0);
                        labels[i] = mostrar
                                ? (cruzaMes
                                    ? cursor.get(Calendar.DAY_OF_MONTH) + "/" + (cursor.get(Calendar.MONTH) + 1)
                                    : String.valueOf(cursor.get(Calendar.DAY_OF_MONTH)))
                                : "";
                        cursor.add(Calendar.DAY_OF_MONTH, 1);
                    }
                } else if (spanDias <= 366) {
                    numBuckets = (int) Math.ceil(spanDias / 7.0);
                    labels = new String[numBuckets];
                    int paso = Math.max(1, (int) Math.ceil(numBuckets / 6.0));
                    for (int i = 0; i < numBuckets; i++) {
                        boolean mostrar = (i == 0) || (i == numBuckets - 1) || (i % paso == 0);
                        labels[i] = mostrar
                                ? cursor.get(Calendar.DAY_OF_MONTH) + "/" + (cursor.get(Calendar.MONTH) + 1)
                                : "";
                        cursor.add(Calendar.DAY_OF_MONTH, 7);
                    }
                } else {
                    numBuckets = Math.max(1, (fin.get(Calendar.YEAR) - inicio.get(Calendar.YEAR)) * 12
                            + (fin.get(Calendar.MONTH) - inicio.get(Calendar.MONTH)) + 1);
                    labels = new String[numBuckets];
                    String[] mesesAbrev = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
                                            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
                    int paso = Math.max(1, (int) Math.ceil(numBuckets / 6.0));
                    for (int i = 0; i < numBuckets; i++) {
                        boolean mostrar = (i == 0) || (i == numBuckets - 1) || (i % paso == 0);
                        labels[i] = mostrar
                                ? mesesAbrev[cursor.get(Calendar.MONTH)] + " " + (cursor.get(Calendar.YEAR) % 100)
                                : "";
                        cursor.add(Calendar.MONTH, 1);
                    }
                }
                break;
            }
        }

        int[] sepBuckets  = bucketear(separaciones, periodoIndex, numBuckets, inicio, fin);
        int[] citaBuckets = bucketear(citas, periodoIndex, numBuckets, inicio, fin);

        List<Entry> entriesSep  = new ArrayList<>();
        List<Entry> entriesCita = new ArrayList<>();
        for (int i = 0; i < numBuckets; i++) {
            entriesSep.add(new Entry(i, sepBuckets[i]));
            entriesCita.add(new Entry(i, citaBuckets[i]));
        }

        LineDataSet dsSep = new LineDataSet(entriesSep, "Separaciones");
        dsSep.setColor(Color.parseColor("#26C6DA"));
        dsSep.setCircleColor(Color.parseColor("#26C6DA"));
        dsSep.setLineWidth(2.5f);
        dsSep.setCircleRadius(4f);
        dsSep.setDrawCircleHole(true);
        dsSep.setCircleHoleRadius(2f);
        dsSep.setDrawValues(false);
        dsSep.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dsSep.setDrawFilled(true);
        dsSep.setFillColor(Color.parseColor("#26C6DA"));
        dsSep.setFillAlpha(30);

        LineDataSet dsCita = new LineDataSet(entriesCita, "Reservas");
        dsCita.setColor(Color.parseColor("#B0C4C4"));
        dsCita.setCircleColor(Color.parseColor("#B0C4C4"));
        dsCita.setLineWidth(2f);
        dsCita.setCircleRadius(3.5f);
        dsCita.setDrawCircleHole(true);
        dsCita.setCircleHoleRadius(1.5f);
        dsCita.setDrawValues(false);
        dsCita.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dsCita.setDrawFilled(true);
        dsCita.setFillColor(Color.parseColor("#B0C4C4"));
        dsCita.setFillAlpha(20);

        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.getXAxis().setLabelCount(numBuckets);
        lineChart.setData(new LineData(dsSep, dsCita));
        lineChart.animateX(800);
        lineChart.invalidate();
    }

    private int[] bucketear(List<DocumentSnapshot> docs, int periodoIndex,
                              int numBuckets, Calendar inicio, Calendar fin) {
        int[] buckets = new int[numBuckets];
        int spanDiasRango = periodoIndex == 3 ? calcularSpanDias(inicio, fin) : 0;

        for (DocumentSnapshot doc : docs) {
            Timestamp ts = doc.getTimestamp("fechaCreacion");
            if (ts == null) continue;
            Calendar c = Calendar.getInstance();
            c.setTime(ts.toDate());

            int bucket;
            switch (periodoIndex) {
                case 0: // Semana — día (Lun=0..Dom=6)
                    int dow = c.get(Calendar.DAY_OF_WEEK);
                    bucket = (dow == Calendar.SUNDAY) ? 6 : dow - Calendar.MONDAY;
                    break;
                case 1: // Mes — día real del mes (0..numDias-1)
                    bucket = c.get(Calendar.DAY_OF_MONTH) - 1;
                    break;
                case 2: // Año — mes (0..11)
                    bucket = c.get(Calendar.MONTH);
                    break;
                default: // Rango — misma granularidad usada al armar las etiquetas
                    if (spanDiasRango <= 31) {
                        bucket = (int) ((medianoche(c) - medianoche(inicio)) / MILLIS_DIA);
                    } else if (spanDiasRango <= 366) {
                        int dia = (int) ((medianoche(c) - medianoche(inicio)) / MILLIS_DIA);
                        bucket = dia / 7;
                    } else {
                        bucket = (c.get(Calendar.YEAR) - inicio.get(Calendar.YEAR)) * 12
                                + (c.get(Calendar.MONTH) - inicio.get(Calendar.MONTH));
                    }
                    break;
            }

            if (bucket >= 0 && bucket < numBuckets) buckets[bucket]++;
        }
        return buckets;
    }

    private static final long MILLIS_DIA = 24L * 60 * 60 * 1000L;

    private long medianoche(Calendar c) {
        Calendar x = (Calendar) c.clone();
        x.set(Calendar.HOUR_OF_DAY, 0);
        x.set(Calendar.MINUTE, 0);
        x.set(Calendar.SECOND, 0);
        x.set(Calendar.MILLISECOND, 0);
        return x.getTimeInMillis();
    }

    private int calcularSpanDias(Calendar inicio, Calendar fin) {
        return (int) ((fin.getTimeInMillis() - inicio.getTimeInMillis()) / MILLIS_DIA) + 1;
    }
}

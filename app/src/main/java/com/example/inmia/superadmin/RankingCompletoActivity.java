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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingCompletoActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private TextView tabHoy, tabSemana, tabMes, tabAnio, tabRango;
    private LinearLayout layoutRankingContainer;
    private FirebaseFirestore db;

    private int diaInicio = -1, mesInicio = -1, anioInicio = -1;
    private int diaFin    = -1, mesFin    = -1, anioFin    = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.sa_activity_ranking_completo);

        db = FirebaseFirestore.getInstance();

        bottomNav              = findViewById(R.id.bottomNavSuperAdmin);
        tabHoy                 = findViewById(R.id.tabHoy);
        tabSemana              = findViewById(R.id.tabSemana);
        tabMes                 = findViewById(R.id.tabMes);
        tabAnio                = findViewById(R.id.tabAnio);
        tabRango               = findViewById(R.id.tabRango);
        layoutRankingContainer = findViewById(R.id.layoutRankingContainer);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tabHoy.setOnClickListener(v    -> seleccionarPeriodo(0));
        tabSemana.setOnClickListener(v -> seleccionarPeriodo(1));
        tabMes.setOnClickListener(v    -> seleccionarPeriodo(2));
        tabAnio.setOnClickListener(v   -> seleccionarPeriodo(3));
        tabRango.setOnClickListener(v  -> seleccionarRango());

        bottomNav.setSelectedItemId(R.id.nav_reportes);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                finish(); return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, GestionUsuariosActivity.class)); return true;
            } else if (id == R.id.nav_reportes) {
                finish(); return true;
            } else if (id == R.id.nav_logs) {
                startActivity(new Intent(this, LogsActivity.class)); return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class)); return true;
            }
            return false;
        });

        seleccionarPeriodo(0);
    }

    // ── SELECCIÓN DE PERÍODO ──────────────────────────────────────────────────

    private void seleccionarPeriodo(int index) {
        tabRango.setText("Rango");
        TextView[] tabs = {tabHoy, tabSemana, tabMes, tabAnio, tabRango};
        for (TextView t : tabs) {
            t.setBackground(getDrawable(R.drawable.sa_tab_periodo_unselected));
            t.setTextColor(getColor(R.color.inmia_teal_dark));
        }
        tabs[index].setBackground(getDrawable(R.drawable.sa_tab_periodo_selected));
        tabs[index].setTextColor(getColor(android.R.color.white));

        Calendar inicio = Calendar.getInstance();
        Calendar fin    = Calendar.getInstance();

        switch (index) {
            case 0:
                inicio.set(Calendar.HOUR_OF_DAY, 0); inicio.set(Calendar.MINUTE, 0);
                inicio.set(Calendar.SECOND, 0);      inicio.set(Calendar.MILLISECOND, 0);
                fin.set(Calendar.HOUR_OF_DAY, 23);   fin.set(Calendar.MINUTE, 59);
                fin.set(Calendar.SECOND, 59);         fin.set(Calendar.MILLISECOND, 999);
                break;

            case 1: {
                int dow  = inicio.get(Calendar.DAY_OF_WEEK);
                int diff = (dow == Calendar.SUNDAY) ? -6 : Calendar.MONDAY - dow;
                inicio.add(Calendar.DAY_OF_MONTH, diff);
                inicio.set(Calendar.HOUR_OF_DAY, 0); inicio.set(Calendar.MINUTE, 0);
                inicio.set(Calendar.SECOND, 0);      inicio.set(Calendar.MILLISECOND, 0);
                fin = (Calendar) inicio.clone();
                fin.add(Calendar.DAY_OF_MONTH, 6);
                fin.set(Calendar.HOUR_OF_DAY, 23);   fin.set(Calendar.MINUTE, 59);
                fin.set(Calendar.SECOND, 59);         fin.set(Calendar.MILLISECOND, 999);
                break;
            }

            case 2:
                inicio.set(Calendar.DAY_OF_MONTH, 1);
                inicio.set(Calendar.HOUR_OF_DAY, 0); inicio.set(Calendar.MINUTE, 0);
                inicio.set(Calendar.SECOND, 0);      inicio.set(Calendar.MILLISECOND, 0);
                fin.set(Calendar.DAY_OF_MONTH, fin.getActualMaximum(Calendar.DAY_OF_MONTH));
                fin.set(Calendar.HOUR_OF_DAY, 23);   fin.set(Calendar.MINUTE, 59);
                fin.set(Calendar.SECOND, 59);         fin.set(Calendar.MILLISECOND, 999);
                break;

            case 3:
                inicio.set(Calendar.DAY_OF_YEAR, 1);
                inicio.set(Calendar.HOUR_OF_DAY, 0); inicio.set(Calendar.MINUTE, 0);
                inicio.set(Calendar.SECOND, 0);      inicio.set(Calendar.MILLISECOND, 0);
                fin.set(Calendar.MONTH, Calendar.DECEMBER); fin.set(Calendar.DAY_OF_MONTH, 31);
                fin.set(Calendar.HOUR_OF_DAY, 23);   fin.set(Calendar.MINUTE, 59);
                fin.set(Calendar.SECOND, 59);         fin.set(Calendar.MILLISECOND, 999);
                break;
        }
        cargarRanking(inicio, fin);
    }

    private void seleccionarRango() {
        TextView[] todos = {tabHoy, tabSemana, tabMes, tabAnio, tabRango};
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
                                cargarRanking(ini, fn);
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

    private void cargarRanking(Calendar inicio, Calendar fin) {
        Timestamp tsInicio = new Timestamp(inicio.getTime());
        Timestamp tsFin    = new Timestamp(fin.getTime());

        db.collection("separaciones")
                .whereGreaterThanOrEqualTo("fechaCreacion", tsInicio)
                .whereLessThanOrEqualTo("fechaCreacion", tsFin)
                .get()
                .addOnSuccessListener(query -> mostrarRanking(query.getDocuments()))
                .addOnFailureListener(e -> mostrarRanking(new ArrayList<>()));
    }

    private void mostrarRanking(List<DocumentSnapshot> separaciones) {
        Map<String, Integer> conteo = new HashMap<>();
        for (DocumentSnapshot doc : separaciones) {
            String nombre = doc.getString("inmobiliariaNombre");
            if (nombre == null || nombre.isEmpty()) continue;
            conteo.put(nombre, conteo.getOrDefault(nombre, 0) + 1);
        }

        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(conteo.entrySet());
        ranking.sort((a, b) -> b.getValue() - a.getValue());

        layoutRankingContainer.removeAllViews();

        if (ranking.isEmpty()) {
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

        for (int i = 0; i < ranking.size(); i++) {
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
                            ranking.get(i).getKey(),
                            ranking.get(i).getValue()));
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private View construirFilaRanking(int pos, String nombre, int count) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(52)));

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
}

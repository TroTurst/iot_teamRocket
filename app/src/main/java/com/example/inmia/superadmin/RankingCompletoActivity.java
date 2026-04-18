package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RankingCompletoActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private TextView tabHoy, tabSemana, tabMes, tabAnio, tabRango;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_ranking_completo);

        bottomNav  = findViewById(R.id.bottomNavSuperAdmin);
        tabHoy     = findViewById(R.id.tabHoy);
        tabSemana  = findViewById(R.id.tabSemana);
        tabMes     = findViewById(R.id.tabMes);
        tabAnio    = findViewById(R.id.tabAnio);
        tabRango   = findViewById(R.id.tabRango);

        // Filtros — misma lógica que ReportesActivity
        tabHoy.setOnClickListener(v    -> seleccionarPeriodo(tabHoy));
        tabSemana.setOnClickListener(v -> seleccionarPeriodo(tabSemana));
        tabMes.setOnClickListener(v    -> seleccionarPeriodo(tabMes));
        tabAnio.setOnClickListener(v   -> seleccionarPeriodo(tabAnio));
        tabRango.setOnClickListener(v  -> {
            Toast.makeText(this, "Selecciona un rango de fechas",
                    Toast.LENGTH_SHORT).show();
        });

        // Bottom nav
        bottomNav.setSelectedItemId(R.id.nav_reportes);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                finish();
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, GestionUsuariosActivity.class));
                return true;
            } else if (id == R.id.nav_reportes) {
                finish(); // volver a reportes
                return true;
            } else if (id == R.id.nav_logs) {
                Toast.makeText(this, "Logs del sistema", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_perfil) {
                Toast.makeText(this, "Mi perfil", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void seleccionarPeriodo(TextView tabSeleccionado) {
        TextView[] tabs = {tabHoy, tabSemana, tabMes, tabAnio, tabRango};
        for (TextView tab : tabs) {
            tab.setBackground(getDrawable(R.drawable.sa_tab_periodo_unselected));
            tab.setTextColor(getColor(R.color.inmia_teal_dark));
        }
        tabSeleccionado.setBackground(getDrawable(R.drawable.sa_tab_periodo_selected));
        tabSeleccionado.setTextColor(getColor(android.R.color.white));
        // TODO: actualizar datos del ranking según período con Firebase
    }
}
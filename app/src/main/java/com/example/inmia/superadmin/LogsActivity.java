package com.example.inmia.superadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;

public class LogsActivity extends AppCompatActivity {

    private MaterialCardView cardFechaInicio, cardFechaFin;
    private TextView tvFechaInicio, tvFechaFin;
    private MaterialButton btnAplicarFiltro;
    private BottomNavigationView bottomNav;

    // Guardar fechas seleccionadas
    private int diaInicio = -1, mesInicio = -1, anioInicio = -1;
    private int diaFin = -1,    mesFin = -1,    anioFin = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_logs);

        // Vincular vistas
        cardFechaInicio  = findViewById(R.id.cardFechaInicio);
        cardFechaFin     = findViewById(R.id.cardFechaFin);
        tvFechaInicio    = findViewById(R.id.tvFechaInicio);
        tvFechaFin       = findViewById(R.id.tvFechaFin);
        btnAplicarFiltro = findViewById(R.id.btnAplicarFiltro);
        bottomNav        = findViewById(R.id.bottomNavSuperAdmin);


        // DatePicker fecha inicio
        cardFechaInicio.setOnClickListener(v -> mostrarDatePicker(true));

        // DatePicker fecha fin
        cardFechaFin.setOnClickListener(v -> mostrarDatePicker(false));

        // Botón aplicar filtro
        btnAplicarFiltro.setOnClickListener(v -> aplicarFiltro());

        // Bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_logs);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                finish();
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, GestionUsuariosActivity.class));
                return true;
            } else if (id == R.id.nav_reportes) {
                startActivity(new Intent(this, ReportesActivity.class));
                return true;
            } else if (id == R.id.nav_logs) {
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }

            return false;
        });
    }

    private void mostrarDatePicker(boolean esFechaInicio) {
        Calendar calendar = Calendar.getInstance();
        int anioActual = calendar.get(Calendar.YEAR);
        int mesActual  = calendar.get(Calendar.MONTH);
        int diaActual  = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, anio, mes, dia) -> {
                    // Formato DD/MM/YYYY
                    String fechaFormateada = String.format("%02d/%02d/%04d",
                            dia, mes + 1, anio);

                    if (esFechaInicio) {
                        diaInicio  = dia;
                        mesInicio  = mes;
                        anioInicio = anio;
                        tvFechaInicio.setText(fechaFormateada);
                        tvFechaInicio.setTextColor(
                                getColor(R.color.inmia_text));
                    } else {
                        diaFin  = dia;
                        mesFin  = mes;
                        anioFin = anio;
                        tvFechaFin.setText(fechaFormateada);
                        tvFechaFin.setTextColor(
                                getColor(R.color.inmia_text));
                    }
                },
                anioActual, mesActual, diaActual
        );

        // Si ya hay fecha de inicio seleccionada y estamos
        // eligiendo fecha fin, no permitir fecha anterior
        if (!esFechaInicio && diaInicio != -1) {
            Calendar minDate = Calendar.getInstance();
            minDate.set(anioInicio, mesInicio, diaInicio);
            datePicker.getDatePicker().setMinDate(minDate.getTimeInMillis());
        }

        datePicker.show();
    }

    private void aplicarFiltro() {
        // Validar que ambas fechas estén seleccionadas
        if (diaInicio == -1) {
            Toast.makeText(this,
                    "Selecciona la fecha de inicio",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (diaFin == -1) {
            Toast.makeText(this,
                    "Selecciona la fecha de fin",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String desde = tvFechaInicio.getText().toString();
        String hasta = tvFechaFin.getText().toString();

        // TODO: filtrar logs desde Firebase por rango de fechas
        Toast.makeText(this,
                "Filtrando del " + desde + " al " + hasta,
                Toast.LENGTH_SHORT).show();
    }
}
package com.example.inmia.superadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Log;
import com.example.inmia.superadmin.adapter.LogAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class LogsActivity extends AppCompatActivity {

    private RecyclerView recyclerLogs;
    private LogAdapter adapter;
    private List<Log> listaCompleta;

    private MaterialCardView cardFechaInicio, cardFechaFin;
    private TextView tvFechaInicio, tvFechaFin;
    private MaterialButton btnAplicarFiltro;
    private BottomNavigationView bottomNav;

    // Fechas del rango
    private int diaInicio = -1, mesInicio = -1, anioInicio = -1;
    private int diaFin    = -1, mesFin    = -1, anioFin    = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_logs);

        // Vincular vistas
        recyclerLogs     = findViewById(R.id.recyclerLogs);
        cardFechaInicio  = findViewById(R.id.cardFechaInicio);
        cardFechaFin     = findViewById(R.id.cardFechaFin);
        tvFechaInicio    = findViewById(R.id.tvFechaInicio);
        tvFechaFin       = findViewById(R.id.tvFechaFin);
        btnAplicarFiltro = findViewById(R.id.btnAplicarFiltro);
        bottomNav        = findViewById(R.id.bottomNavSuperAdmin);

        // Inicializar datos
        inicializarDatos();

        // Configurar RecyclerView
        recyclerLogs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LogAdapter(this, listaCompleta);
        recyclerLogs.setAdapter(adapter);

        // DatePicker fecha inicio
        cardFechaInicio.setOnClickListener(v ->
                mostrarDatePicker(true));

        // DatePicker fecha fin
        cardFechaFin.setOnClickListener(v ->
                mostrarDatePicker(false));

        // Aplicar filtro
        btnAplicarFiltro.setOnClickListener(v -> aplicarFiltro());

        // Bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_logs);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, SuperAdminHomeActivity.class));
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this,
                        GestionUsuariosActivity.class));
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

    // ── Datos hardcodeados ───────────────────────────────────────────────────

    private void inicializarDatos() {
        listaCompleta = new ArrayList<>();

        listaCompleta.add(new Log(
                "George Córdova se ha unido a la aplicación",
                "Hoy 3:59 pm",
                Log.TIPO_USUARIO));

        listaCompleta.add(new Log(
                "El administrador Jhon Travolta ha solicitado habilitar "
                        + "como asesor a Guiliana Sánchez",
                "Ayer 11:39 pm",
                Log.TIPO_ADMIN));

        listaCompleta.add(new Log(
                "Jonás Vélez ha hecho una reserva a la inmobiliaria Sofia",
                "27/03/2026 2:39 am",
                Log.TIPO_RESERVA));

        listaCompleta.add(new Log(
                "María García se ha registrado como asesor de ventas",
                "26/03/2026 10:15 am",
                Log.TIPO_USUARIO));

        listaCompleta.add(new Log(
                "Carlos Rodríguez separó un departamento en Catalina Sky",
                "25/03/2026 8:00 pm",
                Log.TIPO_RESERVA));
    }

    // ── DatePicker ───────────────────────────────────────────────────────────

    private void mostrarDatePicker(boolean esFechaInicio) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, anio, mes, dia) -> {
                    String fechaFormateada = String.format(
                            "%02d/%02d/%04d", dia, mes + 1, anio);

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
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Fecha fin no puede ser anterior a fecha inicio
        if (!esFechaInicio && diaInicio != -1) {
            Calendar minDate = Calendar.getInstance();
            minDate.set(anioInicio, mesInicio, diaInicio);
            datePicker.getDatePicker()
                    .setMinDate(minDate.getTimeInMillis());
        }

        datePicker.show();
    }

    // ── Filtro por rango ─────────────────────────────────────────────────────

    private void aplicarFiltro() {
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

        // TODO: filtrar por rango real cuando conectemos Firebase
        // Por ahora muestra todos los logs como resultado del filtro
        adapter.actualizarLista(listaCompleta);

        String desde = tvFechaInicio.getText().toString();
        String hasta = tvFechaFin.getText().toString();
        Toast.makeText(this,
                "Mostrando logs del " + desde + " al " + hasta,
                Toast.LENGTH_SHORT).show();
    }
}
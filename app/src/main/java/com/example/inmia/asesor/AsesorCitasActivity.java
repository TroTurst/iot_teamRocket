package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AsesorCitasActivity extends AppCompatActivity implements CitaItemAdapter.Listener {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private FrameLayout framePerfil;
    private View btnCalendar;
    private AutoCompleteTextView dropdownEstado;
    private AutoCompleteTextView dropdownProyecto;
    private AutoCompleteTextView dropdownHorario;
    private RecyclerView recyclerCitas;
    private CitaItemAdapter citaAdapter;

    private int totalNotificaciones = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_citas);

        bottomNav = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        framePerfil = findViewById(R.id.framePerfil);
        btnCalendar = findViewById(R.id.btnCalendar);
        dropdownEstado = findViewById(R.id.dropdownEstado);
        dropdownProyecto = findViewById(R.id.dropdownProyecto);
        dropdownHorario = findViewById(R.id.dropdownHorario);
        recyclerCitas = findViewById(R.id.recyclerCitas);

        configurarBadge();
        configurarDropdowns();
        configurarCalendario();

        citaAdapter = new CitaItemAdapter(buildMockCitas(), this);
        recyclerCitas.setLayoutManager(new LinearLayoutManager(this));
        recyclerCitas.setAdapter(citaAdapter);

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        bottomNav.setSelectedItemId(R.id.nav_citas);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, AsesorHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, AsesorChatActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_citas) {
                return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, AsesorSeparacionesActivity.class));
                finish();
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

    public void openCitaDetalle(View view) {
        startActivity(new Intent(this, AsesorCitaDetailActivity.class));
    }

    @Override
    public void onCitaSelected(CitaItem item) {
        startActivity(new Intent(this, AsesorCitaDetailActivity.class));
    }

    private List<CitaItem> buildMockCitas() {
        List<CitaItem> citas = new ArrayList<>();
        citas.add(new CitaItem(
            "Confirmada",
            R.color.inmia_success,
            1f,
            "Los Alamos",
            "Juan Perez",
            "Surco, Primavera 123",
            "Fecha y hora 12/05/2026 - 10:30AM"
        ));
        citas.add(new CitaItem(
            "Pendiente",
            R.color.inmia_teal_dark,
            1f,
            "Catalina Sky",
            "Maria Garcia",
            "Miraflores, Av. Benavides 410",
            "Fecha y hora 14/05/2026 - 4:00PM"
        ));
        citas.add(new CitaItem(
            "Terminada",
            R.color.inmia_text,
            0.7f,
            "Pueblo Libre",
            "Carlos Ruiz",
            "Pueblo Libre, Av. Bolivar 512",
            "Fecha y hora 10/05/2026 - 11:30AM"
        ));
        return citas;
    }

    private void configurarDropdowns() {
        String[] estados = getResources().getStringArray(R.array.citas_estado_options);
        String[] proyectos = getResources().getStringArray(R.array.citas_proyecto_options);
        String[] horarios = getResources().getStringArray(R.array.citas_horario_options);

        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            estados
        );
        ArrayAdapter<String> proyectoAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            proyectos
        );
        ArrayAdapter<String> horarioAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            horarios
        );

        dropdownEstado.setAdapter(estadoAdapter);
        dropdownProyecto.setAdapter(proyectoAdapter);
        dropdownHorario.setAdapter(horarioAdapter);
    }

    private void configurarCalendario() {
        btnCalendar.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona fecha")
                .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formatted = formatter.format(new Date(selection));
                    Toast.makeText(this, "Fecha: " + formatted, Toast.LENGTH_SHORT).show();
                }
            });

            picker.show(getSupportFragmentManager(), "citas_date_picker");
        });
    }
}

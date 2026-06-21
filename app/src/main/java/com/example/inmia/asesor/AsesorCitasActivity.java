package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    private View btnLimpiarFiltrosCitas;
    private EditText etCitasSearch;
    private AutoCompleteTextView dropdownEstado;
    private AutoCompleteTextView dropdownProyecto;
    private AutoCompleteTextView dropdownHorario;
    private RecyclerView recyclerCitas;
    private CitaItemAdapter citaAdapter;
    private List<CitaItem> allCitas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_asesor_citas);

        bottomNav            = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones  = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif         = findViewById(R.id.tvBadgeNotif);
        framePerfil          = findViewById(R.id.framePerfil);
        btnCalendar          = findViewById(R.id.btnCalendar);
        btnLimpiarFiltrosCitas = findViewById(R.id.btnLimpiarFiltrosCitas);
        etCitasSearch        = findViewById(R.id.etCitasSearch);
        dropdownEstado       = findViewById(R.id.dropdownEstado);
        dropdownProyecto     = findViewById(R.id.dropdownProyecto);
        dropdownHorario      = findViewById(R.id.dropdownHorario);
        recyclerCitas        = findViewById(R.id.recyclerCitas);

        AsesorNotificacionStore.seedIfEmpty(this);
        configurarBadge();
        configurarDropdowns();
        configurarCalendario();
        configurarFiltros();

        citaAdapter = new CitaItemAdapter(new ArrayList<>(), this);
        recyclerCitas.setLayoutManager(new LinearLayoutManager(this));
        recyclerCitas.setAdapter(citaAdapter);

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });
        framePerfil.setOnClickListener(v -> startActivity(new Intent(this, AsesorPerfilActivity.class)));

        bottomNav.setSelectedItemId(R.id.nav_citas);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, AsesorHomeActivity.class));
                finish(); return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, AsesorChatActivity.class));
                finish(); return true;
            } else if (id == R.id.nav_citas) {
                return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, AsesorSeparacionesActivity.class));
                finish(); return true;
            }
            return false;
        });

        cargarCitasFirestore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCitasFirestore();
        configurarBadge();
    }

    private void cargarCitasFirestore() {
        AsesorFirestoreRepository.get().getCitas(items -> {
            allCitas = items;
            aplicarFiltros();
        });
    }

    private void configurarBadge() {
        int total = AsesorNotificacionStore.getBadgeCount(this);
        if (total > 0) {
            tvBadgeNotif.setText(String.valueOf(total));
            tvBadgeNotif.setVisibility(View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(View.GONE);
        }
    }

    private void limpiarBadge() {
        AsesorNotificacionStore.clearBadge(this);
        tvBadgeNotif.setVisibility(View.GONE);
    }

    @Override
    public void onCitaSelected(CitaItem item) {
        abrirDetalleCita(item);
    }

    private void configurarFiltros() {
        if (etCitasSearch != null) {
            etCitasSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) { aplicarFiltros(); }
            });
        }
        if (dropdownEstado   != null) dropdownEstado.setOnItemClickListener((p,v,pos,id) -> aplicarFiltros());
        if (dropdownProyecto != null) dropdownProyecto.setOnItemClickListener((p,v,pos,id) -> aplicarFiltros());
        if (dropdownHorario  != null) dropdownHorario.setOnItemClickListener((p,v,pos,id) -> aplicarFiltros());
        if (btnLimpiarFiltrosCitas != null) btnLimpiarFiltrosCitas.setOnClickListener(v -> limpiarFiltros());
    }

    private void configurarDropdowns() {
        String[] estados   = getResources().getStringArray(R.array.citas_estado_options);
        String[] proyectos = getResources().getStringArray(R.array.citas_proyecto_options);
        String[] horarios  = getResources().getStringArray(R.array.citas_horario_options);
        dropdownEstado.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, estados));
        dropdownProyecto.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, proyectos));
        dropdownHorario.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, horarios));
        dropdownEstado.setText("", false);
        dropdownProyecto.setText("", false);
        dropdownHorario.setText("", false);
    }

    private void limpiarFiltros() {
        if (etCitasSearch   != null) etCitasSearch.setText("");
        if (dropdownEstado  != null) dropdownEstado.setText("", false);
        if (dropdownProyecto!= null) dropdownProyecto.setText("", false);
        if (dropdownHorario != null) dropdownHorario.setText("", false);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String texto    = etCitasSearch   != null && etCitasSearch.getText()   != null ? etCitasSearch.getText().toString().trim().toLowerCase(Locale.getDefault())   : "";
        String estado   = dropdownEstado  != null && dropdownEstado.getText()   != null ? dropdownEstado.getText().toString().trim()   : "";
        String proyecto = dropdownProyecto!= null && dropdownProyecto.getText() != null ? dropdownProyecto.getText().toString().trim().toLowerCase(Locale.getDefault()) : "";
        String horario  = dropdownHorario != null && dropdownHorario.getText()  != null ? dropdownHorario.getText().toString().trim()  : "";

        List<CitaItem> filtradas = new ArrayList<>();
        for (CitaItem cita : allCitas) {
            if (!texto.isEmpty()   && !coincideBusqueda(cita, texto))    continue;
            if (!estado.isEmpty()  && !"Todas".equalsIgnoreCase(estado)  && !estado.equalsIgnoreCase(cita.getStatus()))   continue;
            if (!proyecto.isEmpty()&& !cita.getProject().toLowerCase(Locale.getDefault()).contains(proyecto))             continue;
            if (!horario.isEmpty() && !coincideHorario(cita, horario))   continue;
            filtradas.add(cita);
        }
        if (citaAdapter != null) citaAdapter.updateItems(filtradas);
    }

    private boolean coincideBusqueda(CitaItem cita, String texto) {
        return contiene(cita.getStatus(), texto) || contiene(cita.getProject(), texto)
            || contiene(cita.getClient(), texto) || contiene(cita.getLocation(), texto)
            || contiene(cita.getDateTime(), texto);
    }

    private boolean coincideHorario(CitaItem cita, String horario) {
        String detail = cita.getDateTime() == null ? "" : cita.getDateTime().toLowerCase(Locale.getDefault());
        int hour = extraerHora(detail);
        if ("Manana".equalsIgnoreCase(horario)) return hour >= 0 && hour < 12;
        if ("Tarde".equalsIgnoreCase(horario))  return hour >= 12 && hour < 18;
        if ("Noche".equalsIgnoreCase(horario))  return hour >= 18;
        return true;
    }

    private int extraerHora(String detail) {
        try {
            // formato: dd/MM/yyyy HH:mm
            String[] parts = detail.split(" ");
            if (parts.length < 2) return 0;
            String[] timeParts = parts[1].split(":");
            return Integer.parseInt(timeParts[0].trim());
        } catch (Exception ignored) { return 0; }
    }

    private boolean contiene(String value, String texto) {
        return value != null && value.toLowerCase(Locale.getDefault()).contains(texto);
    }

    private void abrirDetalleCita(CitaItem item) {
        Intent intent = new Intent(this, AsesorCitaDetailActivity.class);
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_KEY,          item.getDocId());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_CLIENTE,      item.getClient());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_PROYECTO,     item.getProject());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_ESTADO,       item.getStatus());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_CONFIRMADA,   item.getStatus().equalsIgnoreCase("Confirmada"));
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_UBICACION,    item.getLocation());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_FECHA,        item.getDateTime());
        intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_INMOBILIARIA, item.getInmobiliariaId());
        startActivity(intent);
    }

    private void configurarCalendario() {
        btnCalendar.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona fecha")
                .setTheme(R.style.ThemeOverlay_Inmia_DatePicker)
                .build();
            picker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null) {
                    String formatted = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                    Toast.makeText(this, "Fecha: " + formatted, Toast.LENGTH_SHORT).show();
                }
            });
            picker.show(getSupportFragmentManager(), "citas_date_picker");
        });
    }
}

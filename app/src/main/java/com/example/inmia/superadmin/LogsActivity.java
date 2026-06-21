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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogsActivity extends AppCompatActivity {

    private RecyclerView recyclerLogs;
    private LogAdapter adapter;
    private List<Log> listaLogs;

    private MaterialCardView cardFechaInicio, cardFechaFin;
    private TextView tvFechaInicio, tvFechaFin;
    private MaterialButton btnAplicarFiltro;
    private BottomNavigationView bottomNav;

    private FirebaseFirestore db;

    // Fechas del rango
    private int diaInicio = -1, mesInicio = -1, anioInicio = -1;
    private int diaFin    = -1, mesFin    = -1, anioFin    = -1;

    private final SimpleDateFormat horaFmt  = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
    private final SimpleDateFormat fechaFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_logs);

        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        recyclerLogs     = findViewById(R.id.recyclerLogs);
        cardFechaInicio  = findViewById(R.id.cardFechaInicio);
        cardFechaFin     = findViewById(R.id.cardFechaFin);
        tvFechaInicio    = findViewById(R.id.tvFechaInicio);
        tvFechaFin       = findViewById(R.id.tvFechaFin);
        btnAplicarFiltro = findViewById(R.id.btnAplicarFiltro);
        bottomNav        = findViewById(R.id.bottomNavSuperAdmin);

        // Configurar RecyclerView
        listaLogs = new ArrayList<>();
        recyclerLogs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LogAdapter(this, listaLogs);
        recyclerLogs.setAdapter(adapter);

        // DatePickers
        cardFechaInicio.setOnClickListener(v -> mostrarDatePicker(true));
        cardFechaFin.setOnClickListener(v -> mostrarDatePicker(false));

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

        // Carga inicial: todos los logs, más recientes primero
        cargarLogs(null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar solo si no hay filtro de fechas activo
        if (diaInicio == -1 && diaFin == -1) {
            cargarLogs(null, null);
        }
    }

    // ── FIRESTORE ──────────────────────────────────────────────────────────────

    private void cargarLogs(Timestamp tsInicio, Timestamp tsFin) {
        Query query = db.collection("logs");

        if (tsInicio != null && tsFin != null) {
            query = query.whereGreaterThanOrEqualTo("fechaCreacion", tsInicio)
                         .whereLessThanOrEqualTo("fechaCreacion", tsFin);
        }
        query = query.orderBy("fechaCreacion", Query.Direction.DESCENDING).limit(200);

        query.get()
                .addOnSuccessListener(snapshot -> {
                    listaLogs.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String descripcion = doc.getString("descripcion");
                        String tipo        = doc.getString("tipo");
                        String rol         = doc.getString("rol");
                        Timestamp ts       = doc.getTimestamp("fechaCreacion");

                        listaLogs.add(new Log(
                                descripcion != null ? descripcion : "",
                                formatearFecha(ts),
                                tipo != null ? tipo : Log.TIPO_CUENTA,
                                rol != null ? rol : ""));
                    }
                    adapter.actualizarLista(new ArrayList<>(listaLogs));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar logs", Toast.LENGTH_SHORT).show());
    }

    /** Convierte un Timestamp en "Hoy 3:59 pm" / "Ayer 11:39 pm" / "27/03/2026 2:39 am". */
    private String formatearFecha(Timestamp ts) {
        if (ts == null) return "";
        Date fecha = ts.toDate();

        Calendar cFecha = Calendar.getInstance();
        cFecha.setTime(fecha);
        Calendar hoy = Calendar.getInstance();
        Calendar ayer = Calendar.getInstance();
        ayer.add(Calendar.DAY_OF_YEAR, -1);

        String hora = horaFmt.format(fecha).toLowerCase(Locale.ENGLISH);

        if (mismoDia(cFecha, hoy)) {
            return "Hoy " + hora;
        } else if (mismoDia(cFecha, ayer)) {
            return "Ayer " + hora;
        } else {
            return fechaFmt.format(fecha) + " " + hora;
        }
    }

    private boolean mismoDia(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    // ── DatePicker ───────────────────────────────────────────────────────────

    private void mostrarDatePicker(boolean esFechaInicio) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, anio, mes, dia) -> {
                    String fechaFormateada = String.format(
                            Locale.getDefault(), "%02d/%02d/%04d", dia, mes + 1, anio);

                    if (esFechaInicio) {
                        diaInicio  = dia;
                        mesInicio  = mes;
                        anioInicio = anio;
                        tvFechaInicio.setText(fechaFormateada);
                        tvFechaInicio.setTextColor(getColor(R.color.inmia_text));
                    } else {
                        diaFin  = dia;
                        mesFin  = mes;
                        anioFin = anio;
                        tvFechaFin.setText(fechaFormateada);
                        tvFechaFin.setTextColor(getColor(R.color.inmia_text));
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
            datePicker.getDatePicker().setMinDate(minDate.getTimeInMillis());
        }

        datePicker.show();
    }

    // ── Filtro por rango ─────────────────────────────────────────────────────

    private void aplicarFiltro() {
        if (diaInicio == -1) {
            Toast.makeText(this, "Selecciona la fecha de inicio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (diaFin == -1) {
            Toast.makeText(this, "Selecciona la fecha de fin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inicio del día de inicio
        Calendar inicio = Calendar.getInstance();
        inicio.set(anioInicio, mesInicio, diaInicio, 0, 0, 0);
        inicio.set(Calendar.MILLISECOND, 0);

        // Fin del día de fin
        Calendar fin = Calendar.getInstance();
        fin.set(anioFin, mesFin, diaFin, 23, 59, 59);
        fin.set(Calendar.MILLISECOND, 999);

        cargarLogs(new Timestamp(inicio.getTime()), new Timestamp(fin.getTime()));

        String desde = tvFechaInicio.getText().toString();
        String hasta = tvFechaFin.getText().toString();
        Toast.makeText(this,
                "Mostrando logs del " + desde + " al " + hasta,
                Toast.LENGTH_SHORT).show();
    }
}

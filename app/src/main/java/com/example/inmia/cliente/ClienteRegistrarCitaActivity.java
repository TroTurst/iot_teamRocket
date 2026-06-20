package com.example.inmia.cliente;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.example.inmia.models.Log;
import com.example.inmia.util.LogHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteRegistrarCitaActivity extends AppCompatActivity {

    private TextView btnHora1, btnHora2, btnHora3;
    private TextView btnCambiarProyecto;
    private MaterialButton btnConfirmarCita;
    private LinearLayout containerFechas;
    private TextView tvFechaSeleccionada, tvSubtituloHorarios;

    private TextView tvResumenNombreProyecto, tvResumenUbicacionProyecto, tvResumenTipologia, tvNombreAsesor;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String proyectoNombre = "Proyecto Desconocido";
    private String proyectoId = "";
    private String tipologiaNombre = "";
    private String asesorId = "";
    private String inmobiliariaId = "";

    private int horaSeleccionada = 10;
    private Calendar calendarioElegido = null;
    private List<View> tarjetasFechas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_registrar_cita_cliente);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (getIntent() != null) {
            if (getIntent().hasExtra("PROYECTO_ID")) proyectoId = getIntent().getStringExtra("PROYECTO_ID");
            if (getIntent().hasExtra("PROYECTO_NOMBRE")) proyectoNombre = getIntent().getStringExtra("PROYECTO_NOMBRE");
            if (getIntent().hasExtra("TIPOLOGIA_NOMBRE")) tipologiaNombre = getIntent().getStringExtra("TIPOLOGIA_NOMBRE");
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        inicializarVistas();
        configurarHorarios();
        configurarNavegacion();

        cargarDatosDeFirestore();
    }

    private void inicializarVistas() {
        btnHora1 = findViewById(R.id.btnHora1);
        btnHora2 = findViewById(R.id.btnHora2);
        btnHora3 = findViewById(R.id.btnHora3);
        btnCambiarProyecto = findViewById(R.id.btnCambiarProyecto);
        btnConfirmarCita = findViewById(R.id.btnConfirmarCita);
        containerFechas = findViewById(R.id.containerFechas);
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);
        tvSubtituloHorarios = findViewById(R.id.tvSubtituloHorarios);

        tvResumenNombreProyecto = findViewById(R.id.tvResumenNombreProyecto);
        tvResumenUbicacionProyecto = findViewById(R.id.tvResumenUbicacionProyecto);
        tvResumenTipologia = findViewById(R.id.tvResumenTipologia);
        tvNombreAsesor = findViewById(R.id.tvNombreAsesor);

        tvResumenNombreProyecto.setText(proyectoNombre);
        if (!tipologiaNombre.isEmpty()) {
            tvResumenTipologia.setText("Tipología: " + tipologiaNombre);
        } else {
            tvResumenTipologia.setText("Tipología: Consultar con asesor");
        }

        marcarHorarioSeleccionado(btnHora1);
    }

    private void cargarDatosDeFirestore() {
        if(btnConfirmarCita != null) {
            btnConfirmarCita.setEnabled(false);
            btnConfirmarCita.setText("Cargando datos...");
        }

        db.collection("proyectos").document(proyectoId).get()
                .addOnSuccessListener(docProyecto -> {
                    if (docProyecto.exists()) {
                        inmobiliariaId = docProyecto.getString("inmobiliariaId");

                        Map<String, Object> ubicacion = (Map<String, Object>) docProyecto.get("ubicacion");
                        if (ubicacion != null && ubicacion.get("direccion") != null) {
                            tvResumenUbicacionProyecto.setText(ubicacion.get("direccion").toString());
                        }

                        List<String> asesoresIds = (List<String>) docProyecto.get("asesoresIds");
                        if (asesoresIds != null && !asesoresIds.isEmpty()) {
                            asesorId = asesoresIds.get(0);
                            cargarDatosY_DiasDelAsesor(asesorId);
                        } else {
                            generarCarruselFechas(Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY));
                            btnConfirmarCita.setEnabled(true);
                            btnConfirmarCita.setText("Confirmar Cita");
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar proyecto", Toast.LENGTH_SHORT).show());
    }

    private void cargarDatosY_DiasDelAsesor(String idAsesor) {
        db.collection("usuarios").document(idAsesor).get()
                .addOnSuccessListener(docAsesor -> {
                    if (docAsesor.exists()) {
                        String nombreReal = docAsesor.getString("nombres") + " " + docAsesor.getString("apellidos");
                        tvNombreAsesor.setText(nombreReal);

                        List<Integer> diasAtencion = new ArrayList<>();
                        if (docAsesor.contains("diasAtencion")) {
                            List<Long> diasBD = (List<Long>) docAsesor.get("diasAtencion");
                            for(Long d : diasBD) diasAtencion.add(d.intValue());
                        } else {
                            diasAtencion = Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY);
                        }

                        generarCarruselFechas(diasAtencion);
                    }
                    btnConfirmarCita.setEnabled(true);
                    btnConfirmarCita.setText("Confirmar Cita");
                });
    }

    private void generarCarruselFechas(List<Integer> diasPermitidos) {
        containerFechas.removeAllViews();
        tarjetasFechas.clear();

        Calendar cal = Calendar.getInstance();
        String[] nombresDias = {"", "DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB"};
        String[] nombresMeses = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"};

        int fechasEncontradas = 0;

        while (fechasEncontradas < 10) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            int diaSemana = cal.get(Calendar.DAY_OF_WEEK);

            if (diasPermitidos.contains(diaSemana)) {
                Calendar fechaValida = (Calendar) cal.clone();

                View cardView = LayoutInflater.from(this).inflate(R.layout.item_fecha_calendario, containerFechas, false);

                TextView tvDiaNombre = cardView.findViewById(R.id.tvDiaNombre);
                TextView tvDiaNumero = cardView.findViewById(R.id.tvDiaNumero);
                TextView tvMes = cardView.findViewById(R.id.tvMes);

                tvDiaNombre.setText(nombresDias[diaSemana]);
                tvDiaNumero.setText(String.valueOf(fechaValida.get(Calendar.DAY_OF_MONTH)));
                tvMes.setText(nombresMeses[fechaValida.get(Calendar.MONTH)]);

                cardView.setOnClickListener(v -> {
                    calendarioElegido = fechaValida;
                    actualizarEstadoTarjetas(cardView);

                    String textoFechaStr = nombresDias[diaSemana] + " " + fechaValida.get(Calendar.DAY_OF_MONTH) + " de " + nombresMeses[fechaValida.get(Calendar.MONTH)];
                    tvFechaSeleccionada.setText("Has elegido el: " + textoFechaStr);
                    tvSubtituloHorarios.setText("Disponibles para el " + fechaValida.get(Calendar.DAY_OF_MONTH) + " de " + nombresMeses[fechaValida.get(Calendar.MONTH)]);
                });

                tarjetasFechas.add(cardView);
                containerFechas.addView(cardView);
                fechasEncontradas++;
            }
        }
    }

    private void actualizarEstadoTarjetas(View tarjetaSeleccionada) {
        for (View card : tarjetasFechas) {
            TextView tvDiaNombre = card.findViewById(R.id.tvDiaNombre);
            TextView tvDiaNumero = card.findViewById(R.id.tvDiaNumero);
            TextView tvMes = card.findViewById(R.id.tvMes);

            if (card == tarjetaSeleccionada) {
                card.setBackgroundResource(R.drawable.bg_time_active);
                tvDiaNombre.setTextColor(Color.WHITE);
                tvDiaNumero.setTextColor(Color.WHITE);
                tvMes.setTextColor(Color.WHITE);
            } else {
                card.setBackgroundResource(R.drawable.bg_time_inactive);
                int colorTeal = Color.parseColor("#18C0C1");
                tvDiaNombre.setTextColor(colorTeal);
                tvDiaNumero.setTextColor(colorTeal);
                tvMes.setTextColor(colorTeal);
            }
        }
    }

    private void configurarHorarios() {
        if (btnHora1 != null) btnHora1.setOnClickListener(v -> actualizarHorario(1, 10));
        if (btnHora2 != null) btnHora2.setOnClickListener(v -> actualizarHorario(2, 11));
        if (btnHora3 != null) btnHora3.setOnClickListener(v -> actualizarHorario(3, 12));
    }

    private void actualizarHorario(int opcion, int hora) {
        resetearHorarios();
        this.horaSeleccionada = hora;
        switch (opcion) {
            case 1: marcarHorarioSeleccionado(btnHora1); break;
            case 2: marcarHorarioSeleccionado(btnHora2); break;
            case 3: marcarHorarioSeleccionado(btnHora3); break;
        }
    }

    private void configurarNavegacion() {
        if (btnCambiarProyecto != null) btnCambiarProyecto.setOnClickListener(v -> finish());

        if (btnConfirmarCita != null) {
            btnConfirmarCita.setOnClickListener(v -> {
                if (calendarioElegido == null) {
                    Toast.makeText(this, "Por favor selecciona un día del calendario", Toast.LENGTH_SHORT).show();
                    return;
                }
                btnConfirmarCita.setEnabled(false);
                btnConfirmarCita.setText("Validando...");
                validarYGuardarCita();
            });
        }
    }

    private void validarYGuardarCita() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        String clienteId = currentUser.getUid();

        calendarioElegido.set(Calendar.HOUR_OF_DAY, horaSeleccionada);
        calendarioElegido.set(Calendar.MINUTE, 0);
        calendarioElegido.set(Calendar.SECOND, 0);

        Date fechaInicio = calendarioElegido.getTime();
        Calendar calFin = (Calendar) calendarioElegido.clone();
        calFin.add(Calendar.HOUR_OF_DAY, 1);
        Date fechaFin = calFin.getTime();

        Timestamp tsInicioNuevo = new Timestamp(fechaInicio);
        Timestamp tsFinNuevo = new Timestamp(fechaFin);

        db.collection("citas").whereEqualTo("clienteId", clienteId)
                .whereIn("estado", Arrays.asList("pendiente", "confirmada")).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean hayCruce = false;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Timestamp tsInicioExistente = doc.getTimestamp("fechaHoraInicio");
                            Timestamp tsFinExistente = doc.getTimestamp("fechaHoraFin");

                            if (tsInicioExistente != null && tsFinExistente != null) {
                                if (tsInicioNuevo.compareTo(tsFinExistente) < 0 && tsFinNuevo.compareTo(tsInicioExistente) > 0) {
                                    hayCruce = true;
                                    break;
                                }
                            }
                        }

                        if (hayCruce) {
                            Toast.makeText(this, "Horario cruzado. Ya tienes una cita en este momento.", Toast.LENGTH_LONG).show();
                            btnConfirmarCita.setEnabled(true);
                            btnConfirmarCita.setText("Confirmar Cita");
                        } else {
                            guardarCitaEnFirestore(clienteId, tsInicioNuevo, tsFinNuevo);
                        }
                    }
                });
    }

    private void guardarCitaEnFirestore(String clienteId, Timestamp inicio, Timestamp fin) {
        Map<String, Object> nuevaCita = new HashMap<>();
        nuevaCita.put("clienteId", clienteId);
        nuevaCita.put("asesorId", asesorId);
        nuevaCita.put("proyectoId", proyectoId);
        nuevaCita.put("nombreProyecto", proyectoNombre);
        nuevaCita.put("tipologiaSeleccionada", tipologiaNombre);
        nuevaCita.put("inmobiliariaId", inmobiliariaId);
        nuevaCita.put("fechaHoraInicio", inicio);
        nuevaCita.put("fechaHoraFin", fin);
        nuevaCita.put("estado", "confirmada");
        nuevaCita.put("fechaCreacion", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("citas").add(nuevaCita)
                .addOnSuccessListener(documentReference -> {
                    LogHelper.registrar(
                            "Se agendó una cita en "
                                    + (proyectoNombre != null ? proyectoNombre : "un proyecto"),
                            Log.TIPO_CITA, LogHelper.ROL_CLIENTE, "", clienteId);

                    Toast.makeText(this, "¡Cita agendada con éxito!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, ClienteDetallesCitaActivity2.class);
                    intent.putExtra("CITA_ID", documentReference.getId());
                    startActivity(intent);
                    finish();
                });
    }

    private void resetearHorarios() {
        TextView[] botones = {btnHora1, btnHora2, btnHora3};
        for (TextView btn : botones) {
            if (btn != null) {
                btn.setBackgroundResource(R.drawable.bg_time_inactive);
                btn.setTextColor(Color.parseColor("#18C0C1"));
                btn.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    private void marcarHorarioSeleccionado(TextView btn) {
        if (btn != null) {
            btn.setBackgroundResource(R.drawable.bg_time_active);
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btn.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }
}
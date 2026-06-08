package com.example.inmia.cliente;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClienteDetallesCitaActivity2 extends AppCompatActivity {

    private FirebaseFirestore db;
    private String citaId = "";

    // Vistas
    private TextView tvNombreProyectoDetalle, tvInmobiliariaProyectoDetalle, tvUbicacionProyectoDetalle;
    private TextView tvEstadoReserva, tvFechaHoraCita, tvNombreAsesorCita, tvDetallesTipologiaCita, tvTelefonoAsesorCita;
    private ImageView imgHeroDetalleCita, imgMiniaturaProyecto;
    private MaterialButton btnCancelarCita, btnHablarAsesor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_detalles_cita_cliente2);

        db = FirebaseFirestore.getInstance();

        if (getIntent() != null && getIntent().hasExtra("CITA_ID")) {
            citaId = getIntent().getStringExtra("CITA_ID");
        }

        inicializarVistas();

        if (!citaId.isEmpty()) {
            cargarDetallesDeCita(citaId);
        } else {
            Toast.makeText(this, "No se encontró el ID de la cita", Toast.LENGTH_SHORT).show();
        }
    }

    private void inicializarVistas() {
        tvNombreProyectoDetalle = findViewById(R.id.tvNombreProyectoDetalle);
        tvInmobiliariaProyectoDetalle = findViewById(R.id.tvInmobiliariaProyectoDetalle);
        tvUbicacionProyectoDetalle = findViewById(R.id.tvUbicacionProyectoDetalle);
        tvEstadoReserva = findViewById(R.id.tvEstadoReserva);
        tvFechaHoraCita = findViewById(R.id.tvFechaHoraCita);
        tvNombreAsesorCita = findViewById(R.id.tvNombreAsesorCita);
        tvDetallesTipologiaCita = findViewById(R.id.tvDetallesTipologiaCita);
        tvTelefonoAsesorCita = findViewById(R.id.tvTelefonoAsesorCita);

        imgHeroDetalleCita = findViewById(R.id.imgHeroDetalleCita);
        imgMiniaturaProyecto = findViewById(R.id.imgMiniaturaProyecto);

        btnHablarAsesor = findViewById(R.id.btnHablarAsesor);
        btnCancelarCita = findViewById(R.id.btnCancelarCita);

        FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }


        if (btnCancelarCita != null) {
            btnCancelarCita.setOnClickListener(v -> cancelarCitaActual());
        }
    }

    private void cargarDetallesDeCita(String idCita) {
        db.collection("citas").document(idCita).get().addOnSuccessListener(docCita -> {
            if (docCita.exists()) {

                tvNombreProyectoDetalle.setText(docCita.getString("nombreProyecto"));

                String estado = docCita.getString("estado");
                if (estado != null) {
                    tvEstadoReserva.setText(estado.toUpperCase());
                    if (estado.equalsIgnoreCase("cancelada")) {
                        tvEstadoReserva.setTextColor(Color.parseColor("#FF4C4C"));
                        btnCancelarCita.setEnabled(false);
                        btnCancelarCita.setText("Cita Cancelada");
                    } else if (estado.equalsIgnoreCase("pendiente")) {
                        tvEstadoReserva.setTextColor(Color.parseColor("#FFA000"));
                    } else {
                        tvEstadoReserva.setTextColor(Color.parseColor("#2ECC71"));
                    }
                }

                String tipologia = docCita.getString("tipologiaSeleccionada");
                tvDetallesTipologiaCita.setText(tipologia != null ? tipologia : "No especificada");

                Timestamp tsInicio = docCita.getTimestamp("fechaHoraInicio");
                if (tsInicio != null) {
                    Date date = tsInicio.toDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault());
                    tvFechaHoraCita.setText(sdf.format(date));
                }

                String proyectoId = docCita.getString("proyectoId");
                String asesorId = docCita.getString("asesorId");
                String inmobiliariaId = docCita.getString("inmobiliariaId");

                if (proyectoId != null) {
                    db.collection("proyectos").document(proyectoId).get().addOnSuccessListener(docProyecto -> {
                        if (docProyecto.exists()) {
                            Map<String, Object> ubicacion = (Map<String, Object>) docProyecto.get("ubicacion");
                            if (ubicacion != null && ubicacion.get("direccion") != null) {
                                tvUbicacionProyectoDetalle.setText(ubicacion.get("direccion").toString());
                            }

                            List<String> imagenes = (List<String>) docProyecto.get("imagenesUrls");
                            if (imagenes != null && !imagenes.isEmpty()) {
                                Glide.with(this).load(imagenes.get(0)).into(imgHeroDetalleCita);
                                Glide.with(this).load(imagenes.get(0)).into(imgMiniaturaProyecto);
                            }
                        }
                    });
                }

                if (asesorId != null) {
                    db.collection("usuarios").document(asesorId).get().addOnSuccessListener(docAsesor -> {
                        if (docAsesor.exists()) {
                            String nombreAsesor = docAsesor.getString("nombres");
                            tvNombreAsesorCita.setText(nombreAsesor != null ? nombreAsesor : "Asesor");

                            String telefono = docAsesor.getString("telefono");
                            tvTelefonoAsesorCita.setText(telefono != null ? telefono : "No registrado");

                            if (btnHablarAsesor != null && nombreAsesor != null) {
                                btnHablarAsesor.setOnClickListener(v -> buscarOAbrirChat(nombreAsesor));
                            }
                        }
                    });
                }

                if (inmobiliariaId != null) {
                    db.collection("inmobiliarias").document(inmobiliariaId).get().addOnSuccessListener(docInmob -> {
                        if (docInmob.exists()) tvInmobiliariaProyectoDetalle.setText(docInmob.getString("nombre"));
                    });
                }
            }
        });
    }

    private void cancelarCitaActual() {
        if (citaId.isEmpty()) return;

        btnCancelarCita.setEnabled(false);
        btnCancelarCita.setText("Cancelando...");

        db.collection("citas").document(citaId)
                .update("estado", "cancelada")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Tu cita ha sido cancelada", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, ClienteCitasActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cancelar la cita", Toast.LENGTH_SHORT).show();
                    btnCancelarCita.setEnabled(true);
                    btnCancelarCita.setText("Cancelar cita");
                });
    }

    private void buscarOAbrirChat(String nombreAsesor) {
        String miUid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnHablarAsesor.setText("Buscando chat...");
        btnHablarAsesor.setEnabled(false);

        db.collection("chats")
                .whereEqualTo("clienteId", miUid)
                .whereEqualTo("asesorNombre", nombreAsesor)
                .get()
                .addOnSuccessListener(query -> {
                    btnHablarAsesor.setText("Hablar con el asesor");
                    btnHablarAsesor.setEnabled(true);

                    if (!query.isEmpty()) {
                        // ¡Encontramos el chat! Sacamos su ID y nos vamos
                        String chatId = query.getDocuments().get(0).getId();
                        Intent intent = new Intent(this, ClienteChatActivity.class);
                        intent.putExtra("CHAT_ID", chatId);
                        intent.putExtra("ASESOR_NOMBRE", nombreAsesor);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Aún no tienes un chat creado con " + nombreAsesor, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    btnHablarAsesor.setText("Hablar con el asesor");
                    btnHablarAsesor.setEnabled(true);
                    Toast.makeText(this, "Error de red al buscar chat", Toast.LENGTH_SHORT).show();
                });
    }
}
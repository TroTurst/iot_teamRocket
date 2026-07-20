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
import com.example.inmia.models.Log;
import com.example.inmia.util.LogHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClienteDetallesCitaActivity2 extends AppCompatActivity {

    private FirebaseFirestore db;
    private String citaId = "";

    private TextView tvNombreProyectoDetalle, tvInmobiliariaProyectoDetalle, tvUbicacionProyectoDetalle;
    private TextView tvEstadoReserva, tvFechaHoraCita, tvNombreAsesorCita, tvDetallesTipologiaCita, tvTelefonoAsesorCita;
    private ImageView imgHeroDetalleCita, imgMiniaturaProyecto;
    private MaterialButton btnCancelarCita, btnHablarAsesor;


    private FirebaseAuth mAuth;


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

        mAuth = FirebaseAuth.getInstance();

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

                            String fotoUrlAsesor = docAsesor.getString("fotoUrl");

                            if (btnHablarAsesor != null && nombreAsesor != null) {
                                btnHablarAsesor.setOnClickListener(v -> buscarOAbrirChat(nombreAsesor, asesorId, fotoUrlAsesor));
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
                    String proy = tvNombreProyectoDetalle != null
                            ? tvNombreProyectoDetalle.getText().toString().trim() : "";
                    LogHelper.registrar(
                            "Se canceló una cita" + (!proy.isEmpty() ? " en " + proy : ""),
                            Log.TIPO_CITA, LogHelper.ROL_CLIENTE);


                    NotificacionHelper.crearNotifCita(mAuth.getCurrentUser().getUid(), "Sistema", "cancelada", citaId);
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

    private void buscarOAbrirChat(String nombreAsesor, String asesorId, String fotoAsesorUrl) {
        String miUid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnHablarAsesor.setText("Abriendo chat...");
        btnHablarAsesor.setEnabled(false);

        db.collection("chats")
                .whereEqualTo("clienteId", miUid)
                .whereEqualTo("asesorId", asesorId)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        btnHablarAsesor.setText("Hablar con el asesor");
                        btnHablarAsesor.setEnabled(true);

                        String chatId = query.getDocuments().get(0).getId();
                        abrirPantallaChat(chatId, nombreAsesor);
                    } else {
                        crearNuevoChat(miUid, asesorId, nombreAsesor, fotoAsesorUrl);
                    }
                })
                .addOnFailureListener(e -> {
                    btnHablarAsesor.setText("Hablar con el asesor");
                    btnHablarAsesor.setEnabled(true);
                    Toast.makeText(this, "Error de red al buscar chat", Toast.LENGTH_SHORT).show();
                });
    }

    private void crearNuevoChat(String miUid, String asesorId, String nombreAsesor, String fotoAsesorUrl) {
        db.collection("usuarios").document(miUid).get()
                .addOnSuccessListener(docCliente -> {
                    String clienteNombre = "Cliente";
                    String clienteTelefono = "";

                    if (docCliente.exists()) {
                        clienteNombre = docCliente.getString("nombres") != null ? docCliente.getString("nombres") : "Cliente";
                        clienteTelefono = docCliente.getString("telefono") != null ? docCliente.getString("telefono") : "";
                    }

                    Map<String, Object> nuevoChat = new HashMap<>();
                    nuevoChat.put("asesorId", asesorId);
                    nuevoChat.put("asesorNombre", nombreAsesor);
                    nuevoChat.put("clienteId", miUid);
                    nuevoChat.put("clienteNombre", clienteNombre);
                    nuevoChat.put("clienteTelefono", clienteTelefono);
                    nuevoChat.put("favoritoAsesor", false);
                    nuevoChat.put("fotoAsesorUrl", fotoAsesorUrl != null ? fotoAsesorUrl : "");
                    nuevoChat.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
                    nuevoChat.put("ultimoMensaje", "Chat iniciado");

                    db.collection("chats").add(nuevoChat)
                            .addOnSuccessListener(documentReference -> {
                                btnHablarAsesor.setText("Hablar con el asesor");
                                btnHablarAsesor.setEnabled(true);

                                abrirPantallaChat(documentReference.getId(), nombreAsesor);
                            })
                            .addOnFailureListener(e -> {
                                btnHablarAsesor.setText("Hablar con el asesor");
                                btnHablarAsesor.setEnabled(true);
                                Toast.makeText(this, "Error al crear el chat", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnHablarAsesor.setText("Hablar con el asesor");
                    btnHablarAsesor.setEnabled(true);
                    Toast.makeText(this, "Error al obtener tus datos", Toast.LENGTH_SHORT).show();
                });
    }

    private void abrirPantallaChat(String chatId, String nombreAsesor) {
        Intent intent = new Intent(this, ClienteChatActivity.class);
        intent.putExtra("CHAT_ID", chatId);
        intent.putExtra("ASESOR_NOMBRE", nombreAsesor);
        startActivity(intent);
    }
}
package com.example.inmia.cliente;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClienteSeparacionAprobadaActivity extends AppCompatActivity {

    private MaterialButton btnProcederPago, btnCancelarSeparacion;
    private FrameLayout btnBack;
    private ShapeableImageView imgProyectoDetalle;
    private TextView tvNombreProyectoDetalle, tvTipologiaDetalle, tvUbicacionDetalle, tvMontoDetalle, tvEstadoMensaje;
    private LinearLayout bannerEstado;

    private FirebaseFirestore db;
    private String separacionId = "";
    private String estadoSeparacion = "";

    private String proyectoId = "", proyectoNombre = "", ubicacion = "", inmobiliariaNombre = "", tipologia = "", imagenUrl = "";
    private double montoSeparacion = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_separacion_aprobada_cliente);

        db = FirebaseFirestore.getInstance();

        if (getIntent() != null) {
            separacionId = getIntent().getStringExtra("SEPARACION_ID");
            estadoSeparacion = getIntent().getStringExtra("ESTADO_SEPARACION");
        }

        inicializarVistas();
        configurarBotonYMensajeSegunEstado();
        cargarDatosDeSeparacion();

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (btnCancelarSeparacion != null) {
            btnCancelarSeparacion.setOnClickListener(v -> {
                Toast.makeText(this, "Trámite cancelado", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void inicializarVistas() {
        btnProcederPago = findViewById(R.id.btnProcederPago);
        btnCancelarSeparacion = findViewById(R.id.btnCancelarSeparacion);
        btnBack = findViewById(R.id.btnBack);

        imgProyectoDetalle = findViewById(R.id.imgProyectoDetalle);
        tvNombreProyectoDetalle = findViewById(R.id.tvNombreProyectoDetalle);
        tvTipologiaDetalle = findViewById(R.id.tvTipologiaDetalle);
        tvUbicacionDetalle = findViewById(R.id.tvUbicacionDetalle);
        tvMontoDetalle = findViewById(R.id.tvMontoDetalle);
        tvEstadoMensaje = findViewById(R.id.tvEstadoMensaje);
        bannerEstado = findViewById(R.id.bannerEstado);
    }

    private void configurarBotonYMensajeSegunEstado() {
        if (estadoSeparacion == null) return;
        String estado = estadoSeparacion.trim().toLowerCase();

        if (btnCancelarSeparacion != null) {
            btnCancelarSeparacion.setVisibility(View.VISIBLE);
        }

        if (estado.equals("en proceso")) {
            bannerEstado.setBackgroundColor(Color.parseColor("#FFF3E0"));
            tvEstadoMensaje.setTextColor(Color.parseColor("#E65100"));
            tvEstadoMensaje.setText("Estamos validando tu solicitud. Te notificaremos cuando el asesor lo apruebe para poder pagar.");

            btnProcederPago.setEnabled(false);
            btnProcederPago.setText("Esperando Aprobación...");
            btnProcederPago.setBackgroundColor(Color.parseColor("#9E9E9E"));

        } else if (estado.equals("aprobada")) {
            bannerEstado.setBackgroundColor(Color.parseColor("#E8F5E9"));
            tvEstadoMensaje.setTextColor(Color.parseColor("#2E7D32"));
            tvEstadoMensaje.setText("¡Felicidades! Tu separación ha sido aprobada. Ya puedes proceder a realizar el pago.");

            btnProcederPago.setEnabled(true);
            btnProcederPago.setText("Pagar Separación");
            btnProcederPago.setBackgroundColor(Color.parseColor("#0A3D46"));

        } else if (estado.equals("pagada")) {
            bannerEstado.setBackgroundColor(Color.parseColor("#E8F5E9"));
            tvEstadoMensaje.setTextColor(Color.parseColor("#2E7D32"));
            tvEstadoMensaje.setText("Este trámite ya fue pagado exitosamente. La unidad está separada para ti.");

            btnProcederPago.setEnabled(false);
            btnProcederPago.setText("Trámite Pagado");
            btnProcederPago.setBackgroundColor(Color.parseColor("#2ECC71"));

            if (btnCancelarSeparacion != null) {
                btnCancelarSeparacion.setVisibility(View.GONE);
            }

        } else {
            bannerEstado.setBackgroundColor(Color.parseColor("#FFEBEE"));
            tvEstadoMensaje.setTextColor(Color.parseColor("#C62828"));
            tvEstadoMensaje.setText("Lamentablemente tu solicitud no fue aprobada o la unidad ya fue vendida.");

            btnProcederPago.setEnabled(false);
            btnProcederPago.setText("Trámite Rechazado");
            btnProcederPago.setBackgroundColor(Color.parseColor("#FF4C4C"));
        }
    }

    private void cargarDatosDeSeparacion() {
        if (separacionId == null || separacionId.isEmpty()) return;

        db.collection("separaciones").document(separacionId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        proyectoId = document.getString("proyectoId");
                        proyectoNombre = document.getString("nombreProyecto");
                        ubicacion = document.getString("ubicacion");
                        inmobiliariaNombre = document.getString("inmobiliariaNombre");
                        tipologia = document.getString("tipologia");
                        imagenUrl = document.getString("imagenUrl");

                        Double montoBD = document.getDouble("montoSeparacion");
                        montoSeparacion = montoBD != null ? montoBD : 0.0;

                        tvNombreProyectoDetalle.setText(proyectoNombre);
                        tvTipologiaDetalle.setText(tipologia);
                        tvUbicacionDetalle.setText(ubicacion);
                        tvMontoDetalle.setText(String.format("S/ %,.2f", montoSeparacion));

                        if (imagenUrl != null && !imagenUrl.isEmpty()) {
                            Glide.with(this).load(imagenUrl).placeholder(R.drawable.onboarding1).into(imgProyectoDetalle);
                        }

                        configurarClicPago();
                    }
                });
    }

    private void configurarClicPago() {
        if (btnProcederPago != null && btnProcederPago.isEnabled()) {
            btnProcederPago.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClientePagoSeparacionActivity.class);
                intent.putExtra("SEPARACION_ID", separacionId);
                intent.putExtra("PROYECTO_ID", proyectoId);
                intent.putExtra("PROYECTO_NOMBRE", proyectoNombre);
                intent.putExtra("UBICACION", ubicacion);
                intent.putExtra("INMOBILIARIA_NOMBRE", inmobiliariaNombre);
                intent.putExtra("TIPOLOGIA", tipologia);
                intent.putExtra("IMAGEN_URL", imagenUrl);
                intent.putExtra("MONTO_SEPARACION", montoSeparacion);

                startActivity(intent);
            });
        }
    }
}
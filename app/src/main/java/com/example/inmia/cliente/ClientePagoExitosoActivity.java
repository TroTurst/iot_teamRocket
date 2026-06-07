package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ClientePagoExitosoActivity extends AppCompatActivity {

    private MaterialButton btnVolverInicio;
    private MaterialButton btnDescargarComprobante;

    private TextView tvMontoExitoso, tvReferenciaExitoso, tvFechaExitoso, tvMetodoPagoExitoso, tvProyectoExitoso, tvTipologiaExitoso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_pago_exitoso_cliente);

        inicializarVistas();
        recibirYPintarDatos();

        if (btnVolverInicio != null) {
            btnVolverInicio.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (btnDescargarComprobante != null) {
            btnDescargarComprobante.setOnClickListener(v -> {
                Toast.makeText(this, "Descargando comprobante PDF...", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void inicializarVistas() {
        btnVolverInicio = findViewById(R.id.btnVolverInicio);
        btnDescargarComprobante = findViewById(R.id.btnDescargarComprobante);

        tvMontoExitoso = findViewById(R.id.tvMontoExitoso);
        tvReferenciaExitoso = findViewById(R.id.tvReferenciaExitoso);
        tvFechaExitoso = findViewById(R.id.tvFechaExitoso);
        tvMetodoPagoExitoso = findViewById(R.id.tvMetodoPagoExitoso);
        tvProyectoExitoso = findViewById(R.id.tvProyectoExitoso);
        tvTipologiaExitoso = findViewById(R.id.tvTipologiaExitoso);
    }

    private void recibirYPintarDatos() {
        if (getIntent() != null) {
            String nombreProyecto = getIntent().getStringExtra("PROYECTO_NOMBRE");
            String tipologia = getIntent().getStringExtra("TIPOLOGIA");
            double monto = getIntent().getDoubleExtra("MONTO_SEPARACION", 0.0);

            if (nombreProyecto != null) tvProyectoExitoso.setText("Separación " + nombreProyecto);
            if (tipologia != null) tvTipologiaExitoso.setText(tipologia);
            tvMontoExitoso.setText(String.format("S/ %,.2f", monto));

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            String fechaActual = sdf.format(new Date());
            tvFechaExitoso.setText(fechaActual);

            Random random = new Random();
            int codigoTrx = 1000000 + random.nextInt(9000000);
            tvReferenciaExitoso.setText("#TRX-" + codigoTrx);

            tvMetodoPagoExitoso.setText("Tarjeta •••• 4832");
        }
    }
}
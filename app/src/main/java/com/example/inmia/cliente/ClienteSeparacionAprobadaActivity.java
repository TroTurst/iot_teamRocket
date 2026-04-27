package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;

public class ClienteSeparacionAprobadaActivity extends AppCompatActivity {

    private MaterialButton btnProcederPago;
    private MaterialButton btnCancelarSeparacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        setContentView(R.layout.activity_separacion_aprobada_cliente);

        btnProcederPago = findViewById(R.id.btnProcederPago);
        btnCancelarSeparacion = findViewById(R.id.btnCancelarSeparacion);

        if (btnProcederPago != null) {
            btnProcederPago.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClientePagoSeparacionActivity.class);
                startActivity(intent);
            });
        }

        if (btnCancelarSeparacion != null) {
            btnCancelarSeparacion.setOnClickListener(v -> {
                finish();
            });
        }
        android.widget.FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
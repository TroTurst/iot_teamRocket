package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ClienteDetallesCitaActivity extends AppCompatActivity {

    private MaterialButton btnHablarAsesor;
    private MaterialButton btnVolverInicio;
    private MaterialCardView btnBackWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_detalles_cita_cliente);

        inicializarVistas();

        configurarNavegacion();
    }
    private void inicializarVistas() {
        btnHablarAsesor = findViewById(R.id.btnHablarAsesor);
        btnVolverInicio = findViewById(R.id.btnVolverInicio);
        btnBackWhite = findViewById(R.id.btnBackWhite);
    }

    private void configurarNavegacion() {
        if (btnHablarAsesor != null) {
            btnHablarAsesor.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteChatActivity.class);
                startActivity(intent);
            });
        }

        if (btnVolverInicio != null) {
            btnVolverInicio.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (btnBackWhite != null) {
            btnBackWhite.setOnClickListener(v -> {
                finish();
            });
        }
    }
}
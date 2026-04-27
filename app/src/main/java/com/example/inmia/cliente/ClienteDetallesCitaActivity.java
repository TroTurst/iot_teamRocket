package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;

public class ClienteDetallesCitaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_detalles_cita_cliente2);

        FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        MaterialButton btnHablarAsesor = findViewById(R.id.btnHablarAsesor);
        if (btnHablarAsesor != null) {
            btnHablarAsesor.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteChatActivity.class);
                startActivity(intent);
            });
        }

        MaterialButton btnCancelarCita = findViewById(R.id.btnCancelarCita);
        if (btnCancelarCita != null) {
            btnCancelarCita.setOnClickListener(v -> {
                finish();
            });
        }
    }
}
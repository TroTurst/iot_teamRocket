package com.example.inmia.cliente;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.example.inmia.R;

public class ClienteDetallesCitaActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_detalles_cita_cliente2);
        android.widget.FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
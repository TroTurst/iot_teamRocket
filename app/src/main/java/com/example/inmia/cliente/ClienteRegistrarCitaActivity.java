package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;

public class ClienteRegistrarCitaActivity extends AppCompatActivity {
    private TextView btnHora1, btnHora2, btnHora3;
    private TextView btnCambiarProyecto;
    private MaterialButton btnConfirmarCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_registrar_cita_cliente);
        android.widget.FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        inicializarVistas();
        configurarHorarios();
        configurarNavegacion();
    }
    private void inicializarVistas() {
        btnHora1 = findViewById(R.id.btnHora1);
        btnHora2 = findViewById(R.id.btnHora2);
        btnHora3 = findViewById(R.id.btnHora3);

        btnCambiarProyecto = findViewById(R.id.btnCambiarProyecto);
        btnConfirmarCita = findViewById(R.id.btnConfirmarCita);
    }

    private void configurarNavegacion() {
        if (btnCambiarProyecto != null) {
            btnCambiarProyecto.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (btnConfirmarCita != null) {
            btnConfirmarCita.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteDetallesCitaActivity.class);
                startActivity(intent);
            });
        }
    }
    private void configurarHorarios() {
        if (btnHora1 != null) btnHora1.setOnClickListener(v -> actualizarHorario(1));
        if (btnHora2 != null) btnHora2.setOnClickListener(v -> actualizarHorario(2));
        if (btnHora3 != null) btnHora3.setOnClickListener(v -> actualizarHorario(3));
    }

    private void actualizarHorario(int opcion) {
        resetearHorarios();

        switch (opcion) {
            case 1:
                marcarHorarioSeleccionado(btnHora1);
                break;
            case 2:
                marcarHorarioSeleccionado(btnHora2);
                break;
            case 3:
                marcarHorarioSeleccionado(btnHora3);
                break;
        }
    }

    private void resetearHorarios() {
        TextView[] botones = {btnHora1, btnHora2, btnHora3};

        for (TextView btn : botones) {
            if (btn != null) {
                btn.setBackgroundResource(R.drawable.bg_time_inactive);
                btn.setTextColor(android.graphics.Color.parseColor("#18C0C1"));
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
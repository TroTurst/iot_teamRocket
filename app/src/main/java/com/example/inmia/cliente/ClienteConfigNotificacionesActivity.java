package com.example.inmia.cliente;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ClienteConfigNotificacionesActivity extends AppCompatActivity {

    private SwitchMaterial switchSeparacion;
    private SwitchMaterial switchReserva;
    private SwitchMaterial switchMensaje;
    private SwitchMaterial switchPagos;

    private static final String PREFS_NAME = "NotificacionesPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_config_notificaciones_cliente);

        inicializarVistas();
        cargarPreferencias();
        configurarListeners();
    }

    private void inicializarVistas() {
        switchSeparacion = findViewById(R.id.switchSeparacion);
        switchReserva = findViewById(R.id.switchReserva);
        switchMensaje = findViewById(R.id.switchMensaje);
        switchPagos = findViewById(R.id.switchPagos);

        FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void cargarPreferencias() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        switchSeparacion.setChecked(settings.getBoolean("notif_separacion", true));
        switchReserva.setChecked(settings.getBoolean("notif_reserva", true));
        switchMensaje.setChecked(settings.getBoolean("notif_mensaje", true));
        switchPagos.setChecked(settings.getBoolean("notif_pagos", true));
    }

    private void configurarListeners() {
        switchSeparacion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            guardarPreferencia("notif_separacion", isChecked);
            mostrarMensaje(isChecked ? "Notificaciones de Separación activadas" : "Notificaciones de Separación desactivadas");
        });

        switchReserva.setOnCheckedChangeListener((buttonView, isChecked) -> {
            guardarPreferencia("notif_reserva", isChecked);
            mostrarMensaje(isChecked ? "Notificaciones de Reserva activadas" : "Notificaciones de Reserva desactivadas");
        });

        switchMensaje.setOnCheckedChangeListener((buttonView, isChecked) -> {
            guardarPreferencia("notif_mensaje", isChecked);
            mostrarMensaje(isChecked ? "Notificaciones de Mensajes activadas" : "Notificaciones de Mensajes desactivadas");
        });

        switchPagos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            guardarPreferencia("notif_pagos", isChecked);
            mostrarMensaje(isChecked ? "Notificaciones de Pagos activadas" : "Notificaciones de Pagos desactivadas");
        });
    }

    private void guardarPreferencia(String key, boolean value) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inmia.R;
import com.example.inmia.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClientePerfilClienteActivity extends AppCompatActivity {
    private ImageView btnEditarDatos;
    private LinearLayout layoutMetodosPago;
    private LinearLayout layoutSeguridad;
    private LinearLayout layoutNotificaciones;
    private LinearLayout layoutCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_perfil_cliente);

        inicializarVistas();
        configurarNavegacion();
        if (layoutCerrarSesion != null) {
            layoutCerrarSesion.setOnClickListener(v -> {
                mostrarDialogoCerrarSesion();
            });
        }
        configurarMenuInferior();
    }
    private void inicializarVistas() {
        btnEditarDatos = findViewById(R.id.btnEditarDatos);
        layoutMetodosPago = findViewById(R.id.layoutMetodosPago);
        layoutSeguridad = findViewById(R.id.layoutSeguridad);
        layoutNotificaciones = findViewById(R.id.layoutNotificaciones);
        layoutCerrarSesion = findViewById(R.id.layoutCerrarSesion);
    }
    private void configurarNavegacion() {
        if (btnEditarDatos != null) {
            btnEditarDatos.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteInformacionPersonalActivity.class);
                startActivity(intent);
            });
        }

        if (layoutMetodosPago != null) {
            layoutMetodosPago.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteTusTarjetasActivity.class);
                startActivity(intent);
            });
        }

        if (layoutSeguridad != null) {
            layoutSeguridad.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteSeguridadActivity.class);
                startActivity(intent);
            });
        }

        if (layoutNotificaciones != null) {
            layoutNotificaciones.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteConfigNotificacionesActivity.class);
                startActivity(intent);
            });
        }


    }
    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void configurarMenuInferior() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_perfil);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    startActivity(new Intent(this, ClienteHomeActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_citas) {
                    startActivity(new Intent(this, ClienteCitasActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(this, ClienteMensajesActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_perfil) {
                    return true; 
                } else if (id == R.id.nav_separaciones) {
                    startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}
package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;

public class ClienteHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private EditText etSearch;
    private ImageButton btnLocation;
    private Button btnSearch;
    private ChipGroup chipGroupFilters;
    private CardView cardProject1, cardProject2, cardProject3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_cliente_home2_cliente);

        inicializarVistas();
        configurarListeners();
    }

    private void inicializarVistas() {
        etSearch = findViewById(R.id.etSearch);
        btnLocation = findViewById(R.id.btnLocation);
        btnSearch = findViewById(R.id.btnSearch);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        cardProject1 = findViewById(R.id.cardProject1);
        cardProject2 = findViewById(R.id.cardProject2);
        cardProject3 = findViewById(R.id.cardProject3);
        bottomNav = findViewById(R.id.bottomNavCliente);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
    }

    private void configurarListeners() {


        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteNotificacionesActivity.class));
        });

        etSearch.setFocusable(false);
        etSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteExplorarMapaActivity.class));
        });


        cardProject1.setOnClickListener(v -> abrirDetalleProyecto("Palm Living"));
        cardProject2.setOnClickListener(v -> abrirDetalleProyecto("Verde Living"));
        cardProject3.setOnClickListener(v -> abrirDetalleProyecto("Park Side"));


        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    return true;
                } else if (id == R.id.nav_citas) {
                    startActivity(new Intent(this, ClienteCitasActivity.class));
                    return true;
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(this, ClienteMensajesActivity.class));
                    return true;
                } else if (id == R.id.nav_separaciones) {
                    startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    private void abrirDetalleProyecto(String nombreProyecto) {
        Intent intent = new Intent(this, ClienteDetallePropiedadActivity.class);
        intent.putExtra("PROYECTO_NOMBRE", nombreProyecto);
        startActivity(intent);
    }
}
package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class ClienteHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private EditText etSearch;
    private ImageButton btnLocation;
    private Button btnSearch;
    private ChipGroup chipGroupFilters;
    private RecyclerView rvProyectos;
    private TextView tvSearchDummy;


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
        bottomNav = findViewById(R.id.bottomNavCliente);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        RecyclerView rvProyectos = findViewById(R.id.rvProyectos);
        rvProyectos.setLayoutManager(new GridLayoutManager(this, 2));
        List<Proyecto> misProyectos = new ArrayList<>();

        misProyectos.add(new Proyecto("Palm Living", "San Isidro, Lima", "Desde S./85,000", "Planos", R.drawable.onboarding1)); // Asegúrate de tener estas imágenes
        misProyectos.add(new Proyecto("Verde Living", "Miraflores", "Desde S./120,000", "Planos", R.drawable.onboarding2));
        misProyectos.add(new Proyecto("Park Side", "Pueblo Libre", "Desde S./72,000", "En preventa", R.drawable.onboarding3));
        misProyectos.add(new Proyecto("Ocean View", "Magdalena", "Desde S./95,000", "Venta", R.drawable.onboarding1)); // Repito imagen de ejemplo
        for (int i = 4; i <= 13; i++) {
            misProyectos.add(new Proyecto(
                    "Proyecto Extra " + i,
                    "Distrito " + i,
                    "Desde S./100,000",
                    "Venta",
                    R.drawable.onboarding1
            ));
        }
        ProyectosAdapter adapter = new ProyectosAdapter(misProyectos);
        rvProyectos.setAdapter(adapter);

        tvSearchDummy = findViewById(R.id.etSearchReal);

    }

    private void configurarListeners() {


        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteBuzonNotificacionesActivity.class));
        });

        etSearch.setFocusable(false);
        etSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteExplorarMapaActivity.class));
        });



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
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(this, ClientePerfilClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_separaciones) {
                    startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                    return true;
                }
                return false;
            });
        }
        btnLocation.setOnClickListener(v -> {
            Intent intent = new Intent(ClienteHomeActivity.this, ClienteExplorarMapaActivity.class);
            startActivity(intent);
        });

        etSearch.setFocusable(false);
        etSearch.setOnClickListener(v -> {
            startActivity(new Intent(ClienteHomeActivity.this, ClienteBuscarActivity.class));
        });
    }

    private void abrirDetalleProyecto(String nombreProyecto) {
        Intent intent = new Intent(this, ClienteDetallePropiedadActivity.class);
        intent.putExtra("PROYECTO_NOMBRE", nombreProyecto);
        startActivity(intent);
    }
}
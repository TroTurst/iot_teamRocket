package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ClienteSeparacionesActivity extends AppCompatActivity {
    private MaterialButton btnDetalles1;
    private MaterialButton btnDetalles2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_separaciones_cliente);

        inicializarVistas();
        configurarNavegacion();
        configurarMenuInferior();
    }

    private void inicializarVistas() {
        btnDetalles1 = findViewById(R.id.btnDetalles1);
        btnDetalles2 = findViewById(R.id.btnDetalles2);
    }

    private void configurarNavegacion() {
        if (btnDetalles1 != null) {
            btnDetalles1.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteSeparacionAprobadaActivity.class);
                intent.putExtra("NOMBRE_PROYECTO", "Palm Living");
                startActivity(intent);
            });
        }

        if (btnDetalles2 != null) {
            btnDetalles2.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteSeparacionAprobadaActivity.class);
                intent.putExtra("NOMBRE_PROYECTO", "Catalina Sky");
                startActivity(intent);
            });
        }
    }

    private void configurarMenuInferior() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_separaciones);

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
                    startActivity(new Intent(this, ClientePerfilClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_separaciones) {
                    return true;
                }
                return false;
            });
        }
    }
}
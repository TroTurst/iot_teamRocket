package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class ClienteCitasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_citas_cliente);

        RecyclerView rvCitas = findViewById(R.id.rvCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(this));

        List<Cita> misCitas = new ArrayList<>();
        misCitas.add(new Cita("En proceso", "Palm Living", "San Isidro, Lima", "GALEON INMOBILIARIA"));
        misCitas.add(new Cita("Cancelado", "Catalina Sky", "Callao, Lima", "GALEON INMOBILIARIA"));
        misCitas.add(new Cita("En proceso", "Verde Living", "Miraflores, Lima", "INMOBILIARIA SUR"));

        CitasAdapter adapter = new CitasAdapter(misCitas);
        rvCitas.setAdapter(adapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        bottomNav.setSelectedItemId(R.id.nav_citas);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, ClienteHomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_citas) {
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
                startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}
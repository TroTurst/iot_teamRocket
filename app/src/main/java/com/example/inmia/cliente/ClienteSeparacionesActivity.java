package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ClienteSeparacionesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_separaciones_cliente);

        RecyclerView rvSeparaciones = findViewById(R.id.rvSeparaciones);
        rvSeparaciones.setLayoutManager(new LinearLayoutManager(this));

        List<Separacion> misSeparaciones = new ArrayList<>();
        misSeparaciones.add(new Separacion("En espera de Aprobación", "Palm Living", "San Isidro, Lima", "GALEON INMOBILIARIA", R.drawable.onboarding1));
        misSeparaciones.add(new Separacion("No aprobada", "Catalina Sky", "Callao, Lima", "GALEON INMOBILIARIA", R.drawable.onboarding3));
        misSeparaciones.add(new Separacion("Aprobada", "Verde Living", "Miraflores, Lima", "INMOBILIARIA SUR", R.drawable.onboarding2));

        SeparacionAdapter adapter = new SeparacionAdapter(misSeparaciones);
        rvSeparaciones.setAdapter(adapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);

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
                return true; // Ya estamos aquí
            }
            return false;
        });
    }
}
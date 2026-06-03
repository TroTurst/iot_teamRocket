package com.example.inmia.cliente;
import com.example.inmia.models.Notificacion;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ClienteBuzonNotificacionesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_notificaciones_cliente);

        FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        RecyclerView rvNotificaciones = findViewById(R.id.rvNotificaciones);
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(this));

        List<Notificacion> misNotificaciones = new ArrayList<>();
        misNotificaciones.add(new Notificacion("Se confirmó tu solicitud para Palm Living", "Ayer 11:39 pm", "EXITO"));
        misNotificaciones.add(new Notificacion("Se canceló tu separación para Catalina Sky", "27/03/2026 2:39 pm", "ERROR"));
        misNotificaciones.add(new Notificacion("Nuevo mensaje de tu asesor Carlos Mendoza", "26/03/2026 10:00 am", "EXITO"));

        NotificacionAdapter adapter = new NotificacionAdapter(misNotificaciones);
        rvNotificaciones.setAdapter(adapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_inicio) {
                    startActivity(new Intent(this, ClienteHomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_citas) {
                    startActivity(new Intent(this, ClienteCitasActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(this, ClienteMensajesActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(this, ClientePerfilClienteActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}
package com.example.inmia.asesor;

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

public class AsesorNotificacionesActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout framePerfil;
    private RecyclerView recyclerNotificacionesHoy;
    private RecyclerView recyclerNotificacionesAyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_notificaciones);

        bottomNav = findViewById(R.id.bottomNavAsesor);
        framePerfil = findViewById(R.id.framePerfil);
        recyclerNotificacionesHoy = findViewById(R.id.recyclerNotificacionesHoy);
        recyclerNotificacionesAyer = findViewById(R.id.recyclerNotificacionesAyer);

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        NotificacionItemAdapter hoyAdapter = new NotificacionItemAdapter(buildMockNotificacionesHoy());
        recyclerNotificacionesHoy.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotificacionesHoy.setAdapter(hoyAdapter);

        NotificacionItemAdapter ayerAdapter = new NotificacionItemAdapter(buildMockNotificacionesAyer());
        recyclerNotificacionesAyer.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotificacionesAyer.setAdapter(ayerAdapter);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, AsesorHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, AsesorChatActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(this, AsesorCitasActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, AsesorSeparacionesActivity.class));
                finish();
                return true;
            }

            return false;
        });
    }

    private List<NotificacionItem> buildMockNotificacionesHoy() {
        List<NotificacionItem> items = new ArrayList<>();
        items.add(new NotificacionItem(
            "George Cordova confirmo una cita para Palm Living",
            "Cita confirmada",
            "Hoy 3:59 pm",
            R.color.inmia_info,
            R.drawable.ic_citas,
            R.color.inmia_info,
            R.color.inmia_teal_light,
            0.8f
        ));
        items.add(new NotificacionItem(
            "Adrian solicita separacion para Palm Living Dpto 402",
            "Separacion solicitada",
            "Hoy 2:39 am",
            R.color.inmia_teal_dark,
            R.drawable.ic_separaciones,
            R.color.inmia_teal_dark,
            R.color.inmia_teal_light,
            0.8f
        ));
        items.add(new NotificacionItem(
            "La separacion de Maria Quispe fue aprobada",
            "Separacion aprobada",
            "Hoy 9:14 am",
            R.color.inmia_success,
            R.drawable.ic_separaciones,
            R.color.inmia_success,
            R.color.inmia_teal_light,
            0.8f
        ));
        return items;
    }

    private List<NotificacionItem> buildMockNotificacionesAyer() {
        List<NotificacionItem> items = new ArrayList<>();
        items.add(new NotificacionItem(
            "Juan Perez cancelo su cita del 06/04/2026",
            "Cita cancelada",
            "Ayer 11:39 pm",
            R.color.inmia_danger,
            R.drawable.ic_citas,
            R.color.inmia_danger,
            R.color.inmia_line,
            1f
        ));
        items.add(new NotificacionItem(
            "Carlos Mendoza realizo el pago de S/ 2,000",
            "Pago recibido",
            "Ayer 4:20 pm",
            R.color.inmia_warning,
            R.drawable.ic_reportes,
            R.color.inmia_warning,
            R.color.inmia_line,
            1f
        ));
        return items;
    }
}

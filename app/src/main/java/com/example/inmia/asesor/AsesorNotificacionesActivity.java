package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        AsesorNotificacionStore.seedIfEmpty(this);

        bottomNav = findViewById(R.id.bottomNavAsesor);
        framePerfil = findViewById(R.id.framePerfil);
        recyclerNotificacionesHoy = findViewById(R.id.recyclerNotificacionesHoy);
        recyclerNotificacionesAyer = findViewById(R.id.recyclerNotificacionesAyer);

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        AsesorNotificacionStore.clearBadge(this);

        NotificacionItemAdapter hoyAdapter = new NotificacionItemAdapter(
            AsesorNotificacionStore.getNotificacionesHoy(this),
            this::abrirDestinoNotificacion
        );
        recyclerNotificacionesHoy.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotificacionesHoy.setAdapter(hoyAdapter);

        NotificacionItemAdapter ayerAdapter = new NotificacionItemAdapter(
            AsesorNotificacionStore.getNotificacionesAyer(this),
            this::abrirDestinoNotificacion
        );
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

    private void abrirDestinoNotificacion(NotificacionItem item) {
        if (item == null || item.getTargetType() == null) {
            return;
        }

        if (AsesorNotificacionStore.TARGET_CHAT_DETAIL.equals(item.getTargetType())) {
            ChatThread thread = AsesorChatStore.getThreadById(this, item.getTargetId());
            if (thread != null) {
                Intent intent = new Intent(this, AsesorChatDetailActivity.class);
                intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_ID, thread.getId());
                intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_NAME, thread.getName());
                startActivity(intent);
            }
            return;
        }

        if (AsesorNotificacionStore.TARGET_CITA_DETAIL.equals(item.getTargetType())) {
            AsesorCitaStore.CitaRecord record = AsesorCitaStore.getRecordByKey(this, item.getTargetId());
            if (record != null) {
                Intent intent = new Intent(this, AsesorCitaDetailActivity.class);
                intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_CLIENTE, record.client);
                intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_PROYECTO, record.project);
                intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_ESTADO, record.status);
                intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_CONFIRMADA, record.confirmed);
                intent.putExtra(AsesorCitaDetailActivity.EXTRA_CITA_KEY, record.key);
                startActivity(intent);
            }
            return;
        }

        if (AsesorNotificacionStore.TARGET_SEPARACION_DETAIL.equals(item.getTargetType())) {
            AsesorSeparacionStore.SeparacionRecord record = AsesorSeparacionStore.getRecordByKey(this, item.getTargetId());
            if (record != null) {
                Intent intent = new Intent(this, AsesorSeparacionDetailActivity.class);
                intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_KEY, record.key);
                intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_CLIENTE, record.client);
                intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_PROYECTO, record.project);
                intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_ESTADO, record.status);
                intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_CONFIRMADA, record.confirmed);
                startActivity(intent);
            }
        }
    }
}

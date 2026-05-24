package com.example.inmia.superadmin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.superadmin.db.AppDatabase;
import com.example.inmia.superadmin.db.NotificacionSAEntity;
import java.util.List;

public class NotificacionesSuperAdminActivity extends AppCompatActivity {

    private RecyclerView recyclerNotificaciones;
    private TextView tvSinNotificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_notificaciones);

        recyclerNotificaciones = findViewById(R.id.recyclerNotificaciones);
        tvSinNotificaciones    = findViewById(R.id.tvSinNotificaciones);

        // Botón atrás
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Marcar todas como leídas al abrir la vista
        AppDatabase db = AppDatabase.getInstance(this);
        db.notificacionDao().marcarTodasLeidas();

        // Limpiar badge
        new SessionManager(this).limpiarNotificaciones();

        // Cargar lista
        List<NotificacionSAEntity> lista = db.notificacionDao().obtenerTodas();

        if (lista.isEmpty()) {
            tvSinNotificaciones.setVisibility(View.VISIBLE);
            recyclerNotificaciones.setVisibility(View.GONE);
        } else {
            tvSinNotificaciones.setVisibility(View.GONE);
            recyclerNotificaciones.setVisibility(View.VISIBLE);
            recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(this));
            recyclerNotificaciones.setAdapter(
                    new NotificacionSAAdapter(this, lista));
        }

    }
}

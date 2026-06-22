package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Notificacion;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClienteBuzonNotificacionesActivity extends AppCompatActivity {

    private RecyclerView rvNotificaciones;
    private LinearLayout layoutEmpty;
    private TextView tvContador;

    private FirebaseFirestore db;
    private NotificacionAdapter adapter;
    private List<Notificacion> listaNotificaciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_notificaciones_cliente);

        db = FirebaseFirestore.getInstance();

        inicializarVistas();
        cargarNotificaciones();
        configurarBottomNav();
    }

    private void inicializarVistas() {
        FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        tvContador = findViewById(R.id.tvSubGreeting);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        rvNotificaciones = findViewById(R.id.rvNotificaciones);
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificacionAdapter(listaNotificaciones, this::marcarComoLeida);
        rvNotificaciones.setAdapter(adapter);
    }

    private void cargarNotificaciones() {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid == null) return;

        db.collection("notificaciones")
                .whereEqualTo("usuarioId", uid)
                .orderBy("fechaCreacion", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) return;

                    listaNotificaciones.clear();
                    int noLeidas = 0;

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Notificacion n = new Notificacion();
                        n.setId(doc.getId());
                        n.setTipo(doc.getString("tipo"));
                        n.setTitulo(doc.getString("titulo"));
                        n.setTexto(doc.getString("texto"));
                        n.setLeido(Boolean.TRUE.equals(doc.getBoolean("leido")));
                        n.setFechaCreacion(doc.getTimestamp("fechaCreacion"));
                        n.setReferenciaId(doc.getString("referenciaId"));

                        listaNotificaciones.add(n);
                        if (!n.isLeido()) noLeidas++;
                    }

                    adapter.notifyDataSetChanged();

                    // Subtítulo dinámico
                    if (tvContador != null) {
                        tvContador.setText(noLeidas > 0
                                ? noLeidas + " sin leer"
                                : "Todo al día");
                    }


                    if (layoutEmpty != null) {
                        layoutEmpty.setVisibility(
                                listaNotificaciones.isEmpty() ? View.VISIBLE : View.GONE);
                        rvNotificaciones.setVisibility(
                                listaNotificaciones.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                });
    }

    private void marcarComoLeida(Notificacion notif) {
        if (notif.isLeido() || notif.getId() == null) return;
        db.collection("notificaciones")
                .document(notif.getId())
                .update("leido", true);
    }

    private void configurarBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        if (bottomNav == null) return;

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
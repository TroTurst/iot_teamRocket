package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AsesorChatActivity extends AppCompatActivity implements ChatThreadAdapter.Listener {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private FrameLayout framePerfil;
    private RecyclerView recyclerChatThreads;
    private EditText etSearchChat;
    private ChatThreadAdapter adapter;

    private int totalNotificaciones = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_chat);

        bottomNav = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        framePerfil = findViewById(R.id.framePerfil);
        recyclerChatThreads = findViewById(R.id.recyclerChatThreads);
        etSearchChat = findViewById(R.id.etSearchChat);

        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        bottomNav.setSelectedItemId(R.id.nav_chat);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, AsesorHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
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

        adapter = new ChatThreadAdapter(buildMockThreads(), this);
        recyclerChatThreads.setLayoutManager(new LinearLayoutManager(this));
        recyclerChatThreads.setAdapter(adapter);

        etSearchChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filterByName(s == null ? "" : s.toString());
            }
        });
    }

    private List<ChatThread> buildMockThreads() {
        List<ChatThread> threads = new ArrayList<>();
        threads.add(new ChatThread(
            "chat_1",
            "Maria R.",
            "Hola, quiero info del proyecto Catalina Sky",
            "10:02",
            R.drawable.ic_perfil
        ));
        threads.add(new ChatThread(
            "chat_2",
            "Carlos M.",
            "Gracias, tambien quiero agendar visita",
            "10:04",
            R.drawable.ic_perfil
        ));
        threads.add(new ChatThread(
            "chat_3",
            "Luisa T.",
            "Me puedes enviar el brochure del proyecto",
            "09:45",
            R.drawable.ic_perfil
        ));
        threads.add(new ChatThread(
            "chat_4",
            "Javier P.",
            "Estoy interesado en separar un departamento",
            "Ayer",
            R.drawable.ic_perfil
        ));
        threads.add(new ChatThread(
            "chat_5",
            "Ana L.",
            "Podemos ver opciones de financiamiento",
            "Ayer",
            R.drawable.ic_perfil
        ));
        return threads;
    }

    @Override
    public void onChatSelected(ChatThread thread) {
        Intent intent = new Intent(this, AsesorChatDetailActivity.class);
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_ID, thread.getId());
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_NAME, thread.getName());
        startActivity(intent);
    }

    @Override
    public void onChatDeleted(ChatThread thread) {
        Toast.makeText(this, "Chat eliminado", Toast.LENGTH_SHORT).show();
    }

    private void configurarBadge() {
        if (totalNotificaciones > 0) {
            tvBadgeNotif.setText(String.valueOf(totalNotificaciones));
            tvBadgeNotif.setVisibility(View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(View.GONE);
        }
    }

    private void limpiarBadge() {
        totalNotificaciones = 0;
        tvBadgeNotif.setVisibility(View.GONE);
    }
}

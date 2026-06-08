package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.button.MaterialButton;

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

        AsesorNotificacionStore.seedIfEmpty(this);
        AsesorChatStore.seedIfEmpty(this);
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

        adapter = new ChatThreadAdapter(AsesorChatStore.getThreads(this), this);
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

    @Override
    public void onChatSelected(ChatThread thread) {
        Intent intent = new Intent(this, AsesorChatDetailActivity.class);
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_ID, thread.getId());
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_NAME, thread.getName());
        startActivity(intent);
    }

    @Override
    public void onChatDeleteRequested(ChatThread thread) {
        mostrarDialogoEliminarChat(thread);
    }

    private void mostrarDialogoEliminarChat(ChatThread thread) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_eliminar_chat, null);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvDialogMessage = dialogView.findViewById(R.id.tvDialogMessage);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelarDialogo);
        MaterialButton btnEliminar = dialogView.findViewById(R.id.btnEliminarDialogo);

        tvDialogTitle.setText("Eliminar chat");
        tvDialogMessage.setText("Seguro que quieres eliminar la conversación con " + thread.getName() + "?");

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        btnEliminar.setOnClickListener(v -> {
            AsesorChatStore.deleteThread(this, thread.getId());
            adapter.deleteThreadById(thread.getId());
            AsesorNotificacionHelper.enviar(
                this,
                "Chat eliminado",
                "Se elimino la conversacion con " + thread.getName(),
                AsesorNotificacionStore.TIPO_CHAT_ELIMINADO,
                AsesorNotificacionStore.TARGET_CHAT_DETAIL,
                thread.getId()
            );
            configurarBadge();
            Toast.makeText(this, "Chat eliminado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void configurarBadge() {
        int totalNotificaciones = AsesorNotificacionStore.getBadgeCount(this);
        if (totalNotificaciones > 0) {
            tvBadgeNotif.setText(String.valueOf(totalNotificaciones));
            tvBadgeNotif.setVisibility(View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(View.GONE);
        }
    }

    private void limpiarBadge() {
        AsesorNotificacionStore.clearBadge(this);
        tvBadgeNotif.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.updateThreads(AsesorChatStore.getThreads(this));
        }
        configurarBadge();
    }
}

package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.asesor.ChatThread;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClienteMensajesActivity extends AppCompatActivity {

    private RecyclerView rvMensajes;
    private ChatAdapter adapter;


    private List<ChatThread> listaChatsCompletos = new ArrayList<>();
    private List<ChatThread> listaChatsFiltrados = new ArrayList<>();

    private EditText etSearchChat;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_mensajes_cliente);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvMensajes = findViewById(R.id.rvMensajes);
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatAdapter(listaChatsFiltrados);
        rvMensajes.setAdapter(adapter);

        etSearchChat = findViewById(R.id.etSearchChat);

        configurarNavegacion();
        configurarBuscador();
        cargarBandejaDeChats();
    }


    private void configurarBuscador() {
        if (etSearchChat != null) {
            etSearchChat.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filtrarLista(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });
        }
    }

    private void filtrarLista(String textoBusqueda) {
        listaChatsFiltrados.clear();

        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            listaChatsFiltrados.addAll(listaChatsCompletos);
        } else {
            String filtro = textoBusqueda.toLowerCase().trim();
            for (ChatThread chat : listaChatsCompletos) {
                if (chat.getName() != null && chat.getName().toLowerCase().contains(filtro)) {
                    listaChatsFiltrados.add(chat);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }


    private void cargarBandejaDeChats() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String miId = currentUser.getUid();

        db.collection("chats")
                .whereEqualTo("clienteId", miId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error cargando chats", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    listaChatsCompletos.clear();

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            String asesorNombre = doc.getString("asesorNombre");
                            String ultimoMsj = doc.getString("ultimoMensaje");
                            String fotoUrl = doc.getString("fotoAsesorUrl");

                            String horaFormateada = "";
                            if (doc.getTimestamp("timestamp") != null) {
                                Date date = doc.getTimestamp("timestamp").toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                horaFormateada = sdf.format(date);
                            }

                            asesorNombre = asesorNombre != null ? asesorNombre : "Asesor Inmia";
                            ultimoMsj = ultimoMsj != null ? ultimoMsj : "Archivo adjunto";

                            listaChatsCompletos.add(new ChatThread(
                                    doc.getId(),
                                    asesorNombre,
                                    ultimoMsj,
                                    horaFormateada,
                                    R.drawable.ic_perfil,
                                    fotoUrl
                            ));
                        }
                    }

                    String textoActual = etSearchChat != null ? etSearchChat.getText().toString() : "";
                    filtrarLista(textoActual);
                });
    }


    private void configurarNavegacion() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        bottomNav.setSelectedItemId(R.id.nav_chat);

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
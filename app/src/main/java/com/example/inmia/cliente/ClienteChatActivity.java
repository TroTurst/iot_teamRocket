package com.example.inmia.cliente;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Mensaje;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClienteChatActivity extends AppCompatActivity {

    private RecyclerView rvMensajesDetalle;
    private MensajeAdapter adapter;
    private List<Mensaje> historialChat = new ArrayList<>();

    private EditText etMessageInput;
    private FloatingActionButton btnSend;
    private TextView tvGreeting;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String miId;
    private String chatId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_chat_cliente);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            miId = mAuth.getCurrentUser().getUid();
        }

        inicializarVistas();
        recibirDatosDelChat();
        cargarMensajesEnTiempoReal();
        configurarBotonEnviar();
    }

    private void inicializarVistas() {
        FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        tvGreeting = findViewById(R.id.tvGreeting);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSend = findViewById(R.id.btnSend);

        rvMensajesDetalle = findViewById(R.id.rvMensajesDetalle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMensajesDetalle.setLayoutManager(layoutManager);

        adapter = new MensajeAdapter(historialChat);
        rvMensajesDetalle.setAdapter(adapter);
    }

    private void recibirDatosDelChat() {
        if (getIntent() != null) {
            chatId = getIntent().getStringExtra("CHAT_ID");
            String asesorNombre = getIntent().getStringExtra("ASESOR_NOMBRE");

            if (asesorNombre != null) {
                tvGreeting.setText(asesorNombre);
            }
        }

        if (chatId == null || chatId.isEmpty()) {
            Toast.makeText(this, "Error: Chat no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarMensajesEnTiempoReal() {
        if (chatId == null || chatId.isEmpty()) return;

        db.collection("chats").document(chatId).collection("mensajes")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    historialChat.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            String texto = doc.getString("texto");
                            String emisorId = doc.getString("emisorId");


                            String horaFormateada = "";
                            if (doc.getTimestamp("timestamp") != null) {
                                Date date = doc.getTimestamp("timestamp").toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                horaFormateada = sdf.format(date);
                            }

                            boolean esMio = (emisorId != null && emisorId.equals(miId));

                            if (texto != null) {
                                historialChat.add(new Mensaje(texto, horaFormateada, esMio));
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (!historialChat.isEmpty()) {
                        rvMensajesDetalle.scrollToPosition(historialChat.size() - 1);
                    }
                });
    }

    private void configurarBotonEnviar() {
        btnSend.setOnClickListener(v -> {
            String texto = etMessageInput.getText().toString().trim();
            if (texto.isEmpty() || chatId == null || chatId.isEmpty()) return;

            etMessageInput.setText("");

            Map<String, Object> nuevoMensaje = new HashMap<>();
            nuevoMensaje.put("texto", texto);
            nuevoMensaje.put("emisorId", miId);
            nuevoMensaje.put("timestamp", new Date());

            db.collection("chats").document(chatId).collection("mensajes")
                    .add(nuevoMensaje)
                    .addOnSuccessListener(documentReference -> {

                        Map<String, Object> actualizacionChat = new HashMap<>();
                        actualizacionChat.put("ultimoMensaje", texto);
                        actualizacionChat.put("timestamp", new Date());

                        db.collection("chats").document(chatId).update(actualizacionChat);

                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show();
                        etMessageInput.setText(texto);
                    });
        });
    }
}
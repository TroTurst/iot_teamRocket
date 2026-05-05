package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

import java.util.ArrayList;
import java.util.List;

public class AsesorChatDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CHAT_NAME = "extra_chat_name";
    public static final String EXTRA_CHAT_ID = "extra_chat_id";

    private TextView tvChatName;
    private TextView tvChatStatus;
    private View btnBackChat;
    private View btnChatMenu;
    private RecyclerView recyclerChatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_chat_detail);

        tvChatName = findViewById(R.id.tvChatName);
        tvChatStatus = findViewById(R.id.tvChatStatus);
        btnBackChat = findViewById(R.id.btnBackChat);
        btnChatMenu = findViewById(R.id.btnChatMenu);
        recyclerChatMessages = findViewById(R.id.recyclerChatMessages);

        String chatName = getIntent().getStringExtra(EXTRA_CHAT_NAME);
        if (chatName != null && !chatName.trim().isEmpty()) {
            tvChatName.setText(chatName);
        }

        if (tvChatStatus != null) {
            tvChatStatus.setText("Online");
        }

        if (btnBackChat != null) {
            btnBackChat.setOnClickListener(v -> finish());
        }

        if (btnChatMenu != null) {
            btnChatMenu.setOnClickListener(v ->
                Toast.makeText(this, "Opciones", Toast.LENGTH_SHORT).show()
            );
        }

        ChatMessageAdapter adapter = new ChatMessageAdapter(buildMockMessages());
        recyclerChatMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerChatMessages.setAdapter(adapter);
    }

    private List<ChatMessage> buildMockMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(
            "Hola, quiero info del proyecto Catalina Sky",
            "10:02",
            false
        ));
        messages.add(new ChatMessage(
            "Hola, claro. Te envio las opciones disponibles",
            "10:03",
            true
        ));
        messages.add(new ChatMessage(
            "Gracias, tambien quiero agendar visita",
            "10:04",
            false
        ));
        return messages;
    }
}

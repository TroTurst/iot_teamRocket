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
    private View btnSend;
    private android.widget.EditText etMessage;
    private RecyclerView recyclerChatMessages;
    private ChatMessageAdapter adapter;
    private String chatId;
    private String chatName;

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
        btnSend = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);
        recyclerChatMessages = findViewById(R.id.recyclerChatMessages);

        AsesorChatStore.seedIfEmpty(this);

        chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        chatName = getIntent().getStringExtra(EXTRA_CHAT_NAME);
        if (chatId == null || chatId.trim().isEmpty()) {
            List<ChatThread> threads = AsesorChatStore.getThreads(this);
            chatId = threads.isEmpty() ? "chat_1" : threads.get(0).getId();
        }

        ChatThread thread = AsesorChatStore.getThreadById(this, chatId);
        if (thread != null) {
            chatName = thread.getName();
        }
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

        adapter = new ChatMessageAdapter(new ArrayList<>());
        recyclerChatMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerChatMessages.setAdapter(adapter);
        refrescarMensajes();

        if (btnSend != null) {
            btnSend.setOnClickListener(v -> enviarMensaje());
        }
    }

    private void refrescarMensajes() {
        if (adapter == null) {
            return;
        }
        adapter.updateMessages(AsesorChatStore.getMessages(this, chatId));
        recyclerChatMessages.scrollToPosition(Math.max(0, adapter.getItemCount() - 1));
    }

    private void enviarMensaje() {
        if (etMessage == null) {
            return;
        }
        String texto = etMessage.getText() == null ? "" : etMessage.getText().toString().trim();
        if (texto.isEmpty()) {
            return;
        }

        AsesorChatStore.sendMessage(this, chatId, texto);
        etMessage.setText("");
        refrescarMensajes();
        Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
    }
}

package com.example.inmia.asesor;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class AsesorChatDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CHAT_NAME = "extra_chat_name";
    public static final String EXTRA_CHAT_ID   = "extra_chat_id";

    private TextView tvChatName;
    private TextView tvChatStatus;
    private View btnBackChat;
    private View btnSend;
    private EditText etMessage;
    private RecyclerView recyclerChatMessages;
    private ChatMessageAdapter adapter;
    private String chatId;
    private ListenerRegistration messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_asesor_chat_detail);

        tvChatName          = findViewById(R.id.tvChatName);
        tvChatStatus        = findViewById(R.id.tvChatStatus);
        btnBackChat         = findViewById(R.id.btnBackChat);
        btnSend             = findViewById(R.id.btnSend);
        etMessage           = findViewById(R.id.etMessage);
        recyclerChatMessages= findViewById(R.id.recyclerChatMessages);

        chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        String chatName = getIntent().getStringExtra(EXTRA_CHAT_NAME);

        if (chatName != null && !chatName.trim().isEmpty() && tvChatName != null) {
            tvChatName.setText(chatName);
        }
        if (tvChatStatus != null) tvChatStatus.setText("Online");
        if (btnBackChat  != null) btnBackChat.setOnClickListener(v -> finish());

        adapter = new ChatMessageAdapter(new ArrayList<>());
        recyclerChatMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerChatMessages.setAdapter(adapter);

        if (btnSend != null) btnSend.setOnClickListener(v -> enviarMensaje());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (chatId != null && !chatId.isEmpty()) {
            messagesListener = AsesorFirestoreRepository.get().listenMessages(chatId, messages -> {
                if (adapter != null) {
                    adapter.updateMessages(messages);
                    recyclerChatMessages.scrollToPosition(Math.max(0, adapter.getItemCount() - 1));
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (messagesListener != null) {
            messagesListener.remove();
            messagesListener = null;
        }
    }

    private void enviarMensaje() {
        if (etMessage == null) return;
        String texto = etMessage.getText() == null ? "" : etMessage.getText().toString().trim();
        if (texto.isEmpty() || chatId == null || chatId.isEmpty()) return;
        AsesorFirestoreRepository.get().sendMessage(chatId, texto);
        etMessage.setText("");
    }
}

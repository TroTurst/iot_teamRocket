package com.example.inmia.asesor;

import android.content.Intent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private View btnAttach;
    private ImageView btnFavorite;
    private EditText etMessage;
    private RecyclerView recyclerChatMessages;
    private ChatMessageAdapter adapter;
    private String chatId;
    private String chatName = "Cliente";
    private String clientPhone = "";
    private boolean favorite;
    private ActivityResultLauncher<String[]> attachmentPicker;
    private ListenerRegistration messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_asesor_chat_detail);

        attachmentPicker = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), this::onAttachmentSelected);

        tvChatName          = findViewById(R.id.tvChatName);
        tvChatStatus        = findViewById(R.id.tvChatStatus);
        btnBackChat         = findViewById(R.id.btnBackChat);
        btnSend             = findViewById(R.id.btnSend);
        btnAttach           = findViewById(R.id.btnAttach);
        btnFavorite         = findViewById(R.id.btnFavorite);
        etMessage           = findViewById(R.id.etMessage);
        recyclerChatMessages= findViewById(R.id.recyclerChatMessages);

        chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        String receivedName = getIntent().getStringExtra(EXTRA_CHAT_NAME);
        if (receivedName != null && !receivedName.trim().isEmpty()) chatName = receivedName.trim();

        if (tvChatName != null) {
            tvChatName.setText(chatName);
        }
        if (tvChatStatus != null) tvChatStatus.setText("Online");
        if (btnBackChat  != null) btnBackChat.setOnClickListener(v -> finish());

        adapter = new ChatMessageAdapter(new ArrayList<>());
        recyclerChatMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerChatMessages.setAdapter(adapter);

        if (btnSend != null) btnSend.setOnClickListener(v -> enviarMensaje());
        findViewById(R.id.btnCall).setOnClickListener(v -> callClient());
        findViewById(R.id.btnChatMenu).setOnClickListener(this::showChatMenu);
        btnFavorite.setOnClickListener(v -> toggleFavorite());
        btnAttach.setOnClickListener(v -> attachmentPicker.launch(new String[]{"image/*", "application/pdf"}));

        prepareChat();
    }

    private void prepareChat() {
        boolean needsCreation = chatId == null || chatId.trim().isEmpty();
        btnSend.setEnabled(!needsCreation);
        btnAttach.setEnabled(!needsCreation);
        btnFavorite.setEnabled(!needsCreation);
        if (needsCreation) tvChatStatus.setText("Preparando conversación…");

        AsesorFirestoreRepository.get().ensureChat(chatId, chatName, (success, readyChatId, message) -> {
            if (!success) {
                tvChatStatus.setText("No se pudo iniciar el chat");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                return;
            }
            chatId = readyChatId;
            btnSend.setEnabled(true);
            btnAttach.setEnabled(true);
            btnFavorite.setEnabled(true);
            tvChatStatus.setText("Online");
            startMessagesListener();
            loadChatDetails();
        });
    }

    private void loadChatDetails() {
        AsesorFirestoreRepository.get().getChatDetails(chatId, (phone, isFavorite) -> {
            clientPhone = phone;
            favorite = isFavorite;
            renderFavorite();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startMessagesListener();
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
        if (texto.isEmpty()) return;
        if (chatId == null || chatId.isEmpty()) {
            Toast.makeText(this, "La conversación todavía no está lista", Toast.LENGTH_SHORT).show();
            return;
        }
        btnSend.setEnabled(false);
        AsesorFirestoreRepository.get().sendMessage(chatId, texto, (success, message) -> {
            btnSend.setEnabled(true);
            if (success) {
                etMessage.setText("");
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startMessagesListener() {
        if (messagesListener != null || chatId == null || chatId.isEmpty()) return;
        messagesListener = AsesorFirestoreRepository.get().listenMessages(chatId, messages -> {
            if (adapter != null) {
                adapter.updateMessages(messages);
                recyclerChatMessages.scrollToPosition(Math.max(0, adapter.getItemCount() - 1));
            }
        });
    }

    private void callClient() {
        if (clientPhone == null || clientPhone.trim().isEmpty()) {
            Toast.makeText(this, "Este cliente no tiene un teléfono registrado", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(clientPhone.trim()))));
    }

    private void toggleFavorite() {
        boolean newValue = !favorite;
        btnFavorite.setEnabled(false);
        AsesorFirestoreRepository.get().setChatFavorite(chatId, newValue, (success, message) -> {
            btnFavorite.setEnabled(true);
            if (success) {
                favorite = newValue;
                renderFavorite();
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void renderFavorite() {
        int color = favorite ? R.color.inmia_warning : R.color.inmia_neutral;
        btnFavorite.setColorFilter(ContextCompat.getColor(this, color));
        btnFavorite.setContentDescription(favorite ? "Quitar de favoritos" : "Marcar como favorito");
    }

    private void showChatMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.inflate(R.menu.menu_chat_detail);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_contact_info) {
                String phone = clientPhone == null || clientPhone.trim().isEmpty()
                    ? "No registrado" : clientPhone;
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                    .setTitle(chatName)
                    .setMessage("Teléfono: " + phone)
                    .setPositiveButton("Cerrar", null)
                    .show();
                return true;
            }
            if (item.getItemId() == R.id.action_copy_phone) {
                if (clientPhone == null || clientPhone.trim().isEmpty()) {
                    Toast.makeText(this, "Este cliente no tiene teléfono registrado",
                        Toast.LENGTH_SHORT).show();
                    return true;
                }
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("Teléfono de " + chatName, clientPhone));
                Toast.makeText(this, "Teléfono copiado", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void onAttachmentSelected(Uri uri) {
        if (uri == null) return;
        String fileName = getFileName(uri);
        String mimeType = getContentResolver().getType(uri);
        btnAttach.setEnabled(false);
        Toast.makeText(this, "Subiendo " + fileName + "…", Toast.LENGTH_SHORT).show();
        AsesorFirestoreRepository.get().sendAttachment(chatId, uri, fileName, mimeType,
            (success, message) -> {
                btnAttach.setEnabled(true);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            });
    }

    private String getFileName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) return cursor.getString(index);
            }
        }
        return "archivo";
    }
}

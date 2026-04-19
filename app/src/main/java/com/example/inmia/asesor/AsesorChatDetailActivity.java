package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AsesorChatDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CHAT_NAME = "extra_chat_name";
    public static final String EXTRA_CHAT_ID = "extra_chat_id";

    private BottomNavigationView bottomNav;
    private TextView tvChatName;
    private TextView tvChatStatus;
    private View btnBackChat;
    private View btnChatMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_chat_detail);

        bottomNav = findViewById(R.id.bottomNavAsesor);
        tvChatName = findViewById(R.id.tvChatName);
        tvChatStatus = findViewById(R.id.tvChatStatus);
        btnBackChat = findViewById(R.id.btnBackChat);
        btnChatMenu = findViewById(R.id.btnChatMenu);

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

        bottomNav.setSelectedItemId(R.id.nav_chat);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, AsesorHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, AsesorChatActivity.class));
                finish();
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
    }

    
}

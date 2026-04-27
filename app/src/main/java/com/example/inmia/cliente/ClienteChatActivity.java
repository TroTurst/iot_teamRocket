package com.example.inmia.cliente;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inmia.R;
import java.util.ArrayList;
import java.util.List;

public class ClienteChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_chat_cliente );
        android.widget.FrameLayout btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        RecyclerView rvMensajesDetalle = findViewById(R.id.rvMensajesDetalle);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMensajesDetalle.setLayoutManager(layoutManager);

        List<Mensaje> historialChat = new ArrayList<>();
        historialChat.add(new Mensaje("Hola.", "12:00 PM", true));
        historialChat.add(new Mensaje("¡Hola?", "12:03 PM", false));
        historialChat.add(new Mensaje("XD", "12:05 PM", true));

        MensajeAdapter adapter = new MensajeAdapter(historialChat);
        rvMensajesDetalle.setAdapter(adapter);
    }
}
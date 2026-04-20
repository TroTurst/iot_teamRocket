package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;

public class ClientePagoSeparacionActivity extends AppCompatActivity {
    private MaterialButton btnPagar;
    private FrameLayout btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_pago_separacion_cliente);

        btnPagar = findViewById(R.id.btnPagar);
        btnBack = findViewById(R.id.btnBack);

        if (btnPagar != null) {
            btnPagar.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClientePagoExitosoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            });
        }
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
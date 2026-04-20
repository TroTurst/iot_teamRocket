package com.example.inmia.superadmin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;

public class PerfilUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_perfil_user);

        // Botón atrás
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
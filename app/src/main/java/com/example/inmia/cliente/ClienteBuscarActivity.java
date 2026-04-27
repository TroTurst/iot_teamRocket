package com.example.inmia.cliente;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

public class ClienteBuscarActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etSearchReal;
    private ImageButton btnClear;
    private RecyclerView rvSugerencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_cliente_buscar);

        inicializarVistas();
        configurarBuscador();
    }

    private void inicializarVistas() {
        btnBack = findViewById(R.id.btnBack);
        etSearchReal = findViewById(R.id.etSearchReal);
        btnClear = findViewById(R.id.btnClear);
        rvSugerencias = findViewById(R.id.rvSugerencias);

        rvSugerencias.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configurarBuscador() {
        btnBack.setOnClickListener(v -> finish());

        etSearchReal.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etSearchReal, InputMethodManager.SHOW_IMPLICIT);
        }

        btnClear.setOnClickListener(v -> etSearchReal.setText(""));

        etSearchReal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    btnClear.setVisibility(View.VISIBLE);


                } else {
                    btnClear.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
package com.example.inmia.cliente;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ClienteCambiarPasswordActivity extends AppCompatActivity {

    private EditText etPasswordActual, etPasswordNueva, etPasswordConfirmar;
    private MaterialButton btnActualizarPassword;
    private FrameLayout btnBack;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_cambiar_password_cliente);

        mAuth = FirebaseAuth.getInstance();

        inicializarVistas();
        configurarListeners();
    }

    private void inicializarVistas() {
        etPasswordActual = findViewById(R.id.etPasswordActual);
        etPasswordNueva = findViewById(R.id.etPasswordNueva);
        etPasswordConfirmar = findViewById(R.id.etPasswordConfirmar);
        btnActualizarPassword = findViewById(R.id.btnActualizarPassword);
        btnBack = findViewById(R.id.btnBack);
    }

    private void configurarListeners() {
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        btnActualizarPassword.setOnClickListener(v -> procesarCambioDePassword());
    }

    private void procesarCambioDePassword() {
        String passActual = etPasswordActual.getText().toString().trim();
        String passNueva = etPasswordNueva.getText().toString().trim();
        String passConfirmar = etPasswordConfirmar.getText().toString().trim();

        // 1. Validaciones locales
        if (passActual.isEmpty() || passNueva.isEmpty() || passConfirmar.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passNueva.length() < 6) {
            etPasswordNueva.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!passNueva.equals(passConfirmar)) {
            etPasswordConfirmar.setError("Las nuevas contraseñas no coinciden");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {

            btnActualizarPassword.setEnabled(false);
            btnActualizarPassword.setText("Validando credenciales...");

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), passActual);

            user.reauthenticate(credential).addOnSuccessListener(aVoid -> {

                btnActualizarPassword.setText("Guardando nueva contraseña...");

                user.updatePassword(passNueva).addOnSuccessListener(aVoid1 -> {
                    Toast.makeText(this, "¡Contraseña actualizada con éxito!", Toast.LENGTH_LONG).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    restaurarBoton();
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(this, "La contraseña actual es incorrecta", Toast.LENGTH_LONG).show();
                etPasswordActual.setError("Contraseña incorrecta");
                restaurarBoton();
            });
        }
    }

    private void restaurarBoton() {
        btnActualizarPassword.setEnabled(true);
        btnActualizarPassword.setText("Actualizar Contraseña");
    }
}
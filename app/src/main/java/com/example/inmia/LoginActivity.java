package com.example.inmia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.admin.AdminHomeActivity;
import com.example.inmia.admin.RegistroInmobiliariaActivity;
import com.example.inmia.asesor.AsesorHomeActivity;
import com.example.inmia.cliente.ClienteHomeActivity;
import com.example.inmia.superadmin.SuperAdminHomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnRegister;
    private android.widget.TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);

        // Vincular vistas
        tilEmail         = findViewById(R.id.tilEmail);
        tilPassword      = findViewById(R.id.tilPassword);
        etEmail          = findViewById(R.id.etEmail);
        etPassword       = findViewById(R.id.etPassword);
        btnLogin         = findViewById(R.id.btnLogin);
        btnRegister      = findViewById(R.id.btnRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Botón Iniciar Sesión
        btnLogin.setOnClickListener(v -> {
            if (validarFormulario()) {
                String email    = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Verificar credenciales con UserCheck
                String rol = UserCheck.getRol(email, password);

                if (rol == null) {
                    // Credenciales incorrectas
                    tilPassword.setError(getString(R.string.error_credenciales));
                } else {
                    // Credenciales correctas → redirigir según rol
                    tilPassword.setError(null);
                    redirigirSegunRol(rol);
                }
            }
        });

        // Botón Crear Cuenta
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Olvidé mi contraseña
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void redirigirSegunRol(String rol) {
        Intent intent;

        switch (rol) {
            case UserCheck.ROL_CLIENTE:
                intent = new Intent(this, ClienteHomeActivity.class);
                break;
            case UserCheck.ROL_ASESOR:
                intent = new Intent(this, AsesorHomeActivity.class);
                break;
            case UserCheck.ROL_ADMIN:
                intent = new Intent(this, RegistroInmobiliariaActivity.class);
                break;
            case UserCheck.ROL_SUPERADMIN:
                intent = new Intent(this, SuperAdminHomeActivity.class);
                break;
            default:
                return;
        }

        // Limpiar el stack — no puede volver al login con el botón atrás
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean validarFormulario() {
        boolean valido = true;

        String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Validar email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_email_empty));
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email_invalid));
            valido = false;
        } else {
            tilEmail.setError(null);
        }

        // Validar contraseña
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_password_empty));
            valido = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_short));
            valido = false;
        } else {
            tilPassword.setError(null);
        }

        return valido;
    }
}
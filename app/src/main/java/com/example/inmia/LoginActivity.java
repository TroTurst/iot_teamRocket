package com.example.inmia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.util.RolRouter;
import com.example.inmia.util.SesionLocal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnRegister;
    private android.widget.TextView tvForgotPassword;

    // Variables de Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


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

                btnLogin.setEnabled(false);

                // Autenticar con Firebase Auth
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    verificarRolYRedirigir(user.getUid(), email);
                                }
                            } else {
                                btnLogin.setEnabled(true);
                                tilPassword.setError(getString(R.string.error_credenciales));
                            }
                        });
            }
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void verificarRolYRedirigir(String uid, String email) {


        db.collection("usuarios").document(uid).get()

                .addOnSuccessListener(documentSnapshot -> {
                    btnLogin.setEnabled(true);

                    if (documentSnapshot.exists()) {
                        Boolean activoObj = documentSnapshot.getBoolean("activo");
                        boolean activo = activoObj != null ? activoObj : false;

                        if (!activo) {
                            Toast.makeText(this, "Tu cuenta está desactivada o pendiente de aprobación.", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            return;
                        }

                        Intent intent = RolRouter.resolverIntentDestino(this, documentSnapshot, email);
                        if (intent == null) {
                            Toast.makeText(this, "Rol desconocido en la base de datos.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            return;
                        }

                        SesionLocal.marcarActiva(this);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(this, "No se encontró el perfil en la base de datos.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    Log.e("FirestoreError", "Error al leer documento de usuario", e);
                    Toast.makeText(this, "Error de conexión con la base de datos.", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                });
    }

    private boolean validarFormulario() {
        boolean valido = true;

        String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_email_empty));
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_email_invalid));
            valido = false;
        } else {
            tilEmail.setError(null);
        }

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
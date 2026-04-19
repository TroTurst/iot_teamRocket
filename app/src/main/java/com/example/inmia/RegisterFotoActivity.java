package com.example.inmia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterFotoActivity extends AppCompatActivity {

    private ImageView imgFoto;
    private TextInputLayout tilPassword, tilConfirmPassword;
    private TextInputEditText etPassword, etConfirmPassword;
    private MaterialButton btnCamara, btnGaleria, btnCrearCuenta;

    private Uri fotoUri = null;

    // Launcher para galería
    private final ActivityResultLauncher<Intent> launcherGaleria =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {
                            fotoUri = result.getData().getData();
                            imgFoto.setImageURI(fotoUri);
                            imgFoto.setPadding(0, 0, 0, 0);
                        }
                    });

    // Launcher para cámara
    private final ActivityResultLauncher<Intent> launcherCamara =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {
                            fotoUri = result.getData().getData();
                            imgFoto.setImageURI(fotoUri);
                            imgFoto.setPadding(0, 0, 0, 0);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_register_foto);

        // Vincular vistas
        imgFoto           = findViewById(R.id.imgFoto);
        tilPassword       = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCamara         = findViewById(R.id.btnCamara);
        btnGaleria        = findViewById(R.id.btnGaleria);
        btnCrearCuenta    = findViewById(R.id.btnCrearCuenta);

        // Botón atrás — regresa al paso 1
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Toque en el círculo de foto → galería
        findViewById(R.id.frameFoto).setOnClickListener(v -> abrirGaleria());

        // Botón cámara
        btnCamara.setOnClickListener(v -> abrirCamara());

        // Botón galería
        btnGaleria.setOnClickListener(v -> abrirGaleria());

        // Botón crear cuenta
        btnCrearCuenta.setOnClickListener(v -> {
            if (validarFormulario()) {
                crearCuenta();
            }
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcherGaleria.launch(intent);
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launcherCamara.launch(intent);
    }

    private boolean validarFormulario() {
        boolean valido = true;

        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String password   = getText(etPassword);
        String confirmPwd = getText(etConfirmPassword);

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Este campo es obligatorio");
            valido = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres");
            valido = false;
        }

        if (TextUtils.isEmpty(confirmPwd)) {
            tilConfirmPassword.setError("Este campo es obligatorio");
            valido = false;
        } else if (!password.equals(confirmPwd)) {
            tilConfirmPassword.setError("Las contraseñas no coinciden");
            valido = false;
        }

        return valido;
    }

    private void crearCuenta() {
        // TODO: guardar todos los datos en Firebase
        // Los datos del paso 1 están en getIntent().getStringExtra("nombres") etc.

        Toast.makeText(this,
                "¡Cuenta creada exitosamente!",
                Toast.LENGTH_LONG).show();

        // Ir al Login y limpiar el back stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
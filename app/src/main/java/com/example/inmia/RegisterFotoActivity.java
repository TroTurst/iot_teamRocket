package com.example.inmia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.inmia.util.LogHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegisterFotoActivity extends AppCompatActivity {

    private ImageView imgFoto;
    private TextInputLayout tilPassword, tilConfirmPassword;
    private TextInputEditText etPassword, etConfirmPassword;
    private MaterialButton btnCamara, btnGaleria, btnCrearCuenta;
    private Uri fotoUri = null;

    // vaariables de firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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
        btnCrearCuenta.setEnabled(false);

        Intent intent = getIntent();
        String correo = intent.getStringExtra("correo");
        String password = getText(etPassword);

        if (correo == null || correo.isEmpty()) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            btnCrearCuenta.setEnabled(true);
            return;
        }

        // 1. Crear el usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Usuario creado exitosamente en Auth
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            subirFotoYGuardar(user.getUid(), intent);
                        }
                    } else {
                        btnCrearCuenta.setEnabled(true);
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        Toast.makeText(this, "Error de registro: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Sube la foto de perfil al Storage (si el usuario eligió una) y luego guarda
     * los datos. La foto es OPCIONAL: si no se eligió, o si la subida falla, la
     * cuenta se crea igual con fotoUrl vacío (no bloquea el registro).
     */
    private void subirFotoYGuardar(String uid, Intent intent) {
        if (fotoUri == null) {
            guardarDatosEnFirestore(uid, intent, "");
            return;
        }

        StorageReference ref = FirebaseStorage.getInstance()
                .getReference()
                .child("fotos_perfil/" + uid + ".jpg");

        ref.putFile(fotoUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener(url -> guardarDatosEnFirestore(uid, intent, url.toString()))
                        .addOnFailureListener(e -> guardarDatosEnFirestore(uid, intent, "")))
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "No se pudo subir la foto; la cuenta se creó sin foto de perfil.",
                            Toast.LENGTH_LONG).show();
                    guardarDatosEnFirestore(uid, intent, "");
                });
    }

    private void guardarDatosEnFirestore(String uid, Intent intent, String fotoUrl) {
        Map<String, Object> nuevoCliente = new HashMap<>();
        nuevoCliente.put("uid", uid);
        nuevoCliente.put("nombres", intent.getStringExtra("nombres"));
        nuevoCliente.put("apellidos", intent.getStringExtra("apellidos"));
        nuevoCliente.put("tipoDocumento", intent.getStringExtra("tipoDoc"));
        nuevoCliente.put("numeroDocumento", intent.getStringExtra("numDoc"));
        nuevoCliente.put("fechaNacimiento", intent.getStringExtra("fechaNac"));
        nuevoCliente.put("correo", intent.getStringExtra("correo"));
        nuevoCliente.put("telefono", intent.getStringExtra("telefono"));
        nuevoCliente.put("domicilio", intent.getStringExtra("domicilio"));

        nuevoCliente.put("fotoUrl", fotoUrl != null ? fotoUrl : "");

        nuevoCliente.put("rol", "cliente");
        nuevoCliente.put("activo", true);
        nuevoCliente.put("tarjetaRegistrada", false);
        nuevoCliente.put("fechaCreacion", FieldValue.serverTimestamp());

        // 3. Guardar en la colección "usuarios" de Firestore
        db.collection("usuarios").document(uid)
                .set(nuevoCliente)
                .addOnSuccessListener(aVoid -> {
                    String nombreCompleto = ((intent.getStringExtra("nombres") != null
                            ? intent.getStringExtra("nombres") : "")
                            + " " + (intent.getStringExtra("apellidos") != null
                            ? intent.getStringExtra("apellidos") : "")).trim();
                    LogHelper.registrar(
                            (nombreCompleto.isEmpty() ? "Un nuevo cliente" : nombreCompleto)
                                    + " se ha unido a la aplicación",
                            com.example.inmia.models.Log.TIPO_CUENTA,
                            LogHelper.ROL_CLIENTE,
                            nombreCompleto, uid);

                    Toast.makeText(this, "¡Cuenta creada exitosamente!", Toast.LENGTH_LONG).show();

                    // Ir al Login y limpiar el back stack
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnCrearCuenta.setEnabled(true);
                    Log.w("Firebase", "Error al escribir documento", e);
                    Toast.makeText(this, "Error al guardar el perfil del usuario.", Toast.LENGTH_LONG).show();
                });
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
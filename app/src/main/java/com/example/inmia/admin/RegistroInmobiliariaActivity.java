package com.example.inmia.admin;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistroInmobiliariaActivity extends AppCompatActivity {

    // Campos
    private TextInputLayout tilNombreEmpresa, tilCorreo;
    private TextInputLayout tilTelefono, tilDireccion, tilDistrito;
    private TextInputEditText etNombreEmpresa, etCorreo;
    private TextInputEditText etTelefono, etDireccion, etDistrito;

    // Fotos
    private FrameLayout frameFoto1, frameFoto2, frameFoto3, frameFoto4;
    private ImageView imgFoto1, imgFoto2, imgFoto3, imgFoto4;
    private View layoutAdd1, layoutAdd2, layoutAdd3, layoutAdd4;
    private TextView tvContadorFotos;
    private MaterialButton btnGuardar;

    // Botón y contenedor de oficinas extra
    private MaterialButton btnAgregarOficina;
    private LinearLayout layoutOficinasExtra;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // URIs de fotos seleccionadas
    private Uri uriFoto1, uriFoto2, uriFoto3, uriFoto4;

    // Control del slot activo
    private int slotActivo  = 0;
    private int fotosSubidas = 0;

    // Contador de oficinas adicionales
    private int contadorOficinas = 1;

    // Launcher de galería
    private final ActivityResultLauncher<Intent> launcherGaleria =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {
                            Uri uri = result.getData().getData();
                            aplicarFoto(uri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_registro_inmobiliaria_inicio);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Vincular campos
        tilNombreEmpresa  = findViewById(R.id.tilNombreEmpresa);
        tilCorreo         = findViewById(R.id.tilCorreo);
        tilTelefono       = findViewById(R.id.tilTelefono);
        tilDireccion      = findViewById(R.id.tilDireccion);
        tilDistrito       = findViewById(R.id.tilDistrito);
        etNombreEmpresa   = findViewById(R.id.etNombreEmpresa);
        etCorreo          = findViewById(R.id.etCorreo);
        etTelefono        = findViewById(R.id.etTelefono);
        etDireccion       = findViewById(R.id.etDireccion);
        etDistrito        = findViewById(R.id.etDistrito);
        tvContadorFotos   = findViewById(R.id.tvContadorFotos);
        btnGuardar        = findViewById(R.id.btnGuardar);

        // ← CAMBIO 2: Botón y contenedor de oficinas extra
        btnAgregarOficina  = findViewById(R.id.btnAgregarOficina);
        layoutOficinasExtra = findViewById(R.id.layoutOficinasExtra);

        // Vincular frames de fotos
        frameFoto1 = findViewById(R.id.frameFoto1);
        frameFoto2 = findViewById(R.id.frameFoto2);
        frameFoto3 = findViewById(R.id.frameFoto3);
        frameFoto4 = findViewById(R.id.frameFoto4);
        imgFoto1   = findViewById(R.id.imgFoto1);
        imgFoto2   = findViewById(R.id.imgFoto2);
        imgFoto3   = findViewById(R.id.imgFoto3);
        imgFoto4   = findViewById(R.id.imgFoto4);
        layoutAdd1 = findViewById(R.id.layoutAdd1);
        layoutAdd2 = findViewById(R.id.layoutAdd2);
        layoutAdd3 = findViewById(R.id.layoutAdd3);
        layoutAdd4 = findViewById(R.id.layoutAdd4);

        // Clicks en cada slot de foto
        frameFoto1.setOnClickListener(v -> {
            slotActivo = 1;
            abrirGaleria();
        });
        frameFoto2.setOnClickListener(v -> {
            slotActivo = 2;
            abrirGaleria();
        });
        frameFoto3.setOnClickListener(v -> {
            slotActivo = 3;
            abrirGaleria();
        });
        frameFoto4.setOnClickListener(v -> {
            slotActivo = 4;
            abrirGaleria();
        });

        // ← CAMBIO 2: Click en botón añadir oficina
        btnAgregarOficina.setOnClickListener(v ->
                agregarCampoOficina(layoutOficinasExtra));

        // Botón guardar
        btnGuardar.setOnClickListener(v -> {
            if (validarFormulario()) {
                guardarYContinuar();
            }
        });
    }

    // ── GALERÍA ──────────────────────────────────────────────────────────────

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcherGaleria.launch(intent);
    }

    private void aplicarFoto(Uri uri) {
        switch (slotActivo) {
            case 1:
                uriFoto1 = uri;
                imgFoto1.setImageURI(uri);
                imgFoto1.setVisibility(View.VISIBLE);
                layoutAdd1.setVisibility(View.GONE);
                break;
            case 2:
                uriFoto2 = uri;
                imgFoto2.setImageURI(uri);
                imgFoto2.setVisibility(View.VISIBLE);
                layoutAdd2.setVisibility(View.GONE);
                break;
            case 3:
                uriFoto3 = uri;
                imgFoto3.setImageURI(uri);
                imgFoto3.setVisibility(View.VISIBLE);
                layoutAdd3.setVisibility(View.GONE);
                break;
            case 4:
                uriFoto4 = uri;
                imgFoto4.setImageURI(uri);
                imgFoto4.setVisibility(View.VISIBLE);
                layoutAdd4.setVisibility(View.GONE);
                break;
        }

        actualizarContadorFotos();
    }

    private void actualizarContadorFotos() {
        fotosSubidas = 0;
        if (uriFoto1 != null) fotosSubidas++;
        if (uriFoto2 != null) fotosSubidas++;
        if (uriFoto3 != null) fotosSubidas++;
        if (uriFoto4 != null) fotosSubidas++;

        tvContadorFotos.setText(fotosSubidas + "/2 mínimo");

        if (fotosSubidas >= 2) {
            tvContadorFotos.setTextColor(getColor(R.color.inmia_teal_dark));
        } else {
            tvContadorFotos.setTextColor(
                    getColor(android.R.color.holo_orange_dark));
        }
    }

    // ── OFICINAS DINÁMICAS ───────────────────────────────────────────────────

    private void agregarCampoOficina(LinearLayout contenedor) {
        contadorOficinas++;

        // ── Separador visual ──
        View divisor = new View(this);
        LinearLayout.LayoutParams paramsDivisor = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        paramsDivisor.topMargin = 24;
        paramsDivisor.bottomMargin = 20;
        divisor.setLayoutParams(paramsDivisor);
        divisor.setBackgroundColor(getColor(android.R.color.darker_gray) & 0x33FFFFFF);
        contenedor.addView(divisor);

        // ── Título del bloque ──
        TextView tvTitulo = new TextView(this);
        tvTitulo.setText("Oficina " + contadorOficinas);
        tvTitulo.setTextColor(getColor(R.color.inmia_text));
        tvTitulo.setTextSize(14f);
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams paramsTv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTv.bottomMargin = 16;
        tvTitulo.setLayoutParams(paramsTv);
        contenedor.addView(tvTitulo);

        // ── Campo Dirección ──
        contenedor.addView(crearCampo(
                "Dirección de oficina " + contadorOficinas,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
                0, 14));

        // ── Campo Distrito ──
        contenedor.addView(crearCampo(
                "Distrito",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                0, 14));

        // ── Campo Referencia (opcional) ──
        contenedor.addView(crearCampo(
                "Referencia (opcional)",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE,
                0, 0));
    }

    // ── Método auxiliar para crear cada campo ──────────────────────────────────
    private TextInputLayout crearCampo(String hint, int inputType,
                                       int marginTop, int marginBottom) {
        TextInputLayout til = new TextInputLayout(
                this,
                null,
                com.google.android.material.R.attr.textInputOutlinedStyle);

        til.setHint(hint);
        til.setBoxCornerRadii(12f, 12f, 12f, 12f);
        til.setBoxStrokeColorStateList(
                ColorStateList.valueOf(getColor(R.color.inmia_teal_dark)));
        til.setHintTextColor(
                ColorStateList.valueOf(getColor(R.color.inmia_teal_dark)));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin    = marginTop;
        params.bottomMargin = marginBottom;
        til.setLayoutParams(params);

        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setInputType(inputType);
        til.addView(et);

        return til;
    }

    // ── VALIDACIÓN ───────────────────────────────────────────────────────────

    private boolean validarFormulario() {
        boolean valido = true;

        // Limpiar errores
        tilNombreEmpresa.setError(null);
        tilCorreo.setError(null);
        tilTelefono.setError(null);
        tilDireccion.setError(null);
        tilDistrito.setError(null);

        String nombre   = getText(etNombreEmpresa);
        String correo   = getText(etCorreo);
        String telefono = getText(etTelefono);
        String dir      = getText(etDireccion);
        String distrito = getText(etDistrito);

        if (TextUtils.isEmpty(nombre)) {
            tilNombreEmpresa.setError("Este campo es obligatorio");
            valido = false;
        }
        if (TextUtils.isEmpty(correo)) {
            tilCorreo.setError("Este campo es obligatorio");
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError("Correo electrónico no válido");
            valido = false;
        }
        if (TextUtils.isEmpty(telefono)) {
            tilTelefono.setError("Este campo es obligatorio");
            valido = false;
        }
        if (TextUtils.isEmpty(dir)) {
            tilDireccion.setError("Este campo es obligatorio");
            valido = false;
        }
        if (TextUtils.isEmpty(distrito)) {
            tilDistrito.setError("Este campo es obligatorio");
            valido = false;
        }

        // ← CAMBIO 3: Validación de fotos comentada — no obligatoria por ahora
        /*
        if (fotosSubidas < 2) {
            Toast.makeText(this,
                    "Debes subir al menos 2 fotos promocionales",
                    Toast.LENGTH_LONG).show();
            valido = false;
        }
        */

        return valido;
    }

    // ── GUARDAR ──────────────────────────────────────────────────────────────

    private void guardarYContinuar() {
        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        String adminId = mAuth.getCurrentUser() != null
                ? mAuth.getCurrentUser().getUid() : "";

        db.collection("usuarios").document(adminId)
                .update("esPrimeraVez", false)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "¡Inmobiliaria registrada exitosamente!",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, AdminHomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar y continuar");
                    Toast.makeText(this,
                            "Error al guardar: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
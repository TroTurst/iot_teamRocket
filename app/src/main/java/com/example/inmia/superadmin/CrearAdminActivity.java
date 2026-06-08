package com.example.inmia.superadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearAdminActivity extends AppCompatActivity {

    // Campos de datos personales
    private TextInputLayout tilNombres, tilApellidos, tilTipoDocumento;
    private TextInputLayout tilNumDocumento, tilFechaNacimiento;
    private TextInputEditText etNombres, etApellidos, etNumDocumento;
    private TextInputEditText etFechaNacimiento;
    private AutoCompleteTextView spinnerTipoDocumento;

    // Campos de contacto
    private TextInputLayout tilCorreo, tilTelefono, tilDomicilio;
    private TextInputEditText etCorreo, etTelefono, etDomicilio;

    // Campos laborales
    private TextInputLayout tilInmobiliaria;
    private AutoCompleteTextView actvInmobiliaria;
    private final List<String> inmobNombres = new ArrayList<>();
    private final List<String> inmobIds     = new ArrayList<>();
    private String selectedInmobiliariaId   = "";

    // Campos de seguridad
    private TextInputLayout tilPassword, tilConfirmPassword;
    private TextInputEditText etPassword, etConfirmPassword;

    // Botón y overlay
    private MaterialButton btnCrearCuenta;
    private FrameLayout layoutLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_crear_admin);

        // Vincular vistas — datos personales
        tilNombres         = findViewById(R.id.tilNombres);
        tilApellidos       = findViewById(R.id.tilApellidos);
        tilTipoDocumento   = findViewById(R.id.tilTipoDocumento);
        tilNumDocumento    = findViewById(R.id.tilNumDocumento);
        tilFechaNacimiento = findViewById(R.id.tilFechaNacimiento);
        etNombres          = findViewById(R.id.etNombres);
        etApellidos        = findViewById(R.id.etApellidos);
        etNumDocumento     = findViewById(R.id.etNumDocumento);
        etFechaNacimiento  = findViewById(R.id.etFechaNacimiento);
        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);

        // Vincular vistas — contacto
        tilCorreo    = findViewById(R.id.tilCorreo);
        tilTelefono  = findViewById(R.id.tilTelefono);
        tilDomicilio = findViewById(R.id.tilDomicilio);
        etCorreo     = findViewById(R.id.etCorreo);
        etTelefono   = findViewById(R.id.etTelefono);
        etDomicilio  = findViewById(R.id.etDomicilio);

        // Vincular vistas — seguridad
        tilPassword        = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etPassword         = findViewById(R.id.etPassword);
        etConfirmPassword  = findViewById(R.id.etConfirmPassword);

        // Vincular vistas — laborales
        tilInmobiliaria  = findViewById(R.id.tilInmobiliaria);
        actvInmobiliaria = findViewById(R.id.actvInmobiliaria);

        // Botón y overlay
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        layoutLoading  = findViewById(R.id.layoutLoading);

        // Configurar dropdown tipo de documento
        configurarTipoDocumento();
        cargarInmobiliarias();

        // Configurar DatePicker para fecha de nacimiento
        etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());
        tilFechaNacimiento.setEndIconOnClickListener(v -> mostrarDatePicker());

        // Botón atrás
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Botón crear cuenta
        btnCrearCuenta.setOnClickListener(v -> {
            if (validarFormulario()) {
                registrarAdmin();
            }
        });
    }

    private void cargarInmobiliarias() {
        FirebaseFirestore.getInstance().collection("inmobiliarias").get()
                .addOnSuccessListener(query -> {
                    inmobNombres.clear();
                    inmobIds.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        String nombre = doc.getString("nombre");
                        if (nombre != null && !nombre.isEmpty()) {
                            inmobNombres.add(nombre);
                            inmobIds.add(doc.getId());
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_dropdown_item_1line, inmobNombres);
                    actvInmobiliaria.setAdapter(adapter);
                    actvInmobiliaria.setOnItemClickListener((parent, view, pos, id) ->
                            selectedInmobiliariaId = inmobIds.get(pos));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "No se pudieron cargar las inmobiliarias", Toast.LENGTH_SHORT).show());
    }

    private void configurarTipoDocumento() {
        String[] tiposDocumento = {"DNI", "Pasaporte", "Carnet de extranjería"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                tiposDocumento
        );
        spinnerTipoDocumento.setAdapter(adapter);
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int anio = calendar.get(Calendar.YEAR);
        int mes  = calendar.get(Calendar.MONTH);
        int dia  = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, anioSel, mesSel, diaSel) -> {
                    String fecha = String.format("%02d/%02d/%04d",
                            diaSel, mesSel + 1, anioSel);
                    etFechaNacimiento.setText(fecha);
                },
                anio, mes, dia
        );

        // No permitir fechas futuras
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.show();
    }

    private boolean validarFormulario() {
        boolean valido = true;

        // Limpiar errores previos
        tilNombres.setError(null);
        tilApellidos.setError(null);
        tilNumDocumento.setError(null);
        tilFechaNacimiento.setError(null);
        tilCorreo.setError(null);
        tilTelefono.setError(null);
        tilDomicilio.setError(null);
        tilInmobiliaria.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String nombres    = etNombres.getText() != null ? etNombres.getText().toString().trim() : "";
        String apellidos  = etApellidos.getText() != null ? etApellidos.getText().toString().trim() : "";
        String numDoc     = etNumDocumento.getText() != null ? etNumDocumento.getText().toString().trim() : "";
        String fechaNac   = etFechaNacimiento.getText() != null ? etFechaNacimiento.getText().toString().trim() : "";
        String correo     = etCorreo.getText() != null ? etCorreo.getText().toString().trim() : "";
        String telefono   = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        String domicilio  = etDomicilio.getText() != null ? etDomicilio.getText().toString().trim() : "";
        String password   = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPwd = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        // Validar nombres
        if (TextUtils.isEmpty(nombres)) {
            tilNombres.setError(getString(R.string.error_campo_requerido));
            valido = false;
        }

        // Validar apellidos
        if (TextUtils.isEmpty(apellidos)) {
            tilApellidos.setError(getString(R.string.error_campo_requerido));
            valido = false;
        }

        // Validar número documento
        if (TextUtils.isEmpty(numDoc)) {
            tilNumDocumento.setError(getString(R.string.error_campo_requerido));
            valido = false;
        }

        // Validar fecha nacimiento
        if (TextUtils.isEmpty(fechaNac)) {
            tilFechaNacimiento.setError(getString(R.string.error_campo_requerido));
            valido = false;
        }

        // Validar correo
        if (TextUtils.isEmpty(correo)) {
            tilCorreo.setError(getString(R.string.error_campo_requerido));
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError(getString(R.string.error_email_invalid));
            valido = false;
        }

        // Validar teléfono
        if (TextUtils.isEmpty(telefono)) {
            tilTelefono.setError(getString(R.string.error_campo_requerido));
            valido = false;
        }

        // Validar domicilio
        if (TextUtils.isEmpty(domicilio)) {
            tilDomicilio.setError(getString(R.string.error_campo_requerido));
            valido = false;
        }

        // Validar inmobiliaria
        if (selectedInmobiliariaId.isEmpty()) {
            tilInmobiliaria.setError("Selecciona una inmobiliaria");
            valido = false;
        }

        // Validar contraseña
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_campo_requerido));
            valido = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_corta));
            valido = false;
        }

        // Validar confirmar contraseña
        if (TextUtils.isEmpty(confirmPwd)) {
            tilConfirmPassword.setError(getString(R.string.error_campo_requerido));
            valido = false;
        } else if (!password.equals(confirmPwd)) {
            tilConfirmPassword.setError(getString(R.string.error_passwords_no_coinciden));
            valido = false;
        }

        return valido;
    }

    private void registrarAdmin() {
        btnCrearCuenta.setEnabled(false);
        layoutLoading.setVisibility(View.VISIBLE);

        String correo   = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Usamos una FirebaseApp secundaria para crear el usuario sin
        // desloguear al superadmin que está en sesión activa
        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.initializeApp(this,
                    FirebaseApp.getInstance().getOptions(), "creacion_admin");
        } catch (IllegalStateException e) {
            secondaryApp = FirebaseApp.getInstance("creacion_admin");
        }

        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);
        final FirebaseApp appRef = secondaryApp;

        secondaryAuth.createUserWithEmailAndPassword(correo, password)
            .addOnSuccessListener(result -> {
                String uid = result.getUser().getUid();
                secondaryAuth.signOut();
                try { appRef.delete(); } catch (Exception ignored) {}
                guardarEnFirestore(uid);
            })
            .addOnFailureListener(e -> {
                btnCrearCuenta.setEnabled(true);
                layoutLoading.setVisibility(View.GONE);
                try { appRef.delete(); } catch (Exception ignored) {}

                String msg = e.getMessage() != null ? e.getMessage() : "";
                if (msg.contains("email address is already in use")) {
                    tilCorreo.setError("Este correo ya está registrado");
                } else {
                    Toast.makeText(this,
                            "Error al crear cuenta: " + msg,
                            Toast.LENGTH_LONG).show();
                }
            });
    }

    private void guardarEnFirestore(String uid) {
        String nombres   = etNombres.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();

        Map<String, Object> datos = new HashMap<>();
        datos.put("nombres",          nombres);
        datos.put("apellidos",        apellidos);
        datos.put("tipoDocumento",    spinnerTipoDocumento.getText().toString());
        datos.put("numeroDocumento",  etNumDocumento.getText().toString().trim());
        datos.put("fechaNacimiento",  etFechaNacimiento.getText().toString().trim());
        datos.put("correo",           etCorreo.getText().toString().trim());
        datos.put("telefono",         etTelefono.getText().toString().trim());
        datos.put("domicilio",        etDomicilio.getText().toString().trim());
        datos.put("inmobiliariaId",   selectedInmobiliariaId);
        datos.put("rol",              "admin");
        datos.put("activo",           true);
        datos.put("fechaCreacion",    FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .set(datos)
            .addOnSuccessListener(unused -> {
                layoutLoading.setVisibility(View.GONE);

                NotificacionHelper.enviar(
                        this,
                        "Nuevo administrador registrado",
                        nombres + " " + apellidos + " ha sido añadido al sistema.",
                        NotificacionHelper.TIPO_ADMIN_CREADO
                );

                Toast.makeText(this,
                        getString(R.string.registro_exitoso),
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, GestionUsuariosActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            })
            .addOnFailureListener(e -> {
                btnCrearCuenta.setEnabled(true);
                layoutLoading.setVisibility(View.GONE);
                Toast.makeText(this,
                        "Error al guardar datos: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            });
    }
}
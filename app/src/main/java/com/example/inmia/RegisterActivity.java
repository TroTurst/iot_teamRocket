package com.example.inmia;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilNombres, tilApellidos, tilNumDocumento;
    private TextInputLayout tilFechaNacimiento, tilCorreo;
    private TextInputLayout tilTelefono, tilDomicilio;

    private TextInputEditText etNombres, etApellidos, etNumDocumento;
    private TextInputEditText etFechaNacimiento, etCorreo;
    private TextInputEditText etTelefono, etDomicilio;

    private AutoCompleteTextView spinnerTipoDocumento;
    private MaterialButton btnSiguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_register);

        // Vincular vistas
        tilNombres         = findViewById(R.id.tilNombres);
        tilApellidos       = findViewById(R.id.tilApellidos);
        tilNumDocumento    = findViewById(R.id.tilNumDocumento);
        tilFechaNacimiento = findViewById(R.id.tilFechaNacimiento);
        tilCorreo          = findViewById(R.id.tilCorreo);
        tilTelefono        = findViewById(R.id.tilTelefono);
        tilDomicilio       = findViewById(R.id.tilDomicilio);
        etNombres          = findViewById(R.id.etNombres);
        etApellidos        = findViewById(R.id.etApellidos);
        etNumDocumento     = findViewById(R.id.etNumDocumento);
        etFechaNacimiento  = findViewById(R.id.etFechaNacimiento);
        etCorreo           = findViewById(R.id.etCorreo);
        etTelefono         = findViewById(R.id.etTelefono);
        etDomicilio        = findViewById(R.id.etDomicilio);
        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);
        btnSiguiente       = findViewById(R.id.btnSiguiente);

        // Dropdown tipo documento
        String[] tipos = {"DNI", "Pasaporte", "Carnet de extranjería"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, tipos);
        spinnerTipoDocumento.setAdapter(adapter);

        // DatePicker fecha nacimiento
        etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());
        tilFechaNacimiento.setEndIconOnClickListener(v -> mostrarDatePicker());

        // Botón atrás
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Botón siguiente — validar y pasar al paso 2
        btnSiguiente.setOnClickListener(v -> {
            if (validarFormulario()) {
                pasarAlPaso2();
            }
        });
    }

    private void mostrarDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, anio, mes, dia) -> {
                    etFechaNacimiento.setText(
                            String.format("%02d/%02d/%04d", dia, mes + 1, anio));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private boolean validarFormulario() {
        boolean valido = true;

        // Limpiar errores
        tilNombres.setError(null);
        tilApellidos.setError(null);
        tilNumDocumento.setError(null);
        tilFechaNacimiento.setError(null);
        tilCorreo.setError(null);
        tilTelefono.setError(null);
        tilDomicilio.setError(null);

        String nombres   = getText(etNombres);
        String apellidos = getText(etApellidos);
        String numDoc    = getText(etNumDocumento);
        String fechaNac  = getText(etFechaNacimiento);
        String correo    = getText(etCorreo);
        String telefono  = getText(etTelefono);
        String domicilio = getText(etDomicilio);

        if (TextUtils.isEmpty(nombres)) {
            tilNombres.setError("Este campo es obligatorio");
            valido = false;
        }
        if (TextUtils.isEmpty(apellidos)) {
            tilApellidos.setError("Este campo es obligatorio");
            valido = false;
        }
        String tipoDocumento = spinnerTipoDocumento.getText() != null
                ? spinnerTipoDocumento.getText().toString().trim() : "";
        if (TextUtils.isEmpty(numDoc)) {
            tilNumDocumento.setError("Este campo es obligatorio");
            valido = false;
        } else if (tipoDocumento.equals("DNI") && !numDoc.matches("\\d{8}")) {
            tilNumDocumento.setError("El DNI debe tener 8 dígitos");
            valido = false;
        }
        if (TextUtils.isEmpty(fechaNac)) {
            tilFechaNacimiento.setError("Este campo es obligatorio");
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
        } else if (!telefono.matches("\\d{9}")) {
            tilTelefono.setError("El teléfono debe tener 9 dígitos");
            valido = false;
        }
        if (TextUtils.isEmpty(domicilio)) {
            tilDomicilio.setError("Este campo es obligatorio");
            valido = false;
        }

        return valido;
    }

    private void pasarAlPaso2() {
        // Pasar los datos al paso 2 via Intent
        Intent intent = new Intent(this, RegisterFotoActivity.class);
        intent.putExtra("nombres",    getText(etNombres));
        intent.putExtra("apellidos",  getText(etApellidos));
        intent.putExtra("tipoDoc",    spinnerTipoDocumento.getText().toString());
        intent.putExtra("numDoc",     getText(etNumDocumento));
        intent.putExtra("fechaNac",   getText(etFechaNacimiento));
        intent.putExtra("correo",     getText(etCorreo));
        intent.putExtra("telefono",   getText(etTelefono));
        intent.putExtra("domicilio",  getText(etDomicilio));
        startActivity(intent);
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
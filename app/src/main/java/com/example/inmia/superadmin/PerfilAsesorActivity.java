package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;

public class PerfilAsesorActivity extends AppCompatActivity {

    // Claves para pasar datos entre activities
    public static final String EXTRA_NOMBRE        = "nombre";
    public static final String EXTRA_DOCUMENTO     = "documento";
    public static final String EXTRA_FECHA_NAC     = "fecha_nac";
    public static final String EXTRA_CORREO        = "correo";
    public static final String EXTRA_TELEFONO      = "telefono";
    public static final String EXTRA_DOMICILIO     = "domicilio";
    public static final String EXTRA_INMOBILIARIA  = "inmobiliaria";

    private TextView tvNombreAsesor, tvInmobiliaria;
    private TextView tvNombreCompleto, tvDocumento;
    private TextView tvFechaNacimiento, tvCorreo;
    private TextView tvTelefono, tvDomicilio;
    private MaterialButton btnHabilitar, btnRechazar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_perfil_asesor);

        // Vincular vistas
        tvNombreAsesor    = findViewById(R.id.tvNombreAsesor);
        tvInmobiliaria    = findViewById(R.id.tvInmobiliaria);
        tvNombreCompleto  = findViewById(R.id.tvNombreCompleto);
        tvDocumento       = findViewById(R.id.tvDocumento);
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento);
        tvCorreo          = findViewById(R.id.tvCorreo);
        tvTelefono        = findViewById(R.id.tvTelefono);
        tvDomicilio       = findViewById(R.id.tvDomicilio);
        btnHabilitar      = findViewById(R.id.btnHabilitar);
        btnRechazar       = findViewById(R.id.btnRechazar);

        // Recibir datos del Intent
        cargarDatos();

        // Botón atrás
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Botón Habilitar — con AlertDialog
        btnHabilitar.setOnClickListener(v ->
                mostrarDialogoHabilitar());

        // Botón Rechazar — con AlertDialog
        btnRechazar.setOnClickListener(v ->
                mostrarDialogoRechazar());
    }

    private void cargarDatos() {
        Intent intent = getIntent();

        String nombre       = intent.getStringExtra(EXTRA_NOMBRE);
        String inmobiliaria = intent.getStringExtra(EXTRA_INMOBILIARIA);
        String documento    = intent.getStringExtra(EXTRA_DOCUMENTO);
        String fechaNac     = intent.getStringExtra(EXTRA_FECHA_NAC);
        String correo       = intent.getStringExtra(EXTRA_CORREO);
        String telefono     = intent.getStringExtra(EXTRA_TELEFONO);
        String domicilio    = intent.getStringExtra(EXTRA_DOMICILIO);

        // Datos con fallback si vienen nulos
        tvNombreAsesor.setText(nombre != null ? nombre : "—");
        tvInmobiliaria.setText(inmobiliaria != null ? inmobiliaria : "—");
        tvNombreCompleto.setText(nombre != null ? nombre : "—");
        tvDocumento.setText(documento != null ? documento : "—");
        tvFechaNacimiento.setText(fechaNac != null ? fechaNac : "—");
        tvCorreo.setText(correo != null ? correo : "—");
        tvTelefono.setText(telefono != null ? telefono : "—");
        tvDomicilio.setText(domicilio != null ? domicilio : "—");
    }

    private void mostrarDialogoHabilitar() {
        String nombre = getIntent().getStringExtra(EXTRA_NOMBRE);
        DialogHelper.mostrarDialogoAccion(
            this,
            "¿Habilitar asesor?",
            "¿Estás seguro de habilitar a " + nombre + " como asesor de ventas?",
            "Habilitar",
            "Cancelar",
            R.color.inmia_success,
            R.drawable.bg_badge_teal,
            this::irASolicitudes
        );
    }

    private void mostrarDialogoRechazar() {
        String nombre = getIntent().getStringExtra(EXTRA_NOMBRE);
        DialogHelper.mostrarDialogoAccion(
            this,
            "¿Rechazar solicitud?",
            "¿Estás seguro de rechazar la solicitud de " + nombre + "? Esta acción no se puede deshacer.",
            "Rechazar",
            "Cancelar",
            R.color.inmia_danger,
            R.drawable.bg_badge_red_circle,
            this::irASolicitudes
        );
    }

    private void irASolicitudes() {
        Intent intent = new Intent(this, SolicitudesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
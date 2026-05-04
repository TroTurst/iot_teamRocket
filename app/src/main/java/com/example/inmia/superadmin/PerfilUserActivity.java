package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;

public class PerfilUserActivity extends AppCompatActivity {

    public static final String EXTRA_NOMBRE    = "nombre";
    public static final String EXTRA_EMPRESA   = "empresa";
    public static final String EXTRA_INICIALES = "iniciales";
    public static final String EXTRA_DOCUMENTO = "documento";
    public static final String EXTRA_FECHA_NAC = "fecha_nac";
    public static final String EXTRA_CORREO    = "correo";
    public static final String EXTRA_TELEFONO  = "telefono";
    public static final String EXTRA_DOMICILIO = "domicilio";
    public static final String EXTRA_ACTIVO    = "activo";
    public static final String EXTRA_ROL       = "rol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_perfil_user);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        cargarDatos();
    }

    private void cargarDatos() {
        Intent intent = getIntent();

        String nombre    = intent.getStringExtra(EXTRA_NOMBRE);
        String empresa   = intent.getStringExtra(EXTRA_EMPRESA);
        String iniciales = intent.getStringExtra(EXTRA_INICIALES);
        String documento = intent.getStringExtra(EXTRA_DOCUMENTO);
        String fechaNac  = intent.getStringExtra(EXTRA_FECHA_NAC);
        String correo    = intent.getStringExtra(EXTRA_CORREO);
        String telefono  = intent.getStringExtra(EXTRA_TELEFONO);
        String domicilio = intent.getStringExtra(EXTRA_DOMICILIO);
        boolean activo   = intent.getBooleanExtra(EXTRA_ACTIVO, true);
        String rol       = intent.getStringExtra(EXTRA_ROL);

        // Fallbacks
        if (nombre    == null) nombre    = "—";
        if (empresa   == null) empresa   = "—";
        if (iniciales == null) iniciales = "??";
        if (documento == null) documento = "—";
        if (fechaNac  == null) fechaNac  = "—";
        if (correo    == null) correo    = "—";
        if (telefono  == null) telefono  = "—";
        if (domicilio == null) domicilio = "—";
        if (rol       == null) rol       = "—";

        // ── Avatar ──
        ((TextView) findViewById(R.id.tvAvatar)).setText(iniciales);

        // ── Header ──
        ((TextView) findViewById(R.id.tvNombreAdmin)).setText(nombre);
        ((TextView) findViewById(R.id.tvInmobiliaria)).setText(empresa);

        // ── Badge estado header ──
        TextView tvBadge = findViewById(R.id.tvBadgeEstado);
        if (activo) {
            tvBadge.setText("Activo");
            tvBadge.setTextColor(getColor(android.R.color.holo_green_dark));
            tvBadge.setBackgroundResource(R.drawable.badge_activo_superadmin);
        } else {
            tvBadge.setText("Inactivo");
            tvBadge.setTextColor(getColor(android.R.color.holo_red_dark));
            tvBadge.setBackgroundResource(R.drawable.badge_inactivo_superadmin);
        }

        // ── Card información personal ──
        ((TextView) findViewById(R.id.tvNombreCompleto)).setText(nombre);
        ((TextView) findViewById(R.id.tvDocumento)).setText(documento);
        ((TextView) findViewById(R.id.tvFechaNacimiento)).setText(fechaNac);

        // ── Card contacto ──
        ((TextView) findViewById(R.id.tvCorreo)).setText(correo);
        ((TextView) findViewById(R.id.tvTelefono)).setText(telefono);
        ((TextView) findViewById(R.id.tvDomicilio)).setText(domicilio);

        // ── Card rol ──
        TextView tvRolAsignado = findViewById(R.id.tvRolAsignado);
        TextView tvBadgeRol    = findViewById(R.id.tvBadgeRol);

        switch (rol) {
            case "admin":
                tvRolAsignado.setText("Administrador de Inmobiliaria");
                break;
            case "asesor":
                tvRolAsignado.setText("Asesor de Ventas");
                break;
            case "cliente":
                tvRolAsignado.setText("Cliente");
                break;
            default:
                tvRolAsignado.setText(rol);
        }

        if (activo) {
            tvBadgeRol.setText("Activo");
            tvBadgeRol.setTextColor(getColor(android.R.color.holo_green_dark));
            tvBadgeRol.setBackgroundResource(R.drawable.badge_activo_superadmin);
        } else {
            tvBadgeRol.setText("Inactivo");
            tvBadgeRol.setTextColor(getColor(android.R.color.holo_red_dark));
            tvBadgeRol.setBackgroundResource(R.drawable.badge_inactivo_superadmin);
        }
    }
}
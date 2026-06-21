package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.models.Log;
import com.example.inmia.util.LogHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PerfilAsesorActivity extends AppCompatActivity {

    public static final String EXTRA_FIRESTORE_ID   = "firestoreId";
    public static final String EXTRA_NOMBRE         = "nombre";
    public static final String EXTRA_APELLIDOS      = "apellidos";
    public static final String EXTRA_DOCUMENTO      = "documento";
    public static final String EXTRA_FECHA_NAC      = "fecha_nac";
    public static final String EXTRA_CORREO         = "correo";
    public static final String EXTRA_TELEFONO       = "telefono";
    public static final String EXTRA_DOMICILIO      = "domicilio";
    public static final String EXTRA_INMOBILIARIA    = "inmobiliaria";
    public static final String EXTRA_INMOBILIARIA_ID = "inmobiliariaId";
    public static final String EXTRA_OFICINA        = "oficina";
    public static final String EXTRA_FOTO_URL       = "fotoUrl";
    public static final String EXTRA_TIPO_DOCUMENTO = "tipoDocumento";
    public static final String EXTRA_NUM_DOCUMENTO  = "numeroDocumento";
    public static final String EXTRA_FECHA_NAC_ORIG = "fechaNacOrig";

    private TextView tvNombreAsesor, tvInmobiliaria;
    private TextView tvNombreCompleto, tvDocumento;
    private TextView tvFechaNacimiento, tvCorreo;
    private TextView tvTelefono, tvDomicilio, tvOficina;
    private ImageView imgFotoPerfil;
    private MaterialButton btnHabilitar, btnRechazar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.sa_activity_perfil_asesor);

        db = FirebaseFirestore.getInstance();

        tvNombreAsesor    = findViewById(R.id.tvNombreAsesor);
        tvInmobiliaria    = findViewById(R.id.tvInmobiliaria);
        tvNombreCompleto  = findViewById(R.id.tvNombreCompleto);
        tvDocumento       = findViewById(R.id.tvDocumento);
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento);
        tvCorreo          = findViewById(R.id.tvCorreo);
        tvTelefono        = findViewById(R.id.tvTelefono);
        tvDomicilio       = findViewById(R.id.tvDomicilio);
        tvOficina         = findViewById(R.id.tvOficina);
        imgFotoPerfil     = findViewById(R.id.imgFotoPerfil);
        btnHabilitar      = findViewById(R.id.btnHabilitar);
        btnRechazar       = findViewById(R.id.btnRechazar);

        cargarDatos();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnHabilitar.setOnClickListener(v -> mostrarDialogoHabilitar());
        btnRechazar.setOnClickListener(v  -> mostrarDialogoRechazar());
    }

    private void cargarDatos() {
        Intent i = getIntent();

        String nombre      = i.getStringExtra(EXTRA_NOMBRE);
        String inmob       = i.getStringExtra(EXTRA_INMOBILIARIA);
        String oficina     = i.getStringExtra(EXTRA_OFICINA);
        String documento   = i.getStringExtra(EXTRA_DOCUMENTO);
        String fechaNac    = i.getStringExtra(EXTRA_FECHA_NAC);
        String correo      = i.getStringExtra(EXTRA_CORREO);
        String telefono    = i.getStringExtra(EXTRA_TELEFONO);
        String domicilio   = i.getStringExtra(EXTRA_DOMICILIO);
        String fotoUrl     = i.getStringExtra(EXTRA_FOTO_URL);

        tvNombreAsesor.setText(nombre != null ? nombre : "—");
        tvInmobiliaria.setText(inmob != null && !inmob.isEmpty() ? inmob : "—");
        tvNombreCompleto.setText(nombre != null ? nombre   : "—");
        tvDocumento.setText(documento   != null ? documento : "—");
        tvFechaNacimiento.setText(fechaNac != null ? fechaNac : "—");
        tvCorreo.setText(correo         != null ? correo   : "—");
        tvTelefono.setText(telefono     != null ? telefono : "—");
        tvDomicilio.setText(domicilio   != null ? domicilio : "—");
        if (tvOficina != null) tvOficina.setText(oficina   != null ? oficina : "—");

        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            imgFotoPerfil.setPadding(0, 0, 0, 0);
            imgFotoPerfil.clearColorFilter();
            Glide.with(this).load(fotoUrl).circleCrop().into(imgFotoPerfil);
        }
    }

    // ── DIÁLOGOS ──────────────────────────────────────────────────────────────

    private void mostrarDialogoHabilitar() {
        String nombre = getIntent().getStringExtra(EXTRA_NOMBRE);
        DialogHelper.mostrarDialogoAccion(this,
                "¿Habilitar asesor?",
                "¿Estás seguro de habilitar a " + nombre + " como asesor de ventas?",
                "Habilitar", "Cancelar",
                R.color.inmia_success, R.drawable.bg_badge_teal,
                this::habilitarAsesor);
    }

    private void mostrarDialogoRechazar() {
        String nombre = getIntent().getStringExtra(EXTRA_NOMBRE);
        DialogHelper.mostrarDialogoAccion(this,
                "¿Rechazar solicitud?",
                "¿Estás seguro de rechazar la solicitud de " + nombre + "? Esta acción no se puede deshacer.",
                "Rechazar", "Cancelar",
                R.color.inmia_danger, R.drawable.bg_badge_red_circle,
                this::rechazarSolicitud);
    }

    // ── HABILITAR ─────────────────────────────────────────────────────────────

    private void habilitarAsesor() {
        Intent i = getIntent();
        String correo     = i.getStringExtra(EXTRA_CORREO);
        String firestoreId = i.getStringExtra(EXTRA_FIRESTORE_ID);

        if (correo == null || correo.isEmpty()) {
            Toast.makeText(this, "El asesor no tiene correo registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        btnHabilitar.setEnabled(false);
        btnHabilitar.setText("Habilitando...");

        String tempPassword = UUID.randomUUID().toString().substring(0, 12) + "!A1";

        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.initializeApp(this,
                    FirebaseApp.getInstance().getOptions(), "creacion_asesor_perfil");
        } catch (IllegalStateException e) {
            secondaryApp = FirebaseApp.getInstance("creacion_asesor_perfil");
        }

        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);
        final FirebaseApp appRef = secondaryApp;

        secondaryAuth.createUserWithEmailAndPassword(correo, tempPassword)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    secondaryAuth.signOut();
                    try { appRef.delete(); } catch (Exception ignored) {}

                    FirebaseAuth.getInstance().sendPasswordResetEmail(correo);
                    guardarAsesorEnUsuarios(uid, firestoreId);
                })
                .addOnFailureListener(e -> {
                    try { appRef.delete(); } catch (Exception ignored) {}
                    btnHabilitar.setEnabled(true);
                    btnHabilitar.setText("Habilitar");
                    String msg = e.getMessage() != null ? e.getMessage() : "";
                    if (msg.contains("already in use")) {
                        Toast.makeText(this, "Este correo ya tiene cuenta registrada", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error al crear cuenta: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarAsesorEnUsuarios(String uid, String firestoreId) {
        Intent i = getIntent();

        Map<String, Object> datos = new HashMap<>();
        datos.put("nombres",         i.getStringExtra(EXTRA_NOMBRE) != null
                ? i.getStringExtra(EXTRA_NOMBRE).split(" ")[0] : "");
        datos.put("apellidos",       i.getStringExtra(EXTRA_APELLIDOS) != null
                ? i.getStringExtra(EXTRA_APELLIDOS) : "");
        datos.put("tipoDocumento",   i.getStringExtra(EXTRA_TIPO_DOCUMENTO) != null
                ? i.getStringExtra(EXTRA_TIPO_DOCUMENTO) : "DNI");
        datos.put("numeroDocumento", i.getStringExtra(EXTRA_NUM_DOCUMENTO) != null
                ? i.getStringExtra(EXTRA_NUM_DOCUMENTO) : "");
        datos.put("fechaNacimiento", i.getStringExtra(EXTRA_FECHA_NAC) != null
                ? i.getStringExtra(EXTRA_FECHA_NAC) : "");
        datos.put("correo",          i.getStringExtra(EXTRA_CORREO) != null
                ? i.getStringExtra(EXTRA_CORREO) : "");
        datos.put("telefono",        i.getStringExtra(EXTRA_TELEFONO) != null
                ? i.getStringExtra(EXTRA_TELEFONO) : "");
        datos.put("domicilio",       i.getStringExtra(EXTRA_DOMICILIO) != null
                ? i.getStringExtra(EXTRA_DOMICILIO) : "");
        datos.put("oficina",         i.getStringExtra(EXTRA_OFICINA) != null
                ? i.getStringExtra(EXTRA_OFICINA) : "");
        datos.put("fotoUrl",         i.getStringExtra(EXTRA_FOTO_URL) != null
                ? i.getStringExtra(EXTRA_FOTO_URL) : "");
        datos.put("inmobiliariaId",  i.getStringExtra(EXTRA_INMOBILIARIA_ID) != null
                ? i.getStringExtra(EXTRA_INMOBILIARIA_ID) : "");
        datos.put("rol",             "asesor");
        datos.put("activo",          true);
        datos.put("fechaCreacion",   FieldValue.serverTimestamp());

        db.collection("usuarios").document(uid).set(datos)
                .addOnSuccessListener(unused -> {
                    if (firestoreId != null && !firestoreId.isEmpty()) {
                        db.collection("solicitudes").document(firestoreId)
                                .update("estado", "aprobado");
                    }

                    String nombre = i.getStringExtra(EXTRA_NOMBRE);
                    NotificacionHelper.enviar(this, "Asesor habilitado",
                            nombre + " ha sido habilitado como asesor.",
                            NotificacionHelper.TIPO_ASESOR_HABILITADO);

                    LogHelper.registrar(
                            (nombre != null ? nombre : "Un asesor")
                                    + " fue habilitado como asesor de ventas",
                            Log.TIPO_SOLICITUD,
                            LogHelper.ROL_SUPERADMIN,
                            nombre != null ? nombre : "", uid);

                    Toast.makeText(this,
                            "Asesor habilitado. Se envió correo para establecer contraseña.",
                            Toast.LENGTH_LONG).show();

                    irASolicitudes();
                })
                .addOnFailureListener(e -> {
                    btnHabilitar.setEnabled(true);
                    btnHabilitar.setText("Habilitar");
                    Toast.makeText(this, "Error al guardar asesor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // ── RECHAZAR ──────────────────────────────────────────────────────────────

    private void rechazarSolicitud() {
        String firestoreId = getIntent().getStringExtra(EXTRA_FIRESTORE_ID);
        String nombre      = getIntent().getStringExtra(EXTRA_NOMBRE);

        if (firestoreId != null && !firestoreId.isEmpty()) {
            db.collection("solicitudes").document(firestoreId)
                    .update("estado", "rechazado");
        }

        NotificacionHelper.enviar(this, "Solicitud rechazada",
                "La solicitud de " + nombre + " ha sido rechazada.",
                NotificacionHelper.TIPO_ASESOR_RECHAZADO);

        LogHelper.registrar(
                "Se rechazó la solicitud de asesor de " + (nombre != null ? nombre : ""),
                Log.TIPO_SOLICITUD,
                LogHelper.ROL_SUPERADMIN);

        Toast.makeText(this, "Solicitud rechazada.", Toast.LENGTH_SHORT).show();
        irASolicitudes();
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private void irASolicitudes() {
        Intent intent = new Intent(this, SolicitudesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}

package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ClientePerfilClienteActivity extends AppCompatActivity {

    private ImageView btnEditarDatos;
    private LinearLayout layoutMetodosPago, layoutSeguridad;
    private LinearLayout layoutNotificaciones, layoutCerrarSesion;
    private LinearLayout layoutCambiarPassword, layoutMisResenias;

    private TextView tvNombreUsuario, tvNombre, tvCorreo;
    private TextView tvTelefono, tvUbicacion, tvAvatar;
    private TextView tvBadgeResenias;
    private ImageView imgAvatar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_perfil_cliente);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        inicializarVistas();
        configurarNavegacion();
        configurarMenuInferior();
        cargarDatosDeFirebase();
        cargarContadorResenias();
    }


    private void inicializarVistas() {
        btnEditarDatos       = findViewById(R.id.btnEditarDatos);
        layoutMetodosPago    = findViewById(R.id.layoutMetodosPago);
        layoutSeguridad      = findViewById(R.id.layoutSeguridad);
        layoutNotificaciones = findViewById(R.id.layoutNotificaciones);
        layoutCerrarSesion   = findViewById(R.id.layoutCerrarSesion);
        layoutCambiarPassword = findViewById(R.id.layoutCambiarPassword);
        layoutMisResenias    = findViewById(R.id.layoutMisResenias);
        tvBadgeResenias      = findViewById(R.id.tvBadgeResenias);

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvNombre        = findViewById(R.id.tvNombre);
        tvCorreo        = findViewById(R.id.tvCorreo);
        tvTelefono      = findViewById(R.id.tvTelefono);
        tvUbicacion     = findViewById(R.id.tvUbicacion);
        tvAvatar        = findViewById(R.id.tvAvatar);
        imgAvatar       = findViewById(R.id.imgAvatar);
    }


    private void cargarDatosDeFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No hay usuario conectado", Toast.LENGTH_SHORT).show();
            return;
        }

        tvCorreo.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Sin correo");

        db.collection("usuarios").document(currentUser.getUid()).get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) return;

                    String nombresBD   = document.getString("nombres");
                    String apellidosBD = document.getString("apellidos");
                    String telefonoBD  = document.getString("telefono");
                    String domicilioBD = document.getString("domicilio");

                    String nombreCompleto = "Usuario Invitado";
                    if (nombresBD != null && apellidosBD != null) {
                        nombreCompleto = nombresBD + " " + apellidosBD;
                    } else if (nombresBD != null) {
                        nombreCompleto = nombresBD;
                    }

                    tvNombreUsuario.setText(nombreCompleto);
                    tvNombre.setText(nombreCompleto);
                    tvTelefono.setText(telefonoBD != null ? telefonoBD : "No registrado");
                    tvUbicacion.setText(domicilioBD != null ? domicilioBD : "Ubicación no registrada");


                    String[] partes = nombreCompleto.trim().split(" ");
                    String iniciales = partes.length >= 2
                            ? "" + partes[0].charAt(0) + partes[1].charAt(0)
                            : nombreCompleto.length() >= 2
                            ? nombreCompleto.substring(0, 2)
                            : nombreCompleto;
                    tvAvatar.setText(iniciales.toUpperCase());


                    String fotoUrl = document.getString("fotoUrl");
                    if (fotoUrl != null && !fotoUrl.isEmpty()) {
                        imgAvatar.setVisibility(View.VISIBLE);
                        tvAvatar.setVisibility(View.GONE);
                        Glide.with(this).load(fotoUrl).circleCrop().into(imgAvatar);
                    } else {
                        imgAvatar.setVisibility(View.GONE);
                        tvAvatar.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar el perfil", Toast.LENGTH_SHORT).show());
    }


    private void cargarContadorResenias() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collectionGroup("valoraciones")
                .whereEqualTo("usuarioId", currentUser.getUid())
                .get()
                .addOnSuccessListener((QuerySnapshot snap) -> {
                    int total = snap.size();
                    if (total > 0 && tvBadgeResenias != null) {
                        tvBadgeResenias.setText(String.valueOf(total));
                        tvBadgeResenias.setVisibility(View.VISIBLE);
                    }
                });
    }


    private void configurarNavegacion() {
        if (btnEditarDatos != null)
            btnEditarDatos.setOnClickListener(v ->
                    startActivity(new Intent(this, ClienteInformacionPersonalActivity.class)));

        if (layoutMetodosPago != null)
            layoutMetodosPago.setOnClickListener(v ->
                    startActivity(new Intent(this, ClienteTusTarjetasActivity.class)));

        if (layoutSeguridad != null)
            layoutSeguridad.setOnClickListener(v ->
                    startActivity(new Intent(this, ClienteSeguridadActivity.class)));

        if (layoutNotificaciones != null)
            layoutNotificaciones.setOnClickListener(v ->
                    startActivity(new Intent(this, ClienteConfigNotificacionesActivity.class)));

        if (layoutCambiarPassword != null)
            layoutCambiarPassword.setOnClickListener(v ->
                    startActivity(new Intent(this, ClienteCambiarPasswordActivity.class)));

        if (layoutCerrarSesion != null)
            layoutCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());

        // ← NUEVO: abre pantalla de reseñas
        if (layoutMisResenias != null)
            layoutMisResenias.setOnClickListener(v ->
                    startActivity(new Intent(this, ClienteMisReseniasActivity.class)));
    }

    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void configurarMenuInferior() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(R.id.nav_perfil);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, ClienteHomeActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(this, ClienteCitasActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ClienteMensajesActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (id == R.id.nav_perfil) {
                return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            }
            return false;
        });
    }
}
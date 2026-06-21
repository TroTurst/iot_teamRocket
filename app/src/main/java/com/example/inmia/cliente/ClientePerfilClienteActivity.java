package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
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

public class ClientePerfilClienteActivity extends AppCompatActivity {

    private ImageView btnEditarDatos;
    private LinearLayout layoutMetodosPago;
    private LinearLayout layoutSeguridad;
    private LinearLayout layoutNotificaciones;
    private LinearLayout layoutCerrarSesion;
    private LinearLayout layoutCambiarPassword;

    private TextView tvNombreUsuario, tvNombre, tvCorreo, tvTelefono, tvUbicacion, tvAvatar;
    private ImageView imgAvatar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_perfil_cliente);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inicializarVistas();
        configurarNavegacion();
        configurarMenuInferior();

        cargarDatosDeFirebase();
    }

    private void inicializarVistas() {
        btnEditarDatos = findViewById(R.id.btnEditarDatos);
        layoutMetodosPago = findViewById(R.id.layoutMetodosPago);
        layoutSeguridad = findViewById(R.id.layoutSeguridad);
        layoutNotificaciones = findViewById(R.id.layoutNotificaciones);
        layoutCerrarSesion = findViewById(R.id.layoutCerrarSesion);
        layoutCambiarPassword = findViewById(R.id.layoutCambiarPassword);

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvNombre = findViewById(R.id.tvNombre);
        tvCorreo = findViewById(R.id.tvCorreo);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        tvAvatar = findViewById(R.id.tvAvatar);
        imgAvatar = findViewById(R.id.imgAvatar);
    }

    private void cargarDatosDeFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "No hay usuario conectado", Toast.LENGTH_SHORT).show();
            return;
        }

        String correoAuth = currentUser.getEmail();
        tvCorreo.setText(correoAuth != null ? correoAuth : "Sin correo");

        String userId = currentUser.getUid();

        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nombresBD = document.getString("nombres");
                        String apellidosBD = document.getString("apellidos");
                        String telefonoBD = document.getString("telefono");
                        String domicilioBD = document.getString("domicilio");

                        String nombreCompleto = "Usuario Invitado";
                        if (nombresBD != null && apellidosBD != null) {
                            nombreCompleto = nombresBD + " " + apellidosBD;
                        } else if (nombresBD != null) {
                            nombreCompleto = nombresBD;
                        }

                        String telefonoFinal = telefonoBD != null ? telefonoBD : "No registrado";
                        String ubicacionFinal = domicilioBD != null ? domicilioBD : "Ubicación no registrada";

                        tvNombreUsuario.setText(nombreCompleto);
                        tvNombre.setText(nombreCompleto);
                        tvTelefono.setText(telefonoFinal);
                        tvUbicacion.setText(ubicacionFinal);

                        String[] partes = nombreCompleto.trim().split(" ");
                        String iniciales = "";
                        if (partes.length >= 2) {
                            iniciales = partes[0].substring(0, 1) + partes[1].substring(0, 1);
                        } else if (nombreCompleto.length() >= 2) {
                            iniciales = nombreCompleto.substring(0, 2);
                        } else if (nombreCompleto.length() == 1) {
                            iniciales = nombreCompleto;
                        }
                        tvAvatar.setText(iniciales.toUpperCase());

                        // Foto de perfil si existe, sino iniciales
                        String fotoUrl = document.getString("fotoUrl");
                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            imgAvatar.setVisibility(android.view.View.VISIBLE);
                            tvAvatar.setVisibility(android.view.View.GONE);
                            Glide.with(this).load(fotoUrl).circleCrop().into(imgAvatar);
                        } else {
                            imgAvatar.setVisibility(android.view.View.GONE);
                            tvAvatar.setVisibility(android.view.View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
                });
    }

    private void configurarNavegacion() {
        if (btnEditarDatos != null) {
            btnEditarDatos.setOnClickListener(v -> startActivity(new Intent(this, ClienteInformacionPersonalActivity.class)));
        }
        if (layoutMetodosPago != null) {
            layoutMetodosPago.setOnClickListener(v -> startActivity(new Intent(this, ClienteTusTarjetasActivity.class)));
        }
        if (layoutSeguridad != null) {
            layoutSeguridad.setOnClickListener(v -> startActivity(new Intent(this, ClienteSeguridadActivity.class)));
        }
        if (layoutNotificaciones != null) {
            layoutNotificaciones.setOnClickListener(v -> startActivity(new Intent(this, ClienteConfigNotificacionesActivity.class)));
        }
        if (layoutCambiarPassword != null) {
            layoutCambiarPassword.setOnClickListener(v -> startActivity(new Intent(this, ClienteCambiarPasswordActivity.class)));        }
        if (layoutCerrarSesion != null) {
            layoutCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());
        }
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
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_perfil);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    startActivity(new Intent(this, ClienteHomeActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_citas) {
                    startActivity(new Intent(this, ClienteCitasActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(this, ClienteMensajesActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_perfil) {
                    return true;
                } else if (id == R.id.nav_separaciones) {
                    startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}
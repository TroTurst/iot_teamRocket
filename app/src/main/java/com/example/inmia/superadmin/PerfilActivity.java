package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.LoginActivity;
import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private LinearLayout layoutCerrarSesion;
    private LinearLayout layoutCambiarPassword;
    private LinearLayout layoutNotificaciones;

    private TextView tvAvatar;
    private TextView tvNombreUsuario;
    private TextView tvNombre;
    private TextView tvCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_perfil);

        bottomNav             = findViewById(R.id.bottomNavSuperAdmin);
        layoutCerrarSesion    = findViewById(R.id.layoutCerrarSesion);
        layoutCambiarPassword = findViewById(R.id.layoutCambiarPassword);
        layoutNotificaciones  = findViewById(R.id.layoutNotificaciones);
        tvAvatar              = findViewById(R.id.tvAvatar);
        tvNombreUsuario       = findViewById(R.id.tvNombreUsuario);
        tvNombre              = findViewById(R.id.tvNombre);
        tvCorreo              = findViewById(R.id.tvCorreo);

        cargarDatosDesdeFirestore();

        // Cerrar sesión — AlertDialog de confirmación
        layoutCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());

        // Cambiar contraseña
        layoutCambiarPassword.setOnClickListener(v ->
                Toast.makeText(this, "Cambiar contraseña", Toast.LENGTH_SHORT).show());

        // Notificaciones
        layoutNotificaciones.setOnClickListener(v ->
                Toast.makeText(this, "Configurar notificaciones", Toast.LENGTH_SHORT).show());

        // Bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_perfil);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, SuperAdminHomeActivity.class));
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, GestionUsuariosActivity.class));
                return true;
            } else if (id == R.id.nav_reportes) {
                startActivity(new Intent(this, ReportesActivity.class));
                return true;
            } else if (id == R.id.nav_logs) {
                startActivity(new Intent(this, LogsActivity.class));
                return true;
            } else if (id == R.id.nav_perfil) {
                return true;
            }

            return false;
        });
    }

    private void cargarDatosDesdeFirestore() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener(doc -> {
                if (!doc.exists()) return;

                String nombres   = doc.getString("nombres");
                String apellidos = doc.getString("apellidos");
                if (nombres == null)   nombres   = "";
                if (apellidos == null) apellidos = "";

                String nombreCompleto = (nombres + " " + apellidos).trim();
                if (nombreCompleto.isEmpty()) nombreCompleto = "Superadmin";

                String iniciales = "";
                if (!nombres.isEmpty())   iniciales += Character.toUpperCase(nombres.charAt(0));
                if (!apellidos.isEmpty()) iniciales += Character.toUpperCase(apellidos.charAt(0));
                if (iniciales.isEmpty())  iniciales = "SA";

                String correo = doc.getString("correo");
                if (correo == null) correo = "";

                tvAvatar.setText(iniciales);
                tvNombreUsuario.setText(nombreCompleto);
                tvNombre.setText(nombreCompleto);
                tvCorreo.setText(correo);
            });
    }

    private void mostrarDialogoCerrarSesion() {
        DialogHelper.mostrarDialogoAccion(
            this,
            "Cerrar sesión",
            "¿Estás seguro que deseas cerrar sesión?",
            "Cerrar sesión",
            "Cancelar",
            R.color.inmia_danger,
            R.drawable.bg_badge_red_circle,
            () -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        );
    }
}
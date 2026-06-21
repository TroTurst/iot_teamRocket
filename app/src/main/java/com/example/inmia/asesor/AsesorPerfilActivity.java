package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inmia.LoginActivity;
import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import android.content.res.ColorStateList;

import java.util.List;
import java.util.Map;

public class AsesorPerfilActivity extends AppCompatActivity {

    private View btnBackPerfil;
    private LinearLayout layoutCerrarSesion;
    private LinearLayout layoutCambiarPassword;
    private LinearLayout layoutNotificaciones;
    private TextView tvNombreUsuario;
    private TextView tvRol;
    private TextView tvAvatar;
    private TextView tvCorreo;
    private TextView tvTelefono;
    private TextView tvOficina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_asesor_perfil);

        btnBackPerfil         = findViewById(R.id.btnBackPerfil);
        layoutCerrarSesion    = findViewById(R.id.layoutCerrarSesion);
        layoutCambiarPassword = findViewById(R.id.layoutCambiarPassword);
        layoutNotificaciones  = findViewById(R.id.layoutNotificaciones);
        tvNombreUsuario       = findViewById(R.id.tvNombreUsuario);
        tvRol                 = findViewById(R.id.tvRol);
        tvAvatar              = findViewById(R.id.tvAvatar);
        tvCorreo              = findViewById(R.id.tvCorreo);
        tvTelefono            = findViewById(R.id.tvTelefono);
        tvOficina             = findViewById(R.id.tvOficina);

        if (btnBackPerfil != null) btnBackPerfil.setOnClickListener(v -> finish());

        layoutCambiarPassword.setOnClickListener(v ->
            Toast.makeText(this, "Cambiar contraseña", Toast.LENGTH_SHORT).show());

        layoutNotificaciones.setOnClickListener(v ->
            Toast.makeText(this, "Configurar notificaciones", Toast.LENGTH_SHORT).show());

        layoutCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());

        cargarPerfilFirestore();
    }

    @SuppressWarnings("unchecked")
    private void cargarPerfilFirestore() {
        AsesorFirestoreRepository repo = AsesorFirestoreRepository.get();

        repo.getPerfil(data -> {
            // En Firestore el campo es "nombres" (no "nombre")
            String nombres        = getStr(data, "nombres", "Asesor");
            String correo         = getStr(data, "correo",
                FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getEmail() : "—");
            String telefono       = getStr(data, "telefono", "—");
            String rol            = getStr(data, "rol", "asesor");
            String inmobiliariaId = getStr(data, "inmobiliariaId", "");

            // Días de atención: [2, 4, 6] → "Lun, Mié, Vie"
            List<Object> dias = data != null ? (List<Object>) data.get("diasAtencion") : null;
            String diasStr = diasToString(dias);

            setText(tvNombreUsuario, nombres);
            setText(tvRol,           capitalizar(rol));
            setText(tvCorreo,        correo);
            setText(tvTelefono,      telefono.equals("—") ? diasStr : telefono);
            setText(tvAvatar,        iniciales(nombres));

            // Obtener el nombre de la inmobiliaria desde Firestore
            if (!inmobiliariaId.isEmpty()) {
                repo.getInmobiliaria(inmobiliariaId, inmData -> {
                    String nombreInmob = getStr(inmData, "nombre", inmobiliariaId);
                    setText(tvOficina, nombreInmob);
                });
            } else {
                setText(tvOficina, "—");
            }
        });
    }

    private String getStr(Map<String, Object> data, String key, String def) {
        if (data == null) return def;
        Object val = data.get(key);
        if (val == null || val.toString().trim().isEmpty()) return def;
        return val.toString().trim();
    }

    private void setText(TextView tv, String value) {
        if (tv != null) tv.setText(value != null ? value : "—");
    }

    private String iniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) return String.valueOf(partes[0].charAt(0)).toUpperCase();
        return (String.valueOf(partes[0].charAt(0)) + String.valueOf(partes[1].charAt(0))).toUpperCase();
    }

    private String capitalizar(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    private String diasToString(List<Object> dias) {
        if (dias == null || dias.isEmpty()) return "—";
        String[] nombres = {"", "Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
        StringBuilder sb = new StringBuilder();
        for (Object d : dias) {
            int num = -1;
            try { num = ((Number) d).intValue(); } catch (Exception ignored) {}
            if (num >= 0 && num < nombres.length) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(nombres[num]);
            }
        }
        return sb.length() > 0 ? sb.toString() : "—";
    }

    private void mostrarDialogoCerrarSesion() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_eliminar_chat, null);
        TextView tvTitle    = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvMsg      = dialogView.findViewById(R.id.tvDialogMessage);
        MaterialButton btnC = dialogView.findViewById(R.id.btnCancelarDialogo);
        MaterialButton btnOk= dialogView.findViewById(R.id.btnEliminarDialogo);
        View frameIcon      = dialogView.findViewById(R.id.frameDialogIcon);
        ImageView imgIcon   = dialogView.findViewById(R.id.imgDialogIcon);

        tvTitle.setText("Cerrar sesión");
        tvMsg.setText("Estas seguro que deseas cerrar sesión?");
        btnC.setText("Cancelar");
        btnOk.setText("Cerrar sesión");
        btnOk.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.inmia_danger)));
        if (frameIcon != null) frameIcon.setBackgroundResource(R.drawable.bg_badge_red_circle);
        if (imgIcon   != null) imgIcon.setImageResource(android.R.drawable.ic_dialog_alert);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
            .setView(dialogView).setCancelable(true).create();

        btnC.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });

        dialog.show();
    }
}

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

import android.content.res.ColorStateList;

public class AsesorPerfilActivity extends AppCompatActivity {

    private View btnBackPerfil;
    private LinearLayout layoutCerrarSesion;
    private LinearLayout layoutCambiarPassword;
    private LinearLayout layoutNotificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_perfil);

        // Vincular vistas
        btnBackPerfil         = findViewById(R.id.btnBackPerfil);
        layoutCerrarSesion    = findViewById(R.id.layoutCerrarSesion);
        layoutCambiarPassword = findViewById(R.id.layoutCambiarPassword);
        layoutNotificaciones  = findViewById(R.id.layoutNotificaciones);

        if (btnBackPerfil != null) {
            btnBackPerfil.setOnClickListener(v -> finish());
        }

        // Cambiar contraseña
        layoutCambiarPassword.setOnClickListener(v ->
                Toast.makeText(this, "Cambiar contraseña",
                        Toast.LENGTH_SHORT).show());

        // Notificaciones
        layoutNotificaciones.setOnClickListener(v ->
                Toast.makeText(this, "Configurar notificaciones",
                        Toast.LENGTH_SHORT).show());

        // Cerrar sesión
        layoutCerrarSesion.setOnClickListener(v ->
                mostrarDialogoCerrarSesion());
    }

    private void mostrarDialogoCerrarSesion() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_eliminar_chat, null);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvDialogMessage = dialogView.findViewById(R.id.tvDialogMessage);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelarDialogo);
        MaterialButton btnConfirmar = dialogView.findViewById(R.id.btnEliminarDialogo);
        View frameIcon = dialogView.findViewById(R.id.frameDialogIcon);
        ImageView imgIcon = dialogView.findViewById(R.id.imgDialogIcon);

        tvDialogTitle.setText("Cerrar sesión");
        tvDialogMessage.setText("Estas seguro que deseas cerrar sesión?");
        btnCancelar.setText("Cancelar");
        btnConfirmar.setText("Cerrar sesión");
        btnConfirmar.setBackgroundTintList(ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.inmia_danger)
        ));

        if (frameIcon != null) {
            frameIcon.setBackgroundResource(R.drawable.bg_badge_red_circle);
        }
        if (imgIcon != null) {
            imgIcon.setImageResource(android.R.drawable.ic_dialog_alert);
        }

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        btnConfirmar.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });

        dialog.show();
    }

}
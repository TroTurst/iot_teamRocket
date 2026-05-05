package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.content.res.ColorStateList;

public class AsesorSeparacionDetailActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private TextView tvEstadoDetalle;
    private TextView btnCancelarSeparacion;
    private FrameLayout framePerfil;
    private TextView tvClienteSeparacionDetalle;

    private int totalNotificaciones = 2;
    private boolean separacionCancelada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_separacion_detail);

        bottomNav = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        tvEstadoDetalle = findViewById(R.id.tvEstadoSeparacionDetalle);
        btnCancelarSeparacion = findViewById(R.id.btnCancelarSeparacion);
        framePerfil = findViewById(R.id.framePerfil);
        tvClienteSeparacionDetalle = findViewById(R.id.tvClienteSeparacionDetalle);

        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        bottomNav.setSelectedItemId(R.id.nav_separaciones);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, AsesorHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, AsesorChatActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(this, AsesorCitasActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, AsesorSeparacionesActivity.class));
                finish();
                return true;
            }

            return false;
        });
    }

    public void onHablarCliente(android.view.View view) {
        String cliente = obtenerNombreCliente();
        Intent intent = new Intent(this, AsesorChatDetailActivity.class);
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_NAME, cliente);
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_ID, "chat_" + cliente.toLowerCase().replace(" ", "_"));
        startActivity(intent);
    }

    public void onCancelarSeparacion(android.view.View view) {
        if (separacionCancelada) {
            return;
        }
        mostrarDialogoAccion(
            "Cancelar separacion",
            "Estas seguro de cancelar la separacion del inmueble?",
            "Cancelar",
            "Volver",
            R.color.inmia_danger,
            R.drawable.bg_badge_red_circle,
            () -> {
                separacionCancelada = true;
                tvEstadoDetalle.setText("Cancelada");
                tvEstadoDetalle.setTextColor(ContextCompat.getColor(this, R.color.inmia_danger));
                btnCancelarSeparacion.setText("Cancelada");
                btnCancelarSeparacion.setEnabled(false);
                btnCancelarSeparacion.setAlpha(0.6f);
            }
        );
    }

    private String obtenerNombreCliente() {
        if (tvClienteSeparacionDetalle == null) {
            return "Cliente";
        }
        String raw = tvClienteSeparacionDetalle.getText() == null ? "" : tvClienteSeparacionDetalle.getText().toString().trim();
        if (raw.isEmpty()) {
            return "Cliente";
        }
        return raw.replace("Cliente:", "").trim();
    }

    private void mostrarDialogoAccion(
        String titulo,
        String mensaje,
        String textoConfirmar,
        String textoCancelar,
        int colorConfirmarRes,
        int iconoFondoRes,
        Runnable onConfirmar
    ) {
        android.view.View dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_confirmar_eliminar_chat, null);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvDialogMessage = dialogView.findViewById(R.id.tvDialogMessage);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnCancelarDialogo);
        MaterialButton btnConfirmar = dialogView.findViewById(R.id.btnEliminarDialogo);
        FrameLayout frameIcon = dialogView.findViewById(R.id.frameDialogIcon);
        ImageView imgIcon = dialogView.findViewById(R.id.imgDialogIcon);

        tvDialogTitle.setText(titulo);
        tvDialogMessage.setText(mensaje);
        btnCancelar.setText(textoCancelar);
        btnConfirmar.setText(textoConfirmar);
        btnConfirmar.setBackgroundTintList(ColorStateList.valueOf(
            ContextCompat.getColor(this, colorConfirmarRes)
        ));

        if (frameIcon != null) {
            frameIcon.setBackgroundResource(iconoFondoRes);
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
            if (onConfirmar != null) {
                onConfirmar.run();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void configurarBadge() {
        if (totalNotificaciones > 0) {
            tvBadgeNotif.setText(String.valueOf(totalNotificaciones));
            tvBadgeNotif.setVisibility(android.view.View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(android.view.View.GONE);
        }
    }

    private void limpiarBadge() {
        totalNotificaciones = 0;
        tvBadgeNotif.setVisibility(android.view.View.GONE);
    }
}

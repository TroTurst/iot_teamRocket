package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.content.res.ColorStateList;

public class AsesorCitaDetailActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private FrameLayout framePerfil;
    private TextView tvClienteCitaDetalle;
    private TextView btnCancelarCita;

    private int totalNotificaciones = 2;
    private boolean citaCancelada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_cita_detail);

        bottomNav = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        framePerfil = findViewById(R.id.framePerfil);
        tvClienteCitaDetalle = findViewById(R.id.tvClienteCitaDetalle);
        btnCancelarCita = findViewById(R.id.btnCancelarCita);

        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        bottomNav.setSelectedItemId(R.id.nav_citas);
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

    public void onHablarCliente(View view) {
        String cliente = obtenerNombreCliente();
        Intent intent = new Intent(this, AsesorChatDetailActivity.class);
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_NAME, cliente);
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_ID, "chat_" + cliente.toLowerCase().replace(" ", "_"));
        startActivity(intent);
    }

    public void onCancelarCita(View view) {
        if (citaCancelada) {
            return;
        }
        mostrarDialogoAccion(
            "Cancelar cita",
            "Estas seguro de cancelar la cita con el cliente?",
            "Cancelar",
            "Volver",
            R.color.inmia_danger,
            R.drawable.bg_badge_red_circle,
            () -> {
                citaCancelada = true;
                if (btnCancelarCita != null) {
                    btnCancelarCita.setText("Cancelada");
                    btnCancelarCita.setEnabled(false);
                    btnCancelarCita.setAlpha(0.6f);
                }
                Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show();
            }
        );
    }

    private String obtenerNombreCliente() {
        if (tvClienteCitaDetalle == null) {
            return "Cliente";
        }
        String raw = tvClienteCitaDetalle.getText() == null ? "" : tvClienteCitaDetalle.getText().toString().trim();
        if (raw.isEmpty()) {
            return "Cliente";
        }
        return raw;
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
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_eliminar_chat, null);

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
            tvBadgeNotif.setVisibility(View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(View.GONE);
        }
    }

    private void limpiarBadge() {
        totalNotificaciones = 0;
        tvBadgeNotif.setVisibility(View.GONE);
    }
}

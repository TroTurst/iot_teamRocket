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

    public static final String EXTRA_CITA_KEY = "extra_cita_key";
    public static final String EXTRA_CITA_CLIENTE = "extra_cita_cliente";
    public static final String EXTRA_CITA_PROYECTO = "extra_cita_proyecto";
    public static final String EXTRA_CITA_ESTADO = "extra_cita_estado";
    public static final String EXTRA_CITA_CONFIRMADA = "extra_cita_confirmada";

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private FrameLayout framePerfil;
    private TextView tvEstadoCitaDetalle;
    private TextView tvProyectoCitaDetalle;
    private TextView tvUbicacionCitaDetalle;
    private TextView tvFechaCitaDetalle;
    private TextView tvProyectoGuiaCitaDetalle;
    private TextView tvUbicacionGuiaCitaDetalle;
    private TextView tvEmpresaCitaDetalle;
    private TextView tvFechaReservaCitaDetalle;
    private TextView tvClienteReservaCitaDetalle;
    private TextView tvDepartamentoReservaCitaDetalle;
    private TextView tvTelefonoReservaCitaDetalle;
    private TextView tvClienteCitaDetalle;
    private TextView btnCancelarCita;

    private boolean citaCancelada = false;
    private String citaKey;

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
        tvEstadoCitaDetalle = findViewById(R.id.tvEstadoCitaDetalle);
        tvProyectoCitaDetalle = findViewById(R.id.tvProyectoCitaDetalle);
        tvUbicacionCitaDetalle = findViewById(R.id.tvUbicacionCitaDetalle);
        tvFechaCitaDetalle = findViewById(R.id.tvFechaCitaDetalle);
        tvProyectoGuiaCitaDetalle = findViewById(R.id.tvProyectoGuiaCitaDetalle);
        tvUbicacionGuiaCitaDetalle = findViewById(R.id.tvUbicacionGuiaCitaDetalle);
        tvEmpresaCitaDetalle = findViewById(R.id.tvEmpresaCitaDetalle);
        tvFechaReservaCitaDetalle = findViewById(R.id.tvFechaReservaCitaDetalle);
        tvClienteReservaCitaDetalle = findViewById(R.id.tvClienteReservaCitaDetalle);
        tvDepartamentoReservaCitaDetalle = findViewById(R.id.tvDepartamentoReservaCitaDetalle);
        tvTelefonoReservaCitaDetalle = findViewById(R.id.tvTelefonoReservaCitaDetalle);
        tvClienteCitaDetalle = findViewById(R.id.tvClienteCitaDetalle);
        btnCancelarCita = findViewById(R.id.btnCancelarCita);

        AsesorCitaStore.seedIfEmpty(this);
        AsesorNotificacionStore.seedIfEmpty(this);
        cargarDatosCita();
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
                if (citaKey != null) {
                    AsesorCitaStore.updateStatus(this, citaKey, "Cancelada", false);
                }
                if (btnCancelarCita != null) {
                    btnCancelarCita.setText("Cancelada");
                    btnCancelarCita.setEnabled(false);
                    btnCancelarCita.setAlpha(0.6f);
                }
                AsesorNotificacionHelper.enviar(
                    this,
                    "Cita cancelada",
                    "Se cancelo la cita con " + obtenerNombreCliente(),
                    AsesorNotificacionStore.TIPO_CITA_CANCELADA,
                    AsesorNotificacionStore.TARGET_CITA_DETAIL,
                    citaKey
                );
                configurarBadge();
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

    private void cargarDatosCita() {
        String cliente = getIntent().getStringExtra(EXTRA_CITA_CLIENTE);
        String proyecto = getIntent().getStringExtra(EXTRA_CITA_PROYECTO);
        citaKey = getIntent().getStringExtra(EXTRA_CITA_KEY);

        if ((citaKey == null || citaKey.trim().isEmpty()) && cliente != null && proyecto != null) {
            citaKey = AsesorCitaStore.buildKey(cliente, proyecto);
        }

        AsesorCitaStore.CitaRecord record = AsesorCitaStore.getRecordByKey(this, citaKey);
        if (record != null) {
            if (tvClienteCitaDetalle != null) {
                tvClienteCitaDetalle.setText(record.client);
            }
            if (tvEstadoCitaDetalle != null) {
                tvEstadoCitaDetalle.setText(record.status);
                AsesorEstadoBadgeStyle.apply(this, tvEstadoCitaDetalle, record.status);
            }
            if (tvProyectoCitaDetalle != null) {
                tvProyectoCitaDetalle.setText(record.project);
            }
            if (tvUbicacionCitaDetalle != null) {
                tvUbicacionCitaDetalle.setText(record.location);
            }
            if (tvFechaCitaDetalle != null) {
                tvFechaCitaDetalle.setText("Fecha " + record.date.replace('-', '/') + " - " + record.time + record.meridian);
            }
            if (tvProyectoGuiaCitaDetalle != null) {
                tvProyectoGuiaCitaDetalle.setText(record.project);
            }
            if (tvUbicacionGuiaCitaDetalle != null) {
                tvUbicacionGuiaCitaDetalle.setText(record.location);
            }
            if (tvEmpresaCitaDetalle != null) {
                tvEmpresaCitaDetalle.setText(record.company);
            }
            if (tvFechaReservaCitaDetalle != null) {
                tvFechaReservaCitaDetalle.setText(record.date.replace('-', '/'));
            }
            if (tvClienteReservaCitaDetalle != null) {
                tvClienteReservaCitaDetalle.setText(record.client);
            }
            if (tvDepartamentoReservaCitaDetalle != null) {
                tvDepartamentoReservaCitaDetalle.setText(record.project);
            }
            if (tvTelefonoReservaCitaDetalle != null) {
                tvTelefonoReservaCitaDetalle.setText(buildPhoneFromKey(citaKey));
            }
            citaCancelada = "Cancelada".equalsIgnoreCase(record.status);
            if (citaCancelada && btnCancelarCita != null) {
                btnCancelarCita.setText("Cancelada");
                btnCancelarCita.setEnabled(false);
                btnCancelarCita.setAlpha(0.6f);
            }
        } else if (cliente != null && tvClienteCitaDetalle != null) {
            tvClienteCitaDetalle.setText(cliente);
        }
    }

    private String buildPhoneFromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return "000000000";
        }
        int hash = Math.abs(key.hashCode());
        return String.format(java.util.Locale.US, "9%08d", hash % 100000000);
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
        int totalNotificaciones = AsesorNotificacionStore.getBadgeCount(this);
        if (totalNotificaciones > 0) {
            tvBadgeNotif.setText(String.valueOf(totalNotificaciones));
            tvBadgeNotif.setVisibility(View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(View.GONE);
        }
    }

    private void limpiarBadge() {
        AsesorNotificacionStore.clearBadge(this);
        tvBadgeNotif.setVisibility(View.GONE);
    }
}

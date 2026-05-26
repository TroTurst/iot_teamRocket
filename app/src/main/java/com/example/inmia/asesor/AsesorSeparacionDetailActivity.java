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

    public static final String EXTRA_SEPARACION_KEY = "extra_separacion_key";
    public static final String EXTRA_SEPARACION_CLIENTE = "extra_separacion_cliente";
    public static final String EXTRA_SEPARACION_PROYECTO = "extra_separacion_proyecto";
    public static final String EXTRA_SEPARACION_ESTADO = "extra_separacion_estado";
    public static final String EXTRA_SEPARACION_CONFIRMADA = "extra_separacion_confirmada";

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private TextView tvEstadoDetalle;
    private TextView tvEstadoSeparacionDetalle;
    private TextView tvProyectoSeparacionDetalle;
    private TextView tvUbicacionSeparacionDetalle;
    private TextView tvEmpresaSeparacionDetalle;
    private TextView tvFechaSeparacionDetalle;
    private TextView tvClienteReservaSeparacionDetalle;
    private TextView tvDepartamentoReservaSeparacionDetalle;
    private TextView tvTelefonoReservaSeparacionDetalle;
    private TextView btnCancelarSeparacion;
    private FrameLayout framePerfil;
    private TextView tvClienteSeparacionDetalle;

    private boolean separacionCancelada = false;
    private String separacionKey;

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
        tvEstadoSeparacionDetalle = findViewById(R.id.tvEstadoSeparacionDetalle);
        tvProyectoSeparacionDetalle = findViewById(R.id.tvProyectoSeparacionDetalle);
        tvUbicacionSeparacionDetalle = findViewById(R.id.tvUbicacionSeparacionDetalle);
        tvEmpresaSeparacionDetalle = findViewById(R.id.tvEmpresaSeparacionDetalle);
        tvFechaSeparacionDetalle = findViewById(R.id.tvFechaSeparacionDetalle);
        tvClienteReservaSeparacionDetalle = findViewById(R.id.tvClienteReservaSeparacionDetalle);
        tvDepartamentoReservaSeparacionDetalle = findViewById(R.id.tvDepartamentoReservaSeparacionDetalle);
        tvTelefonoReservaSeparacionDetalle = findViewById(R.id.tvTelefonoReservaSeparacionDetalle);
        btnCancelarSeparacion = findViewById(R.id.btnCancelarSeparacion);
        framePerfil = findViewById(R.id.framePerfil);
        tvClienteSeparacionDetalle = findViewById(R.id.tvClienteSeparacionDetalle);

        AsesorSeparacionStore.seedIfEmpty(this);
        AsesorNotificacionStore.seedIfEmpty(this);
        cargarDatosSeparacion();
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
                if (separacionKey != null) {
                    AsesorSeparacionStore.updateStatus(this, separacionKey, "Cancelada", false);
                }
                tvEstadoDetalle.setText("Cancelada");
                tvEstadoDetalle.setTextColor(ContextCompat.getColor(this, R.color.inmia_danger));
                btnCancelarSeparacion.setText("Cancelada");
                btnCancelarSeparacion.setEnabled(false);
                btnCancelarSeparacion.setAlpha(0.6f);
                AsesorNotificacionHelper.enviar(
                    this,
                    "Separacion cancelada",
                    "Se cancelo la separacion de " + obtenerNombreCliente(),
                    AsesorNotificacionStore.TIPO_SEPARACION_CANCELADA,
                    AsesorNotificacionStore.TARGET_SEPARACION_DETAIL,
                    separacionKey
                );
                configurarBadge();
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

    private void cargarDatosSeparacion() {
        String cliente = getIntent().getStringExtra(EXTRA_SEPARACION_CLIENTE);
        String proyecto = getIntent().getStringExtra(EXTRA_SEPARACION_PROYECTO);
        separacionKey = getIntent().getStringExtra(EXTRA_SEPARACION_KEY);

        if ((separacionKey == null || separacionKey.trim().isEmpty()) && proyecto != null) {
            separacionKey = proyecto.trim().toLowerCase().replace(' ', '_');
        }

        AsesorSeparacionStore.SeparacionRecord record = AsesorSeparacionStore.getRecordByKey(this, separacionKey);
        if (record != null) {
            if (tvEstadoSeparacionDetalle != null) {
                tvEstadoSeparacionDetalle.setText(record.status);
                AsesorEstadoBadgeStyle.apply(this, tvEstadoSeparacionDetalle, record.status);
            }
            if (tvProyectoSeparacionDetalle != null) {
                tvProyectoSeparacionDetalle.setText(record.project);
            }
            if (tvUbicacionSeparacionDetalle != null) {
                tvUbicacionSeparacionDetalle.setText(record.location);
            }
            if (tvEmpresaSeparacionDetalle != null) {
                tvEmpresaSeparacionDetalle.setText(record.company);
            }
            if (tvClienteSeparacionDetalle != null) {
                tvClienteSeparacionDetalle.setText("Cliente: " + record.client);
            }
            if (tvFechaSeparacionDetalle != null) {
                tvFechaSeparacionDetalle.setText(buildDateFromKey(record.key));
            }
            if (tvClienteReservaSeparacionDetalle != null) {
                tvClienteReservaSeparacionDetalle.setText(record.client);
            }
            if (tvDepartamentoReservaSeparacionDetalle != null) {
                tvDepartamentoReservaSeparacionDetalle.setText(record.project);
            }
            if (tvTelefonoReservaSeparacionDetalle != null) {
                tvTelefonoReservaSeparacionDetalle.setText(buildPhoneFromKey(record.key));
            }
            separacionCancelada = "Cancelada".equalsIgnoreCase(record.status);
            if (separacionCancelada) {
                tvEstadoDetalle.setText("Cancelada");
                tvEstadoDetalle.setTextColor(ContextCompat.getColor(this, R.color.inmia_danger));
                btnCancelarSeparacion.setText("Cancelada");
                btnCancelarSeparacion.setEnabled(false);
                btnCancelarSeparacion.setAlpha(0.6f);
            }
        } else if (cliente != null && tvClienteSeparacionDetalle != null) {
            tvClienteSeparacionDetalle.setText("Cliente: " + cliente);
        }
    }

    private String buildDateFromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return "--/--/----";
        }
        int day = Math.abs(key.hashCode() % 28) + 1;
        int month = Math.abs((key.hashCode() / 31) % 12) + 1;
        return String.format(java.util.Locale.US, "%02d/%02d/2026", day, month);
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
        int totalNotificaciones = AsesorNotificacionStore.getBadgeCount(this);
        if (totalNotificaciones > 0) {
            tvBadgeNotif.setText(String.valueOf(totalNotificaciones));
            tvBadgeNotif.setVisibility(android.view.View.VISIBLE);
        } else {
            tvBadgeNotif.setVisibility(android.view.View.GONE);
        }
    }

    private void limpiarBadge() {
        AsesorNotificacionStore.clearBadge(this);
        tvBadgeNotif.setVisibility(android.view.View.GONE);
    }
}

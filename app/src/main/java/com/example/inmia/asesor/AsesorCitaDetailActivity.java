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

    public static final String EXTRA_CITA_KEY          = "extra_cita_key";
    public static final String EXTRA_CITA_CLIENTE      = "extra_cita_cliente";
    public static final String EXTRA_CITA_PROYECTO     = "extra_cita_proyecto";
    public static final String EXTRA_CITA_ESTADO       = "extra_cita_estado";
    public static final String EXTRA_CITA_CONFIRMADA   = "extra_cita_confirmada";
    public static final String EXTRA_CITA_UBICACION    = "extra_cita_ubicacion";
    public static final String EXTRA_CITA_FECHA        = "extra_cita_fecha";
    public static final String EXTRA_CITA_INMOBILIARIA = "extra_cita_inmobiliaria";

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
    private String citaDocId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_asesor_cita_detail);

        bottomNav                      = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones            = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif                   = findViewById(R.id.tvBadgeNotif);
        framePerfil                    = findViewById(R.id.framePerfil);
        tvEstadoCitaDetalle            = findViewById(R.id.tvEstadoCitaDetalle);
        tvProyectoCitaDetalle          = findViewById(R.id.tvProyectoCitaDetalle);
        tvUbicacionCitaDetalle         = findViewById(R.id.tvUbicacionCitaDetalle);
        tvFechaCitaDetalle             = findViewById(R.id.tvFechaCitaDetalle);
        tvProyectoGuiaCitaDetalle      = findViewById(R.id.tvProyectoGuiaCitaDetalle);
        tvUbicacionGuiaCitaDetalle     = findViewById(R.id.tvUbicacionGuiaCitaDetalle);
        tvEmpresaCitaDetalle           = findViewById(R.id.tvEmpresaCitaDetalle);
        tvFechaReservaCitaDetalle      = findViewById(R.id.tvFechaReservaCitaDetalle);
        tvClienteReservaCitaDetalle    = findViewById(R.id.tvClienteReservaCitaDetalle);
        tvDepartamentoReservaCitaDetalle = findViewById(R.id.tvDepartamentoReservaCitaDetalle);
        tvTelefonoReservaCitaDetalle   = findViewById(R.id.tvTelefonoReservaCitaDetalle);
        tvClienteCitaDetalle           = findViewById(R.id.tvClienteCitaDetalle);
        btnCancelarCita                = findViewById(R.id.btnCancelarCita);

        AsesorNotificacionStore.seedIfEmpty(this);
        cargarDatosCita();
        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });
        framePerfil.setOnClickListener(v -> startActivity(new Intent(this, AsesorPerfilActivity.class)));

        bottomNav.setSelectedItemId(R.id.nav_citas);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, AsesorHomeActivity.class)); finish(); return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, AsesorChatActivity.class)); finish(); return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(this, AsesorCitasActivity.class)); finish(); return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, AsesorSeparacionesActivity.class)); finish(); return true;
            }
            return false;
        });
    }

    public void onHablarCliente(View view) {
        String cliente = obtenerNombreCliente();
        Intent intent = new Intent(this, AsesorChatDetailActivity.class);
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_NAME, cliente);
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_ID, "");
        startActivity(intent);
    }

    public void onCancelarCita(View view) {
        if (citaCancelada) return;
        mostrarDialogoAccion(
            "Cancelar cita",
            "Estas seguro de cancelar la cita con el cliente?",
            "Cancelar", "Volver",
            R.color.inmia_danger, R.drawable.bg_badge_red_circle,
            () -> {
                citaCancelada = true;
                AsesorFirestoreRepository.get().updateCitaEstado(citaDocId, "cancelada");
                if (btnCancelarCita != null) {
                    btnCancelarCita.setText("Cancelada");
                    btnCancelarCita.setEnabled(false);
                    btnCancelarCita.setAlpha(0.6f);
                }
                if (tvEstadoCitaDetalle != null) {
                    tvEstadoCitaDetalle.setText("Cancelada");
                    AsesorEstadoBadgeStyle.apply(this, tvEstadoCitaDetalle, "Cancelada");
                }
                AsesorNotificacionHelper.enviar(
                    this, "Cita cancelada",
                    "Se cancelo la cita con " + obtenerNombreCliente(),
                    AsesorNotificacionStore.TIPO_CITA_CANCELADA,
                    AsesorNotificacionStore.TARGET_CITA_DETAIL,
                    citaDocId
                );
                configurarBadge();
                Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show();
            }
        );
    }

    private String obtenerNombreCliente() {
        if (tvClienteCitaDetalle == null) return "Cliente";
        String raw = tvClienteCitaDetalle.getText() == null ? "" : tvClienteCitaDetalle.getText().toString().trim();
        return raw.isEmpty() ? "Cliente" : raw;
    }

    private void cargarDatosCita() {
        citaDocId         = getIntent().getStringExtra(EXTRA_CITA_KEY);
        String cliente    = getIntent().getStringExtra(EXTRA_CITA_CLIENTE);
        String proyecto   = getIntent().getStringExtra(EXTRA_CITA_PROYECTO);
        String estado     = getIntent().getStringExtra(EXTRA_CITA_ESTADO);
        String ubicacion  = getIntent().getStringExtra(EXTRA_CITA_UBICACION);
        String fecha      = getIntent().getStringExtra(EXTRA_CITA_FECHA);
        String empresa    = getIntent().getStringExtra(EXTRA_CITA_INMOBILIARIA);
        boolean confirmada= getIntent().getBooleanExtra(EXTRA_CITA_CONFIRMADA, false);

        if (cliente   == null) cliente   = "—";
        if (proyecto  == null) proyecto  = "—";
        if (estado    == null) estado    = "—";
        if (ubicacion == null) ubicacion = "—";
        if (fecha     == null) fecha     = "—";
        if (empresa   == null) empresa   = "—";

        setText(tvClienteCitaDetalle,           cliente);
        setText(tvEstadoCitaDetalle,            estado);
        setText(tvProyectoCitaDetalle,          proyecto);
        setText(tvUbicacionCitaDetalle,         ubicacion);
        setText(tvFechaCitaDetalle,             fecha);
        setText(tvProyectoGuiaCitaDetalle,      proyecto);
        setText(tvUbicacionGuiaCitaDetalle,     ubicacion);
        setText(tvEmpresaCitaDetalle,           empresa);
        setText(tvFechaReservaCitaDetalle,      fecha);
        setText(tvClienteReservaCitaDetalle,    cliente);
        setText(tvDepartamentoReservaCitaDetalle, ubicacion);
        setText(tvTelefonoReservaCitaDetalle,   "—");

        if (tvEstadoCitaDetalle != null) {
            AsesorEstadoBadgeStyle.apply(this, tvEstadoCitaDetalle, estado);
        }

        citaCancelada = "Cancelada".equalsIgnoreCase(estado);
        if (citaCancelada && btnCancelarCita != null) {
            btnCancelarCita.setText("Cancelada");
            btnCancelarCita.setEnabled(false);
            btnCancelarCita.setAlpha(0.6f);
        }
    }

    private void setText(TextView tv, String value) {
        if (tv != null) tv.setText(value);
    }

    private void mostrarDialogoAccion(String titulo, String mensaje, String textoConfirmar,
            String textoCancelar, int colorConfirmarRes, int iconoFondoRes, Runnable onConfirmar) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_eliminar_chat, null);
        TextView tvTitle   = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvMsg     = dialogView.findViewById(R.id.tvDialogMessage);
        MaterialButton btnC= dialogView.findViewById(R.id.btnCancelarDialogo);
        MaterialButton btnOk=dialogView.findViewById(R.id.btnEliminarDialogo);
        FrameLayout frameIcon = dialogView.findViewById(R.id.frameDialogIcon);
        ImageView imgIcon  = dialogView.findViewById(R.id.imgDialogIcon);

        tvTitle.setText(titulo);
        tvMsg.setText(mensaje);
        btnC.setText(textoCancelar);
        btnOk.setText(textoConfirmar);
        btnOk.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, colorConfirmarRes)));
        if (frameIcon != null) frameIcon.setBackgroundResource(iconoFondoRes);
        if (imgIcon   != null) imgIcon.setImageResource(android.R.drawable.ic_dialog_alert);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
            .setView(dialogView).setCancelable(true).create();

        btnC.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> { if (onConfirmar != null) onConfirmar.run(); dialog.dismiss(); });
        dialog.show();
    }

    private void configurarBadge() {
        int total = AsesorNotificacionStore.getBadgeCount(this);
        if (total > 0) {
            tvBadgeNotif.setText(String.valueOf(total));
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

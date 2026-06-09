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

    public static final String EXTRA_SEPARACION_KEY       = "extra_separacion_key";
    public static final String EXTRA_SEPARACION_CLIENTE   = "extra_separacion_cliente";
    public static final String EXTRA_SEPARACION_PROYECTO  = "extra_separacion_proyecto";
    public static final String EXTRA_SEPARACION_ESTADO    = "extra_separacion_estado";
    public static final String EXTRA_SEPARACION_CONFIRMADA= "extra_separacion_confirmada";
    public static final String EXTRA_SEPARACION_UBICACION = "extra_separacion_ubicacion";
    public static final String EXTRA_SEPARACION_EMPRESA   = "extra_separacion_empresa";
    public static final String EXTRA_SEPARACION_FECHA     = "extra_separacion_fecha";
    public static final String EXTRA_SEPARACION_TIPOLOGIA = "extra_separacion_tipologia";
    public static final String EXTRA_SEPARACION_MONTO     = "extra_separacion_monto";

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
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
    private String separacionDocId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_asesor_separacion_detail);

        bottomNav                            = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones                  = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif                         = findViewById(R.id.tvBadgeNotif);
        tvEstadoSeparacionDetalle            = findViewById(R.id.tvEstadoSeparacionDetalle);
        tvProyectoSeparacionDetalle          = findViewById(R.id.tvProyectoSeparacionDetalle);
        tvUbicacionSeparacionDetalle         = findViewById(R.id.tvUbicacionSeparacionDetalle);
        tvEmpresaSeparacionDetalle           = findViewById(R.id.tvEmpresaSeparacionDetalle);
        tvFechaSeparacionDetalle             = findViewById(R.id.tvFechaSeparacionDetalle);
        tvClienteReservaSeparacionDetalle    = findViewById(R.id.tvClienteReservaSeparacionDetalle);
        tvDepartamentoReservaSeparacionDetalle = findViewById(R.id.tvDepartamentoReservaSeparacionDetalle);
        tvTelefonoReservaSeparacionDetalle   = findViewById(R.id.tvTelefonoReservaSeparacionDetalle);
        btnCancelarSeparacion                = findViewById(R.id.btnCancelarSeparacion);
        framePerfil                          = findViewById(R.id.framePerfil);
        tvClienteSeparacionDetalle           = findViewById(R.id.tvClienteSeparacionDetalle);

        AsesorNotificacionStore.seedIfEmpty(this);
        cargarDatosSeparacion();
        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });
        framePerfil.setOnClickListener(v -> startActivity(new Intent(this, AsesorPerfilActivity.class)));

        bottomNav.setSelectedItemId(R.id.nav_separaciones);
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

    public void onHablarCliente(android.view.View view) {
        String cliente = obtenerNombreCliente();
        Intent intent = new Intent(this, AsesorChatDetailActivity.class);
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_NAME, cliente);
        intent.putExtra(AsesorChatDetailActivity.EXTRA_CHAT_ID, "");
        startActivity(intent);
    }

    public void onCancelarSeparacion(android.view.View view) {
        if (separacionCancelada) return;
        mostrarDialogoAccion(
            "Cancelar separacion",
            "Estas seguro de cancelar la separacion del inmueble?",
            "Cancelar", "Volver",
            R.color.inmia_danger, R.drawable.bg_badge_red_circle,
            () -> {
                separacionCancelada = true;
                AsesorFirestoreRepository.get().updateSeparacionEstado(separacionDocId, "cancelada");
                setText(tvEstadoSeparacionDetalle, "Cancelada");
                if (tvEstadoSeparacionDetalle != null)
                    AsesorEstadoBadgeStyle.apply(this, tvEstadoSeparacionDetalle, "Cancelada");
                if (btnCancelarSeparacion != null) {
                    btnCancelarSeparacion.setText("Cancelada");
                    btnCancelarSeparacion.setEnabled(false);
                    btnCancelarSeparacion.setAlpha(0.6f);
                }
                AsesorNotificacionHelper.enviar(
                    this, "Separacion cancelada",
                    "Se cancelo la separacion de " + obtenerNombreCliente(),
                    AsesorNotificacionStore.TIPO_SEPARACION_CANCELADA,
                    AsesorNotificacionStore.TARGET_SEPARACION_DETAIL,
                    separacionDocId
                );
                configurarBadge();
            }
        );
    }

    private String obtenerNombreCliente() {
        if (tvClienteSeparacionDetalle == null) return "Cliente";
        String raw = tvClienteSeparacionDetalle.getText() == null ? "" : tvClienteSeparacionDetalle.getText().toString().trim();
        return raw.isEmpty() ? "Cliente" : raw.replace("Cliente:", "").trim();
    }

    private void cargarDatosSeparacion() {
        separacionDocId  = getIntent().getStringExtra(EXTRA_SEPARACION_KEY);
        String cliente   = getIntent().getStringExtra(EXTRA_SEPARACION_CLIENTE);
        String proyecto  = getIntent().getStringExtra(EXTRA_SEPARACION_PROYECTO);
        String estado    = getIntent().getStringExtra(EXTRA_SEPARACION_ESTADO);
        String ubicacion = getIntent().getStringExtra(EXTRA_SEPARACION_UBICACION);
        String empresa   = getIntent().getStringExtra(EXTRA_SEPARACION_EMPRESA);
        String fecha     = getIntent().getStringExtra(EXTRA_SEPARACION_FECHA);
        String tipologia = getIntent().getStringExtra(EXTRA_SEPARACION_TIPOLOGIA);
        String monto     = getIntent().getStringExtra(EXTRA_SEPARACION_MONTO);

        if (cliente  == null) cliente   = "—";
        if (proyecto == null) proyecto  = "—";
        if (estado   == null) estado    = "—";
        if (ubicacion== null) ubicacion = "—";
        if (empresa  == null) empresa   = "—";
        if (fecha    == null) fecha     = "—";
        if (tipologia== null) tipologia = "—";
        if (monto    == null) monto     = "—";

        setText(tvEstadoSeparacionDetalle,            estado);
        setText(tvProyectoSeparacionDetalle,          proyecto);
        setText(tvUbicacionSeparacionDetalle,         ubicacion);
        setText(tvEmpresaSeparacionDetalle,           empresa);
        setText(tvFechaSeparacionDetalle,             fecha);
        setText(tvClienteSeparacionDetalle,           "Cliente: " + cliente);
        setText(tvClienteReservaSeparacionDetalle,    cliente);
        setText(tvDepartamentoReservaSeparacionDetalle, tipologia);
        setText(tvTelefonoReservaSeparacionDetalle,   "—");

        if (tvEstadoSeparacionDetalle != null)
            AsesorEstadoBadgeStyle.apply(this, tvEstadoSeparacionDetalle, estado);

        separacionCancelada = "Cancelada".equalsIgnoreCase(estado);
        if (separacionCancelada && btnCancelarSeparacion != null) {
            btnCancelarSeparacion.setText("Cancelada");
            btnCancelarSeparacion.setEnabled(false);
            btnCancelarSeparacion.setAlpha(0.6f);
        }
    }

    private void setText(TextView tv, String value) {
        if (tv != null) tv.setText(value != null ? value : "—");
    }

    private void mostrarDialogoAccion(String titulo, String mensaje, String textoConfirmar,
            String textoCancelar, int colorConfirmarRes, int iconoFondoRes, Runnable onConfirmar) {
        android.view.View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_eliminar_chat, null);
        TextView tvTitle    = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvMsg      = dialogView.findViewById(R.id.tvDialogMessage);
        MaterialButton btnC = dialogView.findViewById(R.id.btnCancelarDialogo);
        MaterialButton btnOk= dialogView.findViewById(R.id.btnEliminarDialogo);
        FrameLayout frameIcon = dialogView.findViewById(R.id.frameDialogIcon);
        ImageView imgIcon   = dialogView.findViewById(R.id.imgDialogIcon);

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

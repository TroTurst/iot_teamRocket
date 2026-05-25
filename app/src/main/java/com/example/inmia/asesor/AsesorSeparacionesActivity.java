package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.content.res.ColorStateList;

import java.util.ArrayList;
import java.util.List;

public class AsesorSeparacionesActivity extends AppCompatActivity implements SeparacionItemAdapter.Listener {

    private enum FiltroEstado {
        APROBADA,
        POR_CONFIRMAR,
        TERMINADA
    }

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private FrameLayout framePerfil;
    private MaterialButton btnFiltroAprobada;
    private MaterialButton btnFiltroPorConfirmar;
    private MaterialButton btnFiltroTerminada;
    private MaterialButton btnLimpiarFiltrosSeparaciones;
    private TextView tvTituloPendientes;
    private TextView tvTituloAprobadas;
    private TextView tvTituloTerminadas;
    private RecyclerView recyclerSeparacionesPendientes;
    private RecyclerView recyclerSeparacionesAprobadas;
    private RecyclerView recyclerSeparacionesTerminadas;
    private SeparacionItemAdapter pendientesAdapter;
    private SeparacionItemAdapter aprobadasAdapter;
    private SeparacionItemAdapter terminadasAdapter;
    private List<SeparacionItem> separacionesPendientes;
    private List<SeparacionItem> separacionesAprobadas;
    private List<SeparacionItem> separacionesTerminadas;

    private FiltroEstado filtroActual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_asesor_separaciones);

        bottomNav = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif);
        framePerfil = findViewById(R.id.framePerfil);
        btnFiltroAprobada = findViewById(R.id.btnFiltroAprobada);
        btnFiltroPorConfirmar = findViewById(R.id.btnFiltroPorConfirmar);
        btnFiltroTerminada = findViewById(R.id.btnFiltroTerminada);
        btnLimpiarFiltrosSeparaciones = findViewById(R.id.btnLimpiarFiltrosSeparaciones);
        tvTituloPendientes = findViewById(R.id.tvTituloPendientes);
        tvTituloAprobadas = findViewById(R.id.tvTituloAprobadas);
        tvTituloTerminadas = findViewById(R.id.tvTituloTerminadas);
        recyclerSeparacionesPendientes = findViewById(R.id.recyclerSeparacionesPendientes);
        recyclerSeparacionesAprobadas = findViewById(R.id.recyclerSeparacionesAprobadas);
        recyclerSeparacionesTerminadas = findViewById(R.id.recyclerSeparacionesTerminadas);

        AsesorSeparacionStore.seedIfEmpty(this);
        AsesorNotificacionStore.seedIfEmpty(this);
        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });

        framePerfil.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorPerfilActivity.class));
        });

        separacionesPendientes = obtenerSeparacionesPorEstado("Por aprobar");
        pendientesAdapter = new SeparacionItemAdapter(separacionesPendientes, this);
        recyclerSeparacionesPendientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerSeparacionesPendientes.setAdapter(pendientesAdapter);

        separacionesAprobadas = obtenerSeparacionesPorEstado("Aprobada");
        aprobadasAdapter = new SeparacionItemAdapter(separacionesAprobadas, this);
        recyclerSeparacionesAprobadas.setLayoutManager(new LinearLayoutManager(this));
        recyclerSeparacionesAprobadas.setAdapter(aprobadasAdapter);

        separacionesTerminadas = obtenerSeparacionesPorEstado("Terminada");
        terminadasAdapter = new SeparacionItemAdapter(separacionesTerminadas, this);
        recyclerSeparacionesTerminadas.setLayoutManager(new LinearLayoutManager(this));
        recyclerSeparacionesTerminadas.setAdapter(terminadasAdapter);

        configurarFiltros();

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
                return true;
            }

            return false;
        });
    }

    private void configurarFiltros() {
        if (btnFiltroAprobada != null) {
            btnFiltroAprobada.setOnClickListener(v -> onFiltroSeleccionado(FiltroEstado.APROBADA));
        }
        if (btnFiltroPorConfirmar != null) {
            btnFiltroPorConfirmar.setOnClickListener(v -> onFiltroSeleccionado(FiltroEstado.POR_CONFIRMAR));
        }
        if (btnFiltroTerminada != null) {
            btnFiltroTerminada.setOnClickListener(v -> onFiltroSeleccionado(FiltroEstado.TERMINADA));
        }
        if (btnLimpiarFiltrosSeparaciones != null) {
            btnLimpiarFiltrosSeparaciones.setOnClickListener(v -> aplicarFiltro(null));
        }

        aplicarFiltro(null);
    }

    private void onFiltroSeleccionado(FiltroEstado filtro) {
        if (filtroActual == filtro) {
            aplicarFiltro(null);
            return;
        }
        aplicarFiltro(filtro);
    }

    private void aplicarFiltro(FiltroEstado filtro) {
        filtroActual = filtro;

        boolean mostrarPendientes = filtro == null || filtro == FiltroEstado.POR_CONFIRMAR;
        boolean mostrarAprobadas = filtro == null || filtro == FiltroEstado.APROBADA;
        boolean mostrarTerminadas = filtro == null || filtro == FiltroEstado.TERMINADA;

        setSeccionVisible(tvTituloPendientes, recyclerSeparacionesPendientes, mostrarPendientes);
        setSeccionVisible(tvTituloAprobadas, recyclerSeparacionesAprobadas, mostrarAprobadas);
        setSeccionVisible(tvTituloTerminadas, recyclerSeparacionesTerminadas, mostrarTerminadas);

        actualizarEstadoBoton(btnFiltroAprobada, filtro == FiltroEstado.APROBADA);
        actualizarEstadoBoton(btnFiltroPorConfirmar, filtro == FiltroEstado.POR_CONFIRMAR);
        actualizarEstadoBoton(btnFiltroTerminada, filtro == FiltroEstado.TERMINADA);
    }

    private void setSeccionVisible(TextView titulo, RecyclerView lista, boolean visible) {
        int estado = visible ? android.view.View.VISIBLE : android.view.View.GONE;
        if (titulo != null) {
            titulo.setVisibility(estado);
        }
        if (lista != null) {
            lista.setVisibility(estado);
        }
    }

    private void actualizarEstadoBoton(MaterialButton button, boolean selected) {
        if (button == null) {
            return;
        }
        int bg = selected ? R.color.inmia_teal_dark : R.color.inmia_teal_pale;
        int fg = selected ? R.color.inmia_white : R.color.inmia_teal_dark;
        int stroke = selected ? 0 : 1;

        button.setBackgroundTintList(ColorStateList.valueOf(
            ContextCompat.getColor(this, bg)
        ));
        button.setTextColor(ContextCompat.getColor(this, fg));
        button.setStrokeWidth(stroke);
    }

    @Override
    public void onAction(SeparacionItem item, int position) {
        if (item.isConfirmed()) {
            openDetallesSeparacion(item);
            return;
        }
        showConfirmDialog(item, position);
    }

    private void showConfirmDialog(SeparacionItem item, int position) {
        mostrarDialogoAccion(
            "Confirmar separacion",
            "Estas seguro de confirmar la separacion del inmueble?",
            "Confirmar",
            "Cancelar",
            R.color.inmia_info,
            R.drawable.bg_badge_teal,
            () -> {
                String key = AsesorSeparacionStore.buildKey(item.getProject());
                AsesorSeparacionStore.updateStatus(this, key, "Aprobada", true);
                SeparacionItem updated = new SeparacionItem(
                    "Aprobada",
                    R.color.inmia_info,
                    item.getProject(),
                    item.getLocation(),
                    item.getCompany(),
                    "Detalles",
                    true
                );
                separacionesPendientes.set(position, updated);
                pendientesAdapter.notifyItemChanged(position);
                AsesorNotificacionHelper.enviar(
                    this,
                    "Separacion aprobada",
                    "Se aprobo la separacion de " + item.getProject(),
                    AsesorNotificacionStore.TIPO_SEPARACION_APROBADA,
                    AsesorNotificacionStore.TARGET_SEPARACION_DETAIL,
                    key
                );
                cargarSeparaciones();
                configurarBadge();
                Toast.makeText(this, "Separacion confirmada", Toast.LENGTH_SHORT).show();
            }
        );
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

    private void openDetallesSeparacion(SeparacionItem item) {
        Intent intent = new Intent(this, AsesorSeparacionDetailActivity.class);
        String key = AsesorSeparacionStore.buildKey(item.getProject());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_KEY, key);
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_PROYECTO, item.getProject());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_ESTADO, item.getStatus());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_CONFIRMADA, item.isConfirmed());
        startActivity(intent);
    }

    private void cargarSeparaciones() {
        if (pendientesAdapter != null) {
            separacionesPendientes = obtenerSeparacionesPorEstado("Por aprobar");
            pendientesAdapter.updateItems(separacionesPendientes);
        }
        if (aprobadasAdapter != null) {
            separacionesAprobadas = obtenerSeparacionesPorEstado("Aprobada");
            aprobadasAdapter.updateItems(separacionesAprobadas);
        }
        if (terminadasAdapter != null) {
            separacionesTerminadas = obtenerSeparacionesPorEstado("Terminada");
            terminadasAdapter.updateItems(separacionesTerminadas);
        }
    }

    private List<SeparacionItem> obtenerSeparacionesPorEstado(String estado) {
        List<SeparacionItem> items = new ArrayList<>();
        for (SeparacionItem item : AsesorSeparacionStore.getItems(this)) {
            if (estado.equalsIgnoreCase(item.getStatus())) {
                items.add(item);
            }
        }
        return items;
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

    @Override
    protected void onResume() {
        super.onResume();
        cargarSeparaciones();
        aplicarFiltro(filtroActual);
        configurarBadge();
    }

}

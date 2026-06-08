package com.example.inmia.asesor;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import java.util.Locale;

public class AsesorSeparacionesActivity extends AppCompatActivity implements SeparacionItemAdapter.Listener {

    private BottomNavigationView bottomNav;
    private FrameLayout frameNotificaciones;
    private TextView tvBadgeNotif;
    private FrameLayout framePerfil;
    private EditText etSeparacionesSearch;
    private AutoCompleteTextView dropdownEstadoSep;
    private AutoCompleteTextView dropdownProyectoSep;
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

    private List<SeparacionItem> allSeparaciones    = new ArrayList<>();
    private List<SeparacionItem> separacionesPendientes = new ArrayList<>();
    private List<SeparacionItem> separacionesAprobadas  = new ArrayList<>();
    private List<SeparacionItem> separacionesTerminadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_asesor_separaciones);

        bottomNav                    = findViewById(R.id.bottomNavAsesor);
        frameNotificaciones          = findViewById(R.id.frameNotificaciones);
        tvBadgeNotif                 = findViewById(R.id.tvBadgeNotif);
        framePerfil                  = findViewById(R.id.framePerfil);
        etSeparacionesSearch         = findViewById(R.id.etSeparacionesSearch);
        dropdownEstadoSep            = findViewById(R.id.dropdownEstadoSep);
        dropdownProyectoSep          = findViewById(R.id.dropdownProyectoSep);
        btnLimpiarFiltrosSeparaciones= findViewById(R.id.btnLimpiarFiltrosSeparaciones);
        tvTituloPendientes           = findViewById(R.id.tvTituloPendientes);
        tvTituloAprobadas            = findViewById(R.id.tvTituloAprobadas);
        tvTituloTerminadas           = findViewById(R.id.tvTituloTerminadas);
        recyclerSeparacionesPendientes = findViewById(R.id.recyclerSeparacionesPendientes);
        recyclerSeparacionesAprobadas  = findViewById(R.id.recyclerSeparacionesAprobadas);
        recyclerSeparacionesTerminadas = findViewById(R.id.recyclerSeparacionesTerminadas);

        AsesorNotificacionStore.seedIfEmpty(this);
        configurarBadge();

        frameNotificaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, AsesorNotificacionesActivity.class));
            limpiarBadge();
        });
        framePerfil.setOnClickListener(v -> startActivity(new Intent(this, AsesorPerfilActivity.class)));

        pendientesAdapter = new SeparacionItemAdapter(separacionesPendientes, this);
        recyclerSeparacionesPendientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerSeparacionesPendientes.setAdapter(pendientesAdapter);

        aprobadasAdapter = new SeparacionItemAdapter(separacionesAprobadas, this);
        recyclerSeparacionesAprobadas.setLayoutManager(new LinearLayoutManager(this));
        recyclerSeparacionesAprobadas.setAdapter(aprobadasAdapter);

        terminadasAdapter = new SeparacionItemAdapter(separacionesTerminadas, this);
        recyclerSeparacionesTerminadas.setLayoutManager(new LinearLayoutManager(this));
        recyclerSeparacionesTerminadas.setAdapter(terminadasAdapter);

        configurarDropdowns();
        configurarFiltros();

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
                return true;
            }
            return false;
        });

        cargarSeparacionesFirestore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSeparacionesFirestore();
        configurarBadge();
    }

    private void cargarSeparacionesFirestore() {
        AsesorFirestoreRepository.get().getSeparaciones(items -> {
            allSeparaciones = items;
            poblarDropdownProyecto(items);
            aplicarFiltros();
        });
    }

    private void configurarDropdowns() {
        String[] estados = getResources().getStringArray(R.array.separaciones_estado_options);
        dropdownEstadoSep.setAdapter(
            new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, estados));
        dropdownEstadoSep.setText("", false);
        dropdownProyectoSep.setText("", false);
    }

    private void poblarDropdownProyecto(List<SeparacionItem> items) {
        List<String> proyectos = new ArrayList<>();
        proyectos.add("Todos");
        for (SeparacionItem item : items) {
            String p = item.getProject();
            if (!p.equals("—") && !proyectos.contains(p)) proyectos.add(p);
        }
        dropdownProyectoSep.setAdapter(
            new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, proyectos));
    }

    private void configurarFiltros() {
        if (etSeparacionesSearch != null) {
            etSeparacionesSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
                @Override public void afterTextChanged(Editable s) { aplicarFiltros(); }
            });
        }
        if (dropdownEstadoSep   != null) dropdownEstadoSep.setOnItemClickListener((p, v, pos, id) -> aplicarFiltros());
        if (dropdownProyectoSep != null) dropdownProyectoSep.setOnItemClickListener((p, v, pos, id) -> aplicarFiltros());
        if (btnLimpiarFiltrosSeparaciones != null) {
            btnLimpiarFiltrosSeparaciones.setOnClickListener(v -> limpiarFiltros());
        }
    }

    private void limpiarFiltros() {
        if (etSeparacionesSearch != null) etSeparacionesSearch.setText("");
        if (dropdownEstadoSep   != null) dropdownEstadoSep.setText("", false);
        if (dropdownProyectoSep != null) dropdownProyectoSep.setText("", false);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String texto    = etSeparacionesSearch != null && etSeparacionesSearch.getText() != null
            ? etSeparacionesSearch.getText().toString().trim().toLowerCase(Locale.getDefault()) : "";
        String estado   = dropdownEstadoSep != null && dropdownEstadoSep.getText() != null
            ? dropdownEstadoSep.getText().toString().trim() : "";
        String proyecto = dropdownProyectoSep != null && dropdownProyectoSep.getText() != null
            ? dropdownProyectoSep.getText().toString().trim().toLowerCase(Locale.getDefault()) : "";

        List<SeparacionItem> filtradas = new ArrayList<>();
        for (SeparacionItem item : allSeparaciones) {
            if (!texto.isEmpty() && !coincideBusqueda(item, texto)) continue;
            if (!proyecto.isEmpty() && !"todos".equalsIgnoreCase(proyecto)
                && !item.getProject().toLowerCase(Locale.getDefault()).contains(proyecto)) continue;
            filtradas.add(item);
        }

        separacionesPendientes = filtrarPorEstado(filtradas, "Por aprobar");
        separacionesAprobadas  = filtrarPorEstado(filtradas, "Aprobada");
        separacionesTerminadas = filtrarPorEstadoTerminado(filtradas);

        pendientesAdapter.updateItems(separacionesPendientes);
        aprobadasAdapter.updateItems(separacionesAprobadas);
        terminadasAdapter.updateItems(separacionesTerminadas);

        boolean mostrarPendientes = estado.isEmpty() || "Todas".equalsIgnoreCase(estado) || "Por aprobar".equalsIgnoreCase(estado);
        boolean mostrarAprobadas  = estado.isEmpty() || "Todas".equalsIgnoreCase(estado) || "Aprobada".equalsIgnoreCase(estado);
        boolean mostrarTerminadas = estado.isEmpty() || "Todas".equalsIgnoreCase(estado)
            || "Terminada".equalsIgnoreCase(estado) || "Cancelada".equalsIgnoreCase(estado);

        setSeccionVisible(tvTituloPendientes, recyclerSeparacionesPendientes,
            mostrarPendientes && !separacionesPendientes.isEmpty());
        setSeccionVisible(tvTituloAprobadas,  recyclerSeparacionesAprobadas,
            mostrarAprobadas  && !separacionesAprobadas.isEmpty());
        setSeccionVisible(tvTituloTerminadas, recyclerSeparacionesTerminadas,
            mostrarTerminadas && !separacionesTerminadas.isEmpty());
    }

    private boolean coincideBusqueda(SeparacionItem item, String texto) {
        return contiene(item.getProject(),   texto)
            || contiene(item.getLocation(),  texto)
            || contiene(item.getCompany(),   texto)
            || contiene(item.getClienteId(), texto)
            || contiene(item.getStatus(),    texto);
    }

    private boolean contiene(String value, String texto) {
        return value != null && value.toLowerCase(Locale.getDefault()).contains(texto);
    }

    private List<SeparacionItem> filtrarPorEstado(List<SeparacionItem> all, String estado) {
        List<SeparacionItem> result = new ArrayList<>();
        for (SeparacionItem item : all) {
            if (estado.equalsIgnoreCase(item.getStatus())) result.add(item);
        }
        return result;
    }

    private List<SeparacionItem> filtrarPorEstadoTerminado(List<SeparacionItem> all) {
        List<SeparacionItem> result = new ArrayList<>();
        for (SeparacionItem item : all) {
            String s = item.getStatus();
            if ("Terminada".equalsIgnoreCase(s) || "Cancelada".equalsIgnoreCase(s)) result.add(item);
        }
        return result;
    }

    private void setSeccionVisible(TextView titulo, RecyclerView lista, boolean visible) {
        int v = visible ? View.VISIBLE : View.GONE;
        if (titulo != null) titulo.setVisibility(v);
        if (lista  != null) lista.setVisibility(v);
    }

    @Override
    public void onAction(SeparacionItem item, int position) {
        if (item.isConfirmed()) {
            openDetallesSeparacion(item);
        } else {
            showConfirmDialog(item, position);
        }
    }

    private void showConfirmDialog(SeparacionItem item, int position) {
        mostrarDialogoAccion(
            "Confirmar separacion",
            "Estas seguro de confirmar la separacion del inmueble?",
            "Confirmar", "Cancelar",
            R.color.inmia_info, R.drawable.bg_badge_teal,
            () -> {
                AsesorFirestoreRepository.get().updateSeparacionEstado(item.getDocId(), "aprobada");
                SeparacionItem updated = new SeparacionItem(
                    "Aprobada", R.color.inmia_info,
                    item.getProject(), item.getLocation(), item.getCompany(),
                    "Detalles", true,
                    item.getDocId(), item.getClienteId(),
                    item.getTipologia(), item.getFecha(), item.getMonto()
                );
                separacionesPendientes.set(position, updated);
                pendientesAdapter.notifyItemChanged(position);
                AsesorNotificacionHelper.enviar(
                    this, "Separacion aprobada",
                    "Se aprobo la separacion de " + item.getProject(),
                    AsesorNotificacionStore.TIPO_SEPARACION_APROBADA,
                    AsesorNotificacionStore.TARGET_SEPARACION_DETAIL,
                    item.getDocId()
                );
                cargarSeparacionesFirestore();
                configurarBadge();
                Toast.makeText(this, "Separacion confirmada", Toast.LENGTH_SHORT).show();
            }
        );
    }

    private void mostrarDialogoAccion(String titulo, String mensaje, String textoConfirmar,
            String textoCancelar, int colorConfirmarRes, int iconoFondoRes, Runnable onConfirmar) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_eliminar_chat, null);
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

    private void openDetallesSeparacion(SeparacionItem item) {
        Intent intent = new Intent(this, AsesorSeparacionDetailActivity.class);
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_KEY,       item.getDocId());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_CLIENTE,   item.getClienteId());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_PROYECTO,  item.getProject());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_ESTADO,    item.getStatus());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_CONFIRMADA,item.isConfirmed());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_UBICACION, item.getLocation());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_EMPRESA,   item.getCompany());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_FECHA,     item.getFecha());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_TIPOLOGIA, item.getTipologia());
        intent.putExtra(AsesorSeparacionDetailActivity.EXTRA_SEPARACION_MONTO,     item.getMonto());
        startActivity(intent);
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

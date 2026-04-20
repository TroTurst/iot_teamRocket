package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class GestionUsuariosActivity extends AppCompatActivity {

    // Tabs
    private TextView tabAdmins, tabAsesores, tabClientes;

    // Barra superior
    private EditText etBuscar;
    private View btnFiltro;
    private TextView tvTituloLista;
    private MaterialButton btnNuevo, btnSolicitudes;

    // Switches hardcodeados
    private Switch switch1, switch2, switch3, switch4;

    // Bottom nav
    private BottomNavigationView bottomNav;

    // Estado actual del tab
    private String tabActual = "admins";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_gestion_usuarios_superadmin);

        // Vincular vistas
        tabAdmins      = findViewById(R.id.tabAdmins);
        tabAsesores    = findViewById(R.id.tabAsesores);
        tabClientes    = findViewById(R.id.tabClientes);
        etBuscar       = findViewById(R.id.etBuscar);
        btnFiltro      = findViewById(R.id.btnFiltro);
        tvTituloLista  = findViewById(R.id.tvTituloLista);
        btnNuevo       = findViewById(R.id.btnNuevo);
        btnSolicitudes = findViewById(R.id.btnSolicitudes);
        switch1        = findViewById(R.id.switch1);
        switch2        = findViewById(R.id.switch2);
        switch3        = findViewById(R.id.switch3);
        switch4        = findViewById(R.id.switch4);
        bottomNav      = findViewById(R.id.bottomNavSuperAdmin);

        // Marcar tab activo en el nav
        bottomNav.setSelectedItemId(R.id.nav_usuarios);

        // Configurar tabs
        tabAdmins.setOnClickListener(v  -> seleccionarTab("admins"));
        tabAsesores.setOnClickListener(v -> seleccionarTab("asesores"));
        tabClientes.setOnClickListener(v -> seleccionarTab("clientes"));

        // Configurar switches con AlertDialog
        configurarSwitch(switch1, "Cyndy Lillibridge");
        configurarSwitch(switch2, "John Travolta");
        configurarSwitch(switch3, "Teresa Mertens");
        configurarSwitch(switch4, "Teddy Gallagher");

        // ← CAMBIO: Ver perfil — Cyndy navega, los demás Toast
        findViewById(R.id.layoutVerPerfil1).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilUserActivity.class)));

        findViewById(R.id.layoutVerPerfil2).setOnClickListener(v ->
                Toast.makeText(this, "Perfil no disponible",
                        Toast.LENGTH_SHORT).show());

        findViewById(R.id.layoutVerPerfil3).setOnClickListener(v ->
                Toast.makeText(this, "Perfil no disponible",
                        Toast.LENGTH_SHORT).show());

        findViewById(R.id.layoutVerPerfil4).setOnClickListener(v ->
                Toast.makeText(this, "Perfil no disponible",
                        Toast.LENGTH_SHORT).show());

        // Búsqueda
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: filtrar lista por nombre cuando conectemos Firebase
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filtro
        btnFiltro.setOnClickListener(v -> mostrarDialogoFiltro());

        // Botón Nuevo
        btnNuevo.setOnClickListener(v ->
                startActivity(new Intent(this, CrearAdminActivity.class)));

        // Botón Solicitudes
        btnSolicitudes.setOnClickListener(v ->
                startActivity(new Intent(this, SolicitudesActivity.class)));

        // ── BOTTOM NAVIGATION ──
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, SuperAdminHomeActivity.class));
                return true;
            } else if (id == R.id.nav_usuarios) {
                return true;
            } else if (id == R.id.nav_reportes) {
                startActivity(new Intent(this, ReportesActivity.class));
                return true;
            } else if (id == R.id.nav_logs) {
                startActivity(new Intent(this, LogsActivity.class));
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }

            return false;
        });
    }

    private void seleccionarTab(String tab) {
        tabActual = tab;

        tabAdmins.setBackground(getDrawable(R.drawable.tab_unselected_bg_superadmin));
        tabAdmins.setTextColor(getColor(R.color.inmia_teal_dark));
        tabAsesores.setBackground(getDrawable(R.drawable.tab_unselected_bg_superadmin));
        tabAsesores.setTextColor(getColor(R.color.inmia_teal_dark));
        tabClientes.setBackground(getDrawable(R.drawable.tab_unselected_bg_superadmin));
        tabClientes.setTextColor(getColor(R.color.inmia_teal_dark));

        switch (tab) {
            case "admins":
                tabAdmins.setBackground(getDrawable(R.drawable.tab_selected_bg_superadmin));
                tabAdmins.setTextColor(getColor(android.R.color.white));
                tvTituloLista.setText("Lista de administradores (3)");
                btnNuevo.setVisibility(View.VISIBLE);
                btnSolicitudes.setVisibility(View.GONE);
                break;

            case "asesores":
                tabAsesores.setBackground(getDrawable(R.drawable.tab_selected_bg_superadmin));
                tabAsesores.setTextColor(getColor(android.R.color.white));
                tvTituloLista.setText("Lista de asesores (3)");
                btnNuevo.setVisibility(View.GONE);
                btnSolicitudes.setVisibility(View.VISIBLE);
                break;

            case "clientes":
                tabClientes.setBackground(getDrawable(R.drawable.tab_selected_bg_superadmin));
                tabClientes.setTextColor(getColor(android.R.color.white));
                tvTituloLista.setText("Lista de clientes (3)");
                btnNuevo.setVisibility(View.GONE);
                btnSolicitudes.setVisibility(View.GONE);
                break;
        }
    }

    private void configurarSwitch(Switch sw, String nombreUsuario) {
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String titulo = isChecked
                    ? getString(R.string.dialog_habilitar_titulo)
                    : getString(R.string.dialog_inhabilitar_titulo);
            String mensaje = isChecked
                    ? getString(R.string.dialog_habilitar_mensaje)
                    : getString(R.string.dialog_inhabilitar_mensaje);
            String btnConfirmar = isChecked
                    ? getString(R.string.dialog_habilitar_confirmar)
                    : getString(R.string.dialog_inhabilitar_confirmar);

            new AlertDialog.Builder(this)
                    .setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton(btnConfirmar, (dialog, which) -> {
                        String estado = isChecked ? "habilitado" : "inhabilitado";
                        Toast.makeText(this,
                                nombreUsuario + " ha sido " + estado,
                                Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(getString(R.string.dialog_inhabilitar_cancelar),
                            (dialog, which) -> {
                                sw.setOnCheckedChangeListener(null);
                                sw.setChecked(!isChecked);
                                configurarSwitch(sw, nombreUsuario);
                            })
                    .setCancelable(false)
                    .show();
        });
    }

    private void mostrarDialogoFiltro() {
        String[] opciones = {
                getString(R.string.filtro_todos),
                getString(R.string.filtro_activos),
                getString(R.string.filtro_inactivos)
        };

        new AlertDialog.Builder(this)
                .setTitle("Filtrar por estado")
                .setItems(opciones, (dialog, which) ->
                        Toast.makeText(this, "Filtro: " + opciones[which],
                                Toast.LENGTH_SHORT).show())
                .show();
    }
}
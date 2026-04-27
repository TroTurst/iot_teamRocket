package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class GestionUsuariosActivity extends AppCompatActivity
        implements UsuarioAdapter.OnVerPerfilListener {

    // Tabs
    private TextView tabAdmins, tabAsesores, tabClientes;

    // Barra superior
    private EditText etBuscar;
    private View btnFiltro;
    private TextView tvTituloLista;
    private MaterialButton btnNuevo, btnSolicitudes;

    // RecyclerView
    private RecyclerView recyclerUsuarios;
    private UsuarioAdapter adapter;

    // Bottom nav
    private BottomNavigationView bottomNav;

    // Estado actual del tab
    private String tabActual = "admins";

    // ── Listas hardcodeadas por rol ──────────────────────────────────────────
    private List<Usuario> listaAdmins;
    private List<Usuario> listaAsesores;
    private List<Usuario> listaClientes;

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
        recyclerUsuarios = findViewById(R.id.recyclerUsuarios);
        bottomNav      = findViewById(R.id.bottomNavSuperAdmin);

        // Inicializar datos hardcodeados
        inicializarDatos();

        // Configurar RecyclerView
        recyclerUsuarios.setLayoutManager(
                new LinearLayoutManager(this));
        adapter = new UsuarioAdapter(this, listaAdmins, this);
        recyclerUsuarios.setAdapter(adapter);

        // Marcar tab activo en el nav
        bottomNav.setSelectedItemId(R.id.nav_usuarios);

        // Configurar tabs
        tabAdmins.setOnClickListener(v  -> seleccionarTab("admins"));
        tabAsesores.setOnClickListener(v -> seleccionarTab("asesores"));
        tabClientes.setOnClickListener(v -> seleccionarTab("clientes"));

        // Búsqueda en tiempo real
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,
                                                    int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s,
                                                int start, int before, int count) {
                filtrarPorNombre(s.toString());
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

        // Bottom navigation
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

    // ── Datos hardcodeados ───────────────────────────────────────────────────

    private void inicializarDatos() {
        // Admins
        listaAdmins = new ArrayList<>();
        listaAdmins.add(new Usuario(
                "Cyndy Lillibridge", "Inmobiliaria Sofia",  "CL", true,  "admin"));
        listaAdmins.add(new Usuario(
                "John Travolta",     "Inmobiliaria John",   "JT", true,  "admin"));
        listaAdmins.add(new Usuario(
                "Teresa Mertens",    "Inmobiliaria I&M",    "TM", true,  "admin"));
        listaAdmins.add(new Usuario(
                "Teddy Gallagher",   "Inmobiliaria Oasis",  "TG", false, "admin"));

        // Asesores
        listaAsesores = new ArrayList<>();
        listaAsesores.add(new Usuario(
                "María García",      "INMIA San Isidro",    "MG", true,  "asesor"));
        listaAsesores.add(new Usuario(
                "Carlos Ramos",      "INMIA Miraflores",    "CR", true,  "asesor"));
        listaAsesores.add(new Usuario(
                "Juan Sánchez",      "INMIA Surco",         "JS", false, "asesor"));

        // Clientes
        listaClientes = new ArrayList<>();
        listaClientes.add(new Usuario(
                "Ana Torres",        "Sin inmobiliaria",    "AT", true,  "cliente"));
        listaClientes.add(new Usuario(
                "Pedro Vargas",      "Sin inmobiliaria",    "PV", true,  "cliente"));
        listaClientes.add(new Usuario(
                "Lucía Mendoza",     "Sin inmobiliaria",    "LM", false, "cliente"));
    }

    // ── Tabs ─────────────────────────────────────────────────────────────────

    private void seleccionarTab(String tab) {
        tabActual = tab;

        // Resetear todos los tabs
        tabAdmins.setBackground(
                getDrawable(R.drawable.tab_unselected_bg_superadmin));
        tabAdmins.setTextColor(getColor(R.color.inmia_teal_dark));
        tabAsesores.setBackground(
                getDrawable(R.drawable.tab_unselected_bg_superadmin));
        tabAsesores.setTextColor(getColor(R.color.inmia_teal_dark));
        tabClientes.setBackground(
                getDrawable(R.drawable.tab_unselected_bg_superadmin));
        tabClientes.setTextColor(getColor(R.color.inmia_teal_dark));

        switch (tab) {
            case "admins":
                tabAdmins.setBackground(
                        getDrawable(R.drawable.tab_selected_bg_superadmin));
                tabAdmins.setTextColor(getColor(android.R.color.white));
                tvTituloLista.setText(
                        "Lista de administradores (" + listaAdmins.size() + ")");
                adapter.actualizarLista(listaAdmins);
                btnNuevo.setVisibility(View.VISIBLE);
                btnSolicitudes.setVisibility(View.GONE);
                break;

            case "asesores":
                tabAsesores.setBackground(
                        getDrawable(R.drawable.tab_selected_bg_superadmin));
                tabAsesores.setTextColor(getColor(android.R.color.white));
                tvTituloLista.setText(
                        "Lista de asesores (" + listaAsesores.size() + ")");
                adapter.actualizarLista(listaAsesores);
                btnNuevo.setVisibility(View.GONE);
                btnSolicitudes.setVisibility(View.VISIBLE);
                break;

            case "clientes":
                tabClientes.setBackground(
                        getDrawable(R.drawable.tab_selected_bg_superadmin));
                tabClientes.setTextColor(getColor(android.R.color.white));
                tvTituloLista.setText(
                        "Lista de clientes (" + listaClientes.size() + ")");
                adapter.actualizarLista(listaClientes);
                btnNuevo.setVisibility(View.GONE);
                btnSolicitudes.setVisibility(View.GONE);
                break;
        }
    }

    // ── Búsqueda ─────────────────────────────────────────────────────────────

    private void filtrarPorNombre(String query) {
        List<Usuario> listaBase = obtenerListaActual();
        if (query.isEmpty()) {
            adapter.actualizarLista(listaBase);
            return;
        }

        List<Usuario> filtrada = new ArrayList<>();
        for (Usuario u : listaBase) {
            if (u.getNombre().toLowerCase()
                    .contains(query.toLowerCase())) {
                filtrada.add(u);
            }
        }
        adapter.actualizarLista(filtrada);
    }

    private List<Usuario> obtenerListaActual() {
        switch (tabActual) {
            case "asesores": return listaAsesores;
            case "clientes": return listaClientes;
            default:         return listaAdmins;
        }
    }

    // ── Filtro por estado ─────────────────────────────────────────────────────

    private void mostrarDialogoFiltro() {
        String[] opciones = {"Todos", "Activos", "Inactivos"};

        new AlertDialog.Builder(this)
                .setTitle("Filtrar por estado")
                .setItems(opciones, (dialog, which) -> {
                    List<Usuario> listaBase = obtenerListaActual();
                    List<Usuario> filtrada  = new ArrayList<>();

                    switch (which) {
                        case 0: // Todos
                            adapter.actualizarLista(listaBase);
                            return;
                        case 1: // Activos
                            for (Usuario u : listaBase) {
                                if (u.isActivo()) filtrada.add(u);
                            }
                            break;
                        case 2: // Inactivos
                            for (Usuario u : listaBase) {
                                if (!u.isActivo()) filtrada.add(u);
                            }
                            break;
                    }
                    adapter.actualizarLista(filtrada);
                })
                .show();
    }

    // ── Ver perfil — callback del adapter ────────────────────────────────────

    @Override
    public void onVerPerfil(Usuario usuario) {
        if (usuario.getNombre().equals("Cyndy Lillibridge")) {
            startActivity(new Intent(this, PerfilUserActivity.class));
        } else {
            Toast.makeText(this, "Perfil no disponible",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
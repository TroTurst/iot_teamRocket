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

        setContentView(R.layout.sa_activity_gestion_usuarios);

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

    // ── Listas con datos completos ───────────────────────────────────────────

    private void inicializarDatos() {
        // Admins
        listaAdmins = new ArrayList<>();
        listaAdmins.add(new Usuario(
                "Cyndy Lillibridge", "Inmobiliaria Sofia", "CL", true, "admin",
                "DNI · 87654321", "12/05/1988",
                "cyndy@inmiasofia.com", "+51 994 123 456",
                "Av. Santa Cruz 890, Miraflores"));
        listaAdmins.add(new Usuario(
                "John Travolta", "Inmobiliaria John", "JT", true, "admin",
                "DNI · 12345678", "20/08/1985",
                "john@inmiajohn.com", "+51 987 111 222",
                "Jr. Los Olivos 123, San Borja"));
        listaAdmins.add(new Usuario(
                "Teresa Mertens", "Inmobiliaria I&M", "TM", true, "admin",
                "DNI · 23456789", "05/03/1990",
                "teresa@inmiaiym.com", "+51 976 333 444",
                "Av. Arequipa 456, Lince"));
        listaAdmins.add(new Usuario(
                "Teddy Gallagher", "Inmobiliaria Oasis", "TG", false, "admin",
                "DNI · 34567890", "18/11/1982",
                "teddy@inmiaoasis.com", "+51 965 555 666",
                "Calle Lima 789, Pueblo Libre"));

        // Asesores
        listaAsesores = new ArrayList<>();
        listaAsesores.add(new Usuario(
                "María García", "INMIA San Isidro", "MG", true, "asesor",
                "DNI · 45678901", "15/03/1995",
                "m.garcia@inmia.com", "+51 987 654 321",
                "Av. Javier Prado 1234, San Isidro"));
        listaAsesores.add(new Usuario(
                "Carlos Ramos", "INMIA Miraflores", "CR", true, "asesor",
                "DNI · 32156789", "22/07/1990",
                "c.ramos@inmia.com", "+51 912 345 678",
                "Calle Las Flores 567, Miraflores"));
        listaAsesores.add(new Usuario(
                "Juan Sánchez", "INMIA Surco", "JS", false, "asesor",
                "DNI · 78234561", "08/11/1988",
                "j.sanchez@inmia.com", "+51 956 789 012",
                "Jr. Los Pinos 890, Surco"));

        // Clientes
        listaClientes = new ArrayList<>();
        listaClientes.add(new Usuario(
                "Ana Torres", "Sin inmobiliaria", "AT", true, "cliente",
                "DNI · 56789012", "30/06/1998",
                "ana.torres@gmail.com", "+51 945 111 222",
                "Av. Brasil 321, Jesús María"));
        listaClientes.add(new Usuario(
                "Pedro Vargas", "Sin inmobiliaria", "PV", true, "cliente",
                "DNI · 67890123", "14/02/1993",
                "pedro.vargas@gmail.com", "+51 934 333 444",
                "Calle Colón 654, Barranco"));
        listaClientes.add(new Usuario(
                "Lucía Mendoza", "Sin inmobiliaria", "LM", false, "cliente",
                "DNI · 89012345", "25/09/2000",
                "lucia.mendoza@gmail.com", "+51 923 555 666",
                "Jr. Cusco 987, Cercado"));
    }

// ── Ver perfil — navegar con datos completos ──────────────────────────────

    @Override
    public void onVerPerfil(Usuario usuario) {
        Intent intent = new Intent(this, PerfilUserActivity.class);
        intent.putExtra(PerfilUserActivity.EXTRA_NOMBRE,       usuario.getNombre());
        intent.putExtra(PerfilUserActivity.EXTRA_EMPRESA,      usuario.getEmpresa());
        intent.putExtra(PerfilUserActivity.EXTRA_INICIALES,    usuario.getIniciales());
        intent.putExtra(PerfilUserActivity.EXTRA_DOCUMENTO,    usuario.getDocumento());
        intent.putExtra(PerfilUserActivity.EXTRA_FECHA_NAC,    usuario.getFechaNacimiento());
        intent.putExtra(PerfilUserActivity.EXTRA_CORREO,       usuario.getCorreo());
        intent.putExtra(PerfilUserActivity.EXTRA_TELEFONO,     usuario.getTelefono());
        intent.putExtra(PerfilUserActivity.EXTRA_DOMICILIO,    usuario.getDomicilio());
        intent.putExtra(PerfilUserActivity.EXTRA_ACTIVO,       usuario.isActivo());
        intent.putExtra(PerfilUserActivity.EXTRA_ROL,          usuario.getRol());
        startActivity(intent);
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
}
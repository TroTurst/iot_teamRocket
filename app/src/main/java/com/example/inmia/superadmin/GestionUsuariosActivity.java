package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.inmia.R;
import com.example.inmia.models.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    // ViewPager2
    private ViewPager2 viewPager;
    private UsuariosPagerAdapter pagerAdapter;
    private ProgressBar progressBar;

    // Bottom nav
    private BottomNavigationView bottomNav;

    // 0=admins, 1=asesores, 2=clientes
    private int tabActual = 0;

    // Listas por rol
    private List<Usuario> listaAdmins;
    private List<Usuario> listaAsesores;
    private List<Usuario> listaClientes;

    // Firebase
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_gestion_usuarios);

        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        tabAdmins      = findViewById(R.id.tabAdmins);
        tabAsesores    = findViewById(R.id.tabAsesores);
        tabClientes    = findViewById(R.id.tabClientes);
        etBuscar       = findViewById(R.id.etBuscar);
        btnFiltro      = findViewById(R.id.btnFiltro);
        tvTituloLista  = findViewById(R.id.tvTituloLista);
        btnNuevo       = findViewById(R.id.btnNuevo);
        btnSolicitudes = findViewById(R.id.btnSolicitudes);
        viewPager      = findViewById(R.id.viewPagerUsuarios);
        progressBar    = findViewById(R.id.progressBar);
        bottomNav      = findViewById(R.id.bottomNavSuperAdmin);

        listaAdmins   = new ArrayList<>();
        listaAsesores = new ArrayList<>();
        listaClientes = new ArrayList<>();

        // Configurar ViewPager2 con listas vacías (se llenan al cargar Firebase)
        pagerAdapter = new UsuariosPagerAdapter(
                this, listaAdmins, listaAsesores, listaClientes, this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        // Sincronizar tabs al deslizar
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabActual = position;
                etBuscar.setText("");
                actualizarTabVisual(position);
            }
        });

        // Estado inicial del tab visual
        actualizarTabVisual(0);

        // Clicks en tabs navegan el pager con animación
        tabAdmins.setOnClickListener(v   -> viewPager.setCurrentItem(0, true));
        tabAsesores.setOnClickListener(v -> viewPager.setCurrentItem(1, true));
        tabClientes.setOnClickListener(v -> viewPager.setCurrentItem(2, true));

        // Búsqueda en tiempo real
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
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

        // Marcar tab activo en nav
        bottomNav.setSelectedItemId(R.id.nav_usuarios);

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

        // Cargar datos desde Firestore
        cargarUsuariosDeFirestore();
    }

    // ── Carga de datos desde Firestore ──────────────────────────────────────

    private void cargarUsuariosDeFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        final int[] finalizadas = {0};

        db.collection("usuarios").whereEqualTo("rol", "admin").get()
            .addOnSuccessListener(query -> {
                for (DocumentSnapshot doc : query.getDocuments()) {
                    listaAdmins.add(documentToUsuario(doc));
                }
                finalizadas[0]++;
                if (finalizadas[0] == 3) onTodasCargadas();
            })
            .addOnFailureListener(e -> {
                Log.e("Firestore", "Error al cargar admins", e);
                finalizadas[0]++;
                if (finalizadas[0] == 3) onTodasCargadas();
            });

        db.collection("usuarios").whereEqualTo("rol", "asesor").get()
            .addOnSuccessListener(query -> {
                for (DocumentSnapshot doc : query.getDocuments()) {
                    listaAsesores.add(documentToUsuario(doc));
                }
                finalizadas[0]++;
                if (finalizadas[0] == 3) onTodasCargadas();
            })
            .addOnFailureListener(e -> {
                Log.e("Firestore", "Error al cargar asesores", e);
                finalizadas[0]++;
                if (finalizadas[0] == 3) onTodasCargadas();
            });

        db.collection("usuarios").whereEqualTo("rol", "cliente").get()
            .addOnSuccessListener(query -> {
                for (DocumentSnapshot doc : query.getDocuments()) {
                    listaClientes.add(documentToUsuario(doc));
                }
                finalizadas[0]++;
                if (finalizadas[0] == 3) onTodasCargadas();
            })
            .addOnFailureListener(e -> {
                Log.e("Firestore", "Error al cargar clientes", e);
                finalizadas[0]++;
                if (finalizadas[0] == 3) onTodasCargadas();
            });
    }

    private void onTodasCargadas() {
        progressBar.setVisibility(View.GONE);
        pagerAdapter.getPageAdapter(0).actualizarLista(listaAdmins);
        pagerAdapter.getPageAdapter(1).actualizarLista(listaAsesores);
        pagerAdapter.getPageAdapter(2).actualizarLista(listaClientes);
        actualizarTabVisual(tabActual);
    }

    private Usuario documentToUsuario(DocumentSnapshot doc) {
        String nombres   = doc.getString("nombres");
        String apellidos = doc.getString("apellidos");
        if (nombres == null)   nombres   = "";
        if (apellidos == null) apellidos = "";
        String nombre = (nombres + " " + apellidos).trim();

        // Iniciales: primera letra de nombres + primera letra de apellidos
        String iniciales = "";
        if (!nombres.isEmpty())   iniciales += Character.toUpperCase(nombres.charAt(0));
        if (!apellidos.isEmpty()) iniciales += Character.toUpperCase(apellidos.charAt(0));
        if (iniciales.isEmpty())  iniciales = "??";

        Boolean activoObj = doc.getBoolean("activo");
        boolean activo = activoObj != null && activoObj;

        String rol = doc.getString("rol");
        if (rol == null) rol = "";

        // empresa: campo opcional; si no existe, se usa un valor por defecto según rol
        String empresa = doc.getString("empresa");
        if (empresa == null || empresa.isEmpty()) {
            empresa = "cliente".equals(rol) ? "Sin inmobiliaria" : "INMIA";
        }

        // documento: combina tipo y número
        String tipoDoc = doc.getString("tipoDocumento");
        String numDoc  = doc.getString("numeroDocumento");
        String documento = "";
        if (tipoDoc != null && !tipoDoc.isEmpty()) {
            documento = tipoDoc + " · " + (numDoc != null ? numDoc : "");
        } else if (numDoc != null) {
            documento = numDoc;
        }

        String fechaNac  = doc.getString("fechaNacimiento"); if (fechaNac == null)  fechaNac  = "";
        String correo    = doc.getString("correo");          if (correo == null)    correo    = "";
        String telefono  = doc.getString("telefono");        if (telefono == null)  telefono  = "";
        String domicilio = doc.getString("domicilio");       if (domicilio == null) domicilio = "";

        String fotoUrl = doc.getString("fotoUrl"); if (fotoUrl == null) fotoUrl = "";

        Usuario u = new Usuario(nombre, empresa, iniciales, activo, rol,
                documento, fechaNac, correo, telefono, domicilio);
        u.setUid(doc.getId());
        u.setFotoUrl(fotoUrl);
        return u;
    }

    // ── Ver perfil ───────────────────────────────────────────────────────────

    @Override
    public void onVerPerfil(Usuario usuario) {
        Intent intent = new Intent(this, PerfilUserActivity.class);
        intent.putExtra(PerfilUserActivity.EXTRA_NOMBRE,    usuario.getNombre());
        intent.putExtra(PerfilUserActivity.EXTRA_EMPRESA,   usuario.getEmpresa());
        intent.putExtra(PerfilUserActivity.EXTRA_INICIALES, usuario.getIniciales());
        intent.putExtra(PerfilUserActivity.EXTRA_DOCUMENTO, usuario.getDocumento());
        intent.putExtra(PerfilUserActivity.EXTRA_FECHA_NAC, usuario.getFechaNacimiento());
        intent.putExtra(PerfilUserActivity.EXTRA_CORREO,    usuario.getCorreo());
        intent.putExtra(PerfilUserActivity.EXTRA_TELEFONO,  usuario.getTelefono());
        intent.putExtra(PerfilUserActivity.EXTRA_DOMICILIO, usuario.getDomicilio());
        intent.putExtra(PerfilUserActivity.EXTRA_ACTIVO,    usuario.isActivo());
        intent.putExtra(PerfilUserActivity.EXTRA_ROL,       usuario.getRol());
        intent.putExtra(PerfilUserActivity.EXTRA_FOTO_URL,  usuario.getFotoUrl());
        startActivity(intent);
    }

    // ── Tabs ─────────────────────────────────────────────────────────────────

    private void actualizarTabVisual(int position) {
        tabAdmins.setBackground(getDrawable(R.drawable.tab_unselected_bg_superadmin));
        tabAdmins.setTextColor(getColor(R.color.inmia_teal_dark));
        tabAsesores.setBackground(getDrawable(R.drawable.tab_unselected_bg_superadmin));
        tabAsesores.setTextColor(getColor(R.color.inmia_teal_dark));
        tabClientes.setBackground(getDrawable(R.drawable.tab_unselected_bg_superadmin));
        tabClientes.setTextColor(getColor(R.color.inmia_teal_dark));

        switch (position) {
            case 0:
                tabAdmins.setBackground(getDrawable(R.drawable.tab_selected_bg_superadmin));
                tabAdmins.setTextColor(getColor(android.R.color.white));
                tvTituloLista.setText("Lista de administradores (" + listaAdmins.size() + ")");
                btnNuevo.setVisibility(View.VISIBLE);
                btnSolicitudes.setVisibility(View.GONE);
                break;
            case 1:
                tabAsesores.setBackground(getDrawable(R.drawable.tab_selected_bg_superadmin));
                tabAsesores.setTextColor(getColor(android.R.color.white));
                tvTituloLista.setText("Lista de asesores (" + listaAsesores.size() + ")");
                btnNuevo.setVisibility(View.GONE);
                btnSolicitudes.setVisibility(View.VISIBLE);
                break;
            case 2:
                tabClientes.setBackground(getDrawable(R.drawable.tab_selected_bg_superadmin));
                tabClientes.setTextColor(getColor(android.R.color.white));
                tvTituloLista.setText("Lista de clientes (" + listaClientes.size() + ")");
                btnNuevo.setVisibility(View.GONE);
                btnSolicitudes.setVisibility(View.GONE);
                break;
        }
    }

    // ── Búsqueda ─────────────────────────────────────────────────────────────

    private void filtrarPorNombre(String query) {
        List<Usuario> listaBase = obtenerListaActual();
        UsuarioAdapter currentAdapter = pagerAdapter.getPageAdapter(tabActual);

        if (query.isEmpty()) {
            currentAdapter.actualizarLista(listaBase);
            return;
        }

        List<Usuario> filtrada = new ArrayList<>();
        for (Usuario u : listaBase) {
            if (u.getNombre().toLowerCase().contains(query.toLowerCase())) {
                filtrada.add(u);
            }
        }
        currentAdapter.actualizarLista(filtrada);
    }

    private List<Usuario> obtenerListaActual() {
        switch (tabActual) {
            case 1: return listaAsesores;
            case 2: return listaClientes;
            default: return listaAdmins;
        }
    }

    // ── Filtro por estado ─────────────────────────────────────────────────────

    private void mostrarDialogoFiltro() {
        String[] opciones = {"Todos", "Activos", "Inactivos"};

        new AlertDialog.Builder(this)
                .setTitle("Filtrar por estado")
                .setItems(opciones, (dialog, which) -> {
                    List<Usuario> listaBase = obtenerListaActual();
                    UsuarioAdapter currentAdapter = pagerAdapter.getPageAdapter(tabActual);

                    if (which == 0) {
                        currentAdapter.actualizarLista(listaBase);
                        return;
                    }

                    boolean buscarActivos = (which == 1);
                    List<Usuario> filtrada = new ArrayList<>();
                    for (Usuario u : listaBase) {
                        if (u.isActivo() == buscarActivos) filtrada.add(u);
                    }
                    currentAdapter.actualizarLista(filtrada);
                })
                .show();
    }
}

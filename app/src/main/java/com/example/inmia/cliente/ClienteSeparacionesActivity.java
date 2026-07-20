package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Separacion;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClienteSeparacionesActivity extends AppCompatActivity {

    private RecyclerView rvSeparaciones;
    private SeparacionAdapter adapter;
    private List<Separacion> listaSeparacionesCompletas = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_separaciones_cliente);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvSeparaciones = findViewById(R.id.rvSeparaciones);
        rvSeparaciones.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SeparacionAdapter(listaSeparacionesCompletas);
        rvSeparaciones.setAdapter(adapter);

        configurarNavegacion();
        configurarFiltrosChips();
        cargarSeparacionesDeFirestore();
    }

    private void cargarSeparacionesDeFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();

        db.collection("separaciones")
                .whereEqualTo("clienteId", uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    listaSeparacionesCompletas.clear();


                    String sepIdParaValorar      = null;
                    String proyectoIdParaValorar  = null;
                    String nombreParaValorar      = null;

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            String estado         = doc.getString("estado");
                            String nombreProyecto = doc.getString("nombreProyecto");
                            String ubicacion      = doc.getString("ubicacion");
                            String inmobiliaria   = doc.getString("inmobiliariaNombre");
                            String imagenUrl      = doc.getString("imagenUrl");

                            estado        = estado != null ? estado : "En proceso";
                            nombreProyecto = nombreProyecto != null ? nombreProyecto : "Proyecto Desconocido";
                            ubicacion     = ubicacion != null ? ubicacion : "Ubicación no especificada";
                            inmobiliaria  = inmobiliaria != null ? inmobiliaria : "Galeon Inmobiliaria";
                            imagenUrl     = imagenUrl != null ? imagenUrl : "";

                            Separacion separacion = new Separacion(
                                    estado, nombreProyecto, ubicacion, inmobiliaria, imagenUrl);
                            separacion.setId(doc.getId());
                            listaSeparacionesCompletas.add(separacion);


                            boolean esPagada    = "Pagada".equalsIgnoreCase(estado);
                            boolean sinValorar  = !doc.contains("valoracionProyecto");

                            if (esPagada && sinValorar && sepIdParaValorar == null) {
                                sepIdParaValorar     = doc.getId();
                                proyectoIdParaValorar = doc.getString("proyectoId");
                                nombreParaValorar    = nombreProyecto;
                            }
                        }
                    }

                    ChipGroup chipGroup = findViewById(R.id.chipGroupEstadoSeparacion);
                    chipGroup.check(R.id.chipSepTodos);
                    adapter = new SeparacionAdapter(listaSeparacionesCompletas);
                    rvSeparaciones.setAdapter(adapter);


                    if (sepIdParaValorar != null) {
                        if (getLifecycle().getCurrentState().isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
                            mostrarDialogValoracion(sepIdParaValorar, proyectoIdParaValorar, nombreParaValorar, uid);
                        }
                    }
                });
    }
    private void mostrarDialogValoracion(String sepId, String proyectoId,
                                         String nombreProyecto, String uid) {
        ProyectoValoracionBottomSheet sheet = ProyectoValoracionBottomSheet.newInstance(
                sepId, proyectoId, nombreProyecto, uid);
        sheet.show(getSupportFragmentManager(), "valoracion_proyecto");
    }

    private void configurarFiltrosChips() {
        ChipGroup chipGroupEstado = findViewById(R.id.chipGroupEstadoSeparacion);

        chipGroupEstado.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);

            if (checkedId == R.id.chipSepTodos) filtrarLista("todos");
            else if (checkedId == R.id.chipSepProceso) filtrarLista("en proceso");
            else if (checkedId == R.id.chipSepAprobadas) filtrarLista("aprobada");
            else if (checkedId == R.id.chipSepRechazadas) filtrarLista("rechazada");
        });
    }

    private void filtrarLista(String estadoFiltro) {
        if (estadoFiltro.equals("todos")) {
            adapter = new SeparacionAdapter(listaSeparacionesCompletas);
        } else {
            List<Separacion> listaFiltrada = new ArrayList<>();
            for (Separacion sep : listaSeparacionesCompletas) {
                String estadoLimpio = sep.getEstado().trim().toLowerCase();
                if (estadoFiltro.equals("rechazada") && (estadoLimpio.equals("no aprobada") || estadoLimpio.equals("rechazada"))) {
                    listaFiltrada.add(sep);
                } else if (estadoLimpio.equals(estadoFiltro)) {
                    listaFiltrada.add(sep);
                }
            }
            adapter = new SeparacionAdapter(listaFiltrada);
        }
        rvSeparaciones.setAdapter(adapter);
    }

    private void configurarNavegacion() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        bottomNav.setSelectedItemId(R.id.nav_separaciones);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) { startActivity(new Intent(this, ClienteHomeActivity.class)); finish(); return true; }
            else if (id == R.id.nav_citas) { startActivity(new Intent(this, ClienteCitasActivity.class)); finish(); return true; }
            else if (id == R.id.nav_chat) { startActivity(new Intent(this, ClienteMensajesActivity.class)); finish(); return true; }
            else if (id == R.id.nav_perfil) { startActivity(new Intent(this, ClientePerfilClienteActivity.class)); return true; }
            else if (id == R.id.nav_separaciones) { return true; }
            return false;
        });
    }
}
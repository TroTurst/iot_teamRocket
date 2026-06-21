package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Cita;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClienteCitasActivity extends AppCompatActivity {

    private RecyclerView rvCitas;
    private CitasAdapter adapter;
    private List<Cita> listaCitasCompletas = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_citas_cliente);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvCitas = findViewById(R.id.rvCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CitasAdapter(listaCitasCompletas);
        rvCitas.setAdapter(adapter);

        configurarNavegacion();
        configurarFiltrosChips();

        cargarCitasDeFirestore();
    }

    private void cargarCitasDeFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        String clienteId = currentUser.getUid();

        db.collection("citas")
                .whereEqualTo("clienteId", clienteId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firebase", "Error al escuchar citas", error);
                        return;
                    }

                    listaCitasCompletas.clear();

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            String estado = doc.getString("estado");
                            String nombreProyecto = doc.getString("nombreProyecto");

                            String fechaHoraStr = "Fecha por definir";
                            Timestamp tsInicio = doc.getTimestamp("fechaHoraInicio");
                            if (tsInicio != null) {
                                Date date = tsInicio.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.getDefault());
                                fechaHoraStr = sdf.format(date);
                            }

                            Cita cita = new Cita(
                                    estado != null ? estado : "Pendiente",
                                    nombreProyecto != null ? nombreProyecto : "Proyecto Desconocido",
                                    fechaHoraStr,
                                    "GALEON INMOBILIARIA"
                            );

                            cita.setId(doc.getId());

                            listaCitasCompletas.add(cita);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    private void configurarFiltrosChips() {
        ChipGroup chipGroupEstado = findViewById(R.id.chipGroupEstado);

        chipGroupEstado.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }

            int checkedId = checkedIds.get(0);

            if (checkedId == R.id.chipTodos) {
                filtrarLista("todos");
            } else if (checkedId == R.id.chipConfirmadas) {
                filtrarLista("confirmada");
            } else if (checkedId == R.id.chipCanceladas) {
                filtrarLista("cancelada");
            }
        });
    }
    private void filtrarLista(String estadoFiltro) {
        if (estadoFiltro.equals("todos")) {
            adapter = new CitasAdapter(listaCitasCompletas);
        } else {
            List<Cita> listaFiltrada = new ArrayList<>();
            for (Cita cita : listaCitasCompletas) {
                if (cita.getEstado().equalsIgnoreCase(estadoFiltro)) {
                    listaFiltrada.add(cita);
                }
            }
            adapter = new CitasAdapter(listaFiltrada);
        }
        rvCitas.setAdapter(adapter);
    }

    private void configurarNavegacion() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        bottomNav.setSelectedItemId(R.id.nav_citas);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, ClienteHomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_citas) {
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ClienteMensajesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, ClientePerfilClienteActivity.class));
                return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}
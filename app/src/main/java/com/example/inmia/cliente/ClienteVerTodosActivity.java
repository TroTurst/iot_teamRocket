package com.example.inmia.cliente;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClienteVerTodosActivity extends AppCompatActivity {

    private RecyclerView rvProyectosAll;
    private EditText etSearch;
    private ImageButton btnClearSearch;
    private ChipGroup chipGroupFilters;
    private TextView tvResultCount;
    private LinearLayout layoutEmpty;
    private FrameLayout btnBack;

    private FirebaseFirestore db;
    private ProyectosAdapter adapter;
    private List<Proyecto> proyectosMostrados = new ArrayList<>();
    private List<Proyecto> proyectosOriginales = new ArrayList<>();

    private String filtroActivo = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_cliente_ver_todos);

        db = FirebaseFirestore.getInstance();

        inicializarVistas();
        configurarListeners();
        cargarProyectos();
    }

    private void inicializarVistas() {
        btnBack         = findViewById(R.id.btnBack);
        etSearch        = findViewById(R.id.etSearch);
        btnClearSearch  = findViewById(R.id.btnClearSearch);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        tvResultCount   = findViewById(R.id.tvResultCount);
        layoutEmpty     = findViewById(R.id.layoutEmpty);

        rvProyectosAll = findViewById(R.id.rvProyectosAll);
        rvProyectosAll.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ProyectosAdapter(proyectosMostrados);
        rvProyectosAll.setAdapter(adapter);
    }


    private void configurarListeners() {

        btnBack.setOnClickListener(v -> finish());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                aplicarFiltros();
            }
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
        });

        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipAll)       filtroActivo = "Todos";
            else if (checkedId == R.id.chipPreventa) filtroActivo = "Preventa";
            else if (checkedId == R.id.chipPlanos)   filtroActivo = "Planos";
            else if (checkedId == R.id.chipVenta)    filtroActivo = "Venta";

            aplicarFiltros();
        });
    }

    private void cargarProyectos() {
        db.collection("proyectos")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.w("VerTodos", "Error cargando proyectos", error);
                        Toast.makeText(this, "Error al cargar proyectos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshot == null) return;

                    proyectosOriginales.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Proyecto p = new Proyecto();
                        p.setId(doc.getId());
                        p.setNombre(doc.getString("nombre"));

                        Map<String, Object> ubicacionMap = (Map<String, Object>) doc.get("ubicacion");
                        p.setUbicacion(ubicacionMap != null && ubicacionMap.containsKey("direccion")
                                ? (String) ubicacionMap.get("direccion")
                                : "Ubicación no disponible");

                        String estadoRaw = doc.getString("estado");
                        if ("en_planos".equals(estadoRaw))       p.setEstadoProyecto("Planos");
                        else if ("en_preventa".equals(estadoRaw)) p.setEstadoProyecto("Preventa");
                        else                                       p.setEstadoProyecto("Venta");

                        p.setImagenHeroPrincipal(R.drawable.onboarding1);

                        List<Map<String, Object>> tipologiasData = (List<Map<String, Object>>) doc.get("tipologias");
                        List<Tipologia> listaTipologias = new ArrayList<>();
                        if (tipologiasData != null) {
                            for (Map<String, Object> map : tipologiasData) {
                                Tipologia t = new Tipologia();
                                if (map.containsKey("precio")) t.setPrecio(String.valueOf(map.get("precio")));
                                listaTipologias.add(t);
                            }
                        }
                        p.setTipologias(listaTipologias);

                        proyectosOriginales.add(p);
                    }

                    aplicarFiltros();
                });
    }


    private void aplicarFiltros() {
        String textoBusqueda = etSearch.getText().toString().toLowerCase().trim();

        proyectosMostrados.clear();

        for (Proyecto p : proyectosOriginales) {
            boolean pasaChip = filtroActivo.equals("Todos")
                    || filtroActivo.equalsIgnoreCase(p.getEstadoProyecto());

            boolean pasaTexto = textoBusqueda.isEmpty()
                    || (p.getNombre() != null && p.getNombre().toLowerCase().contains(textoBusqueda))
                    || (p.getUbicacion() != null && p.getUbicacion().toLowerCase().contains(textoBusqueda));

            if (pasaChip && pasaTexto) proyectosMostrados.add(p);
        }

        adapter.notifyDataSetChanged();

        int total = proyectosMostrados.size();
        tvResultCount.setText(total + (total == 1 ? " proyecto" : " proyectos"));
        layoutEmpty.setVisibility(total == 0 ? View.VISIBLE : View.GONE);
        rvProyectosAll.setVisibility(total == 0 ? View.GONE : View.VISIBLE);
    }
}
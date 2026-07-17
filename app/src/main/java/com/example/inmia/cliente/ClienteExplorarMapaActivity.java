package com.example.inmia.cliente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteExplorarMapaActivity extends AppCompatActivity {

    private MapView mapaReal;
    private MaterialCardView cardFloatingProperty;
    private AutoCompleteTextView etSearch;
    private ImageView btnClearSearch;
    private TextView tvTextoUbicacion, tvTextoPrecio, tvTextoHabitaciones;
    private TextView tvPropName, tvPropPrice, tvPropDesc;

    private FirebaseFirestore db;
    private String proyectoIdSeleccionado;
    private String proyectoNombreSeleccionado;


    private static final Map<String, GeoPoint> DISTRITOS_LIMA = new HashMap<String, GeoPoint>() {{
        put("Miraflores",        new GeoPoint(-18.1174, -70.2506));
        put("San Isidro",        new GeoPoint(-12.0978, -77.0350));
        put("Surco",             new GeoPoint(-12.1494, -76.9990));
        put("La Molina",         new GeoPoint(-12.0843, -76.9453));
        put("San Borja",         new GeoPoint(-12.1042, -77.0011));
        put("Barranco",          new GeoPoint(-12.1486, -77.0214));
        put("Lince",             new GeoPoint(-12.0837, -77.0356));
        put("Jesús María",       new GeoPoint(-12.0711, -77.0465));
        put("Magdalena del Mar", new GeoPoint(-12.0900, -77.0700));
        put("San Miguel",        new GeoPoint(-12.0775, -77.0903));
        put("Pueblo Libre",      new GeoPoint(-12.0733, -77.0633));
        put("Breña",             new GeoPoint(-12.0594, -77.0483));
        put("Rímac",             new GeoPoint(-12.0302, -77.0275));
        put("Lima Cercado",      new GeoPoint(-12.0432, -77.0282));
        put("La Victoria",       new GeoPoint(-12.0631, -77.0158));
        put("El Agustino",       new GeoPoint(-12.0417, -76.9944));
        put("Ate",               new GeoPoint(-12.0261, -76.9117));
        put("Santa Anita",       new GeoPoint(-12.0478, -76.9725));
        put("San Juan de Lurigancho", new GeoPoint(-11.9833, -77.0000));
        put("Comas",             new GeoPoint(-11.9411, -77.0536));
        put("Independencia",     new GeoPoint(-11.9908, -77.0533));
        put("Los Olivos",        new GeoPoint(-11.9722, -77.0753));
        put("San Martín de Porres", new GeoPoint(-12.0050, -77.0900));
        put("Callao",            new GeoPoint(-12.0561, -77.1183));
        put("Chorrillos",        new GeoPoint(-12.1678, -77.0178));
        put("Villa El Salvador", new GeoPoint(-12.2131, -76.9428));
        put("Villa María del Triunfo", new GeoPoint(-12.1658, -76.9328));
        put("San Juan de Miraflores", new GeoPoint(-12.1575, -76.9742));
        put("Surquillo",         new GeoPoint(-12.1139, -77.0158));
        put("Santiago de Surco", new GeoPoint(-12.1494, -76.9990));
        put("Pachacámac",        new GeoPoint(-12.2181, -76.8703));
        put("Lurigancho",        new GeoPoint(-11.9217, -76.8950));
        put("Carabayllo",        new GeoPoint(-11.8578, -77.0217));
        put("Puente Piedra",     new GeoPoint(-11.8681, -77.0703));
        put("Ancón",             new GeoPoint(-11.7703, -77.1592));
        put("Santa Rosa",        new GeoPoint(-11.7894, -77.1736));
    }};

    // BoundingBox de Lima metropolitana (para filtrar proyectos fuera de Lima)
    private static final double LIMA_NORTE =  -11.70;
    private static final double LIMA_SUR   =  -12.35;
    private static final double LIMA_ESTE  =  -76.80;
    private static final double LIMA_OESTE =  -77.25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        Context ctx = getApplicationContext();
        SharedPreferences prefs = ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE);
        Configuration.getInstance().load(ctx, prefs);
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_explorar_mapa_cliente);

        db = FirebaseFirestore.getInstance();

        inicializarVistas();
        configurarMapa();
        configurarBuscadorDistritos();
        cargarProyectosEnMapa();
        configurarListeners();
    }


    private void inicializarVistas() {
        mapaReal             = findViewById(R.id.mapaReal);
        cardFloatingProperty = findViewById(R.id.cardFloatingProperty);
        btnClearSearch       = findViewById(R.id.btnClearSearch);

        tvPropName           = findViewById(R.id.tvPropName);
        tvPropPrice          = findViewById(R.id.tvPropPrice);
        tvPropDesc           = findViewById(R.id.tvPropDesc);


        etSearch = findViewById(R.id.etSearch);

        cardFloatingProperty.setVisibility(View.GONE);
        findViewById(R.id.mapPin).setVisibility(View.GONE);
    }


    private void configurarMapa() {
        mapaReal.setTileSource(TileSourceFactory.MAPNIK);
        mapaReal.setMultiTouchControls(true);
        mapaReal.getController().setZoom(12.0);
        mapaReal.getController().setCenter(new GeoPoint(-12.046374, -77.042793));

        // Limitar scroll solo a Lima metropolitana
        mapaReal.setScrollableAreaLimitDouble(new BoundingBox(
                LIMA_NORTE, LIMA_ESTE, LIMA_SUR, LIMA_OESTE));
        mapaReal.setMinZoomLevel(10.0);
    }


    private void configurarBuscadorDistritos() {
        List<String> distritos = new ArrayList<>(DISTRITOS_LIMA.keySet());
        java.util.Collections.sort(distritos);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, distritos);

        etSearch.setAdapter(adapter);
        etSearch.setThreshold(1);

        etSearch.setOnItemClickListener((parent, view, position, id) -> {
            String distrito = (String) parent.getItemAtPosition(position);
            irADistrito(distrito);
            ocultarTeclado();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                String texto = s.toString().trim();
                if (DISTRITOS_LIMA.containsKey(texto)) {
                    irADistrito(texto);
                }
            }
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
            mapaReal.getController().animateTo(new GeoPoint(-12.046374, -77.042793));
            mapaReal.getController().setZoom(12.0);
        });
    }

    private void irADistrito(String distrito) {
        GeoPoint punto = DISTRITOS_LIMA.get(distrito);
        if (punto != null) {
            mapaReal.getController().animateTo(punto);
            mapaReal.getController().setZoom(15.0);
            cardFloatingProperty.setVisibility(View.GONE);
        }
    }

    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && etSearch != null) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }


    private void cargarProyectosEnMapa() {
        db.collection("proyectos").get().addOnSuccessListener(snapshot -> {
            mapaReal.getOverlays().clear();

            for (QueryDocumentSnapshot doc : snapshot) {
                String nombre    = doc.getString("nombre");
                String id        = doc.getId();
                String descripcion = doc.getString("descripcion");

                // Precio mínimo
                String precioTexto = "Consultar";
                List<Map<String, Object>> tipologias = (List<Map<String, Object>>) doc.get("tipologias");
                if (tipologias != null && !tipologias.isEmpty()) {
                    double min = Double.MAX_VALUE;
                    for (Map<String, Object> t : tipologias) {
                        if (t.containsKey("precio")) {
                            try {
                                double p = ((Number) t.get("precio")).doubleValue();
                                if (p < min) min = p;
                            } catch (Exception ignored) {}
                        }
                    }
                    if (min != Double.MAX_VALUE) {
                        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.US);
                        precioTexto = "S/. " + nf.format((long) min);
                    }
                }

                Map<String, Object> ubicacion = (Map<String, Object>) doc.get("ubicacion");
                if (ubicacion != null) {
                    double lat = 0.0;
                    double lng = 0.0;
                    boolean tieneCoordenadas = false;

                    if (ubicacion.containsKey("latitud") && ubicacion.containsKey("longitud")) {
                        lat = ((Number) ubicacion.get("latitud")).doubleValue();
                        lng = ((Number) ubicacion.get("longitud")).doubleValue();
                        tieneCoordenadas = true;
                    }
                    else if (ubicacion.containsKey("lat") && ubicacion.containsKey("lng")) {
                        lat = ((Number) ubicacion.get("lat")).doubleValue();
                        lng = ((Number) ubicacion.get("lng")).doubleValue();
                        tieneCoordenadas = true;
                    }

                    if (tieneCoordenadas) {
                        agregarMarker(id, nombre, descripcion, precioTexto, lat, lng);
                    }
                }
            }

            mapaReal.invalidate();
        });
    }

    private void agregarMarker(String id, String nombre, String descripcion,
                               String precio, double lat, double lng) {
        Marker marker = new Marker(mapaReal);
        marker.setPosition(new GeoPoint(lat, lng));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(nombre);

        Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_map_pin);
        if (icon != null) marker.setIcon(icon);

        final String precioFinal = precio;
        final String descFinal   = descripcion != null ? descripcion : "Ver detalles del proyecto";

        marker.setOnMarkerClickListener((m, mapView) -> {
            proyectoIdSeleccionado     = id;
            proyectoNombreSeleccionado = nombre;

            tvPropName.setText(nombre);
            tvPropPrice.setText(precioFinal);
            tvPropDesc.setText(descFinal);

            cardFloatingProperty.setVisibility(View.VISIBLE);
            mapaReal.getController().animateTo(new GeoPoint(lat, lng));
            return true;
        });

        mapaReal.getOverlays().add(marker);
    }


    private void configurarListeners() {

        cardFloatingProperty.setOnClickListener(v -> {
            if (proyectoIdSeleccionado != null) {
                Intent intent = new Intent(this, ClienteDetallePropiedadActivity.class);
                intent.putExtra("PROYECTO_ID", proyectoIdSeleccionado);
                intent.putExtra("PROYECTO_NOMBRE", proyectoNombreSeleccionado);
                startActivity(intent);
            }
        });

        mapaReal.setOnClickListener(v -> cardFloatingProperty.setVisibility(View.GONE));

        android.widget.FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());




        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        if (bottomNav == null) return;
        bottomNav.setOnItemSelectedListener(item -> {
            int navId = item.getItemId();
            if (navId == R.id.nav_inicio) {
                startActivity(new Intent(this, ClienteHomeActivity.class)); finish(); return true;
            } else if (navId == R.id.nav_citas) {
                startActivity(new Intent(this, ClienteCitasActivity.class)); finish(); return true;
            } else if (navId == R.id.nav_chat) {
                startActivity(new Intent(this, ClienteMensajesActivity.class)); finish(); return true;
            } else if (navId == R.id.nav_perfil) {
                startActivity(new Intent(this, ClientePerfilClienteActivity.class)); return true;
            } else if (navId == R.id.nav_separaciones) {
                startActivity(new Intent(this, ClienteSeparacionesActivity.class)); finish(); return true;
            }
            return false;
        });
    }


    @Override protected void onResume() { super.onResume(); if (mapaReal != null) mapaReal.onResume(); }
    @Override protected void onPause()  { super.onPause();  if (mapaReal != null) mapaReal.onPause(); }


    private void mostrarBottomSheetMasFiltros() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_mas_filtros, null);
        dialog.setContentView(view);
        dialog.show();
    }

    private void mostrarBottomSheetPrecio() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_precio, null);
        dialog.setContentView(view);

        com.google.android.material.textfield.TextInputEditText etMin = view.findViewById(R.id.etPrecioMin);
        com.google.android.material.textfield.TextInputEditText etMax = view.findViewById(R.id.etPrecioMax);
        Button btnAplicar  = view.findViewById(R.id.btnAplicarPrecio);
        TextView tvLimpiar = view.findViewById(R.id.tvLimpiarPrecio);

        btnAplicar.setOnClickListener(v -> {
            String min = etMin.getText() != null ? etMin.getText().toString() : "";
            String max = etMax.getText() != null ? etMax.getText().toString() : "";
            if (!min.isEmpty() || !max.isEmpty()) {
                tvTextoPrecio.setText("S/ " + (min.isEmpty() ? "0" : min) + " - " + (max.isEmpty() ? "Max" : max));
                tvTextoPrecio.setTextColor(android.graphics.Color.parseColor("#087A82"));
            }
            dialog.dismiss();
        });
        tvLimpiar.setOnClickListener(v -> {
            etMin.setText(""); etMax.setText("");
            tvTextoPrecio.setText("Precio");
            tvTextoPrecio.setTextColor(android.graphics.Color.parseColor("#222222"));
            dialog.dismiss();
        });
        dialog.show();
    }

    private void mostrarBottomSheetUbicacion() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_ubicacion, null);
        dialog.setContentView(view);

        ChipGroup chipGroup = view.findViewById(R.id.chipGroupUbicacion);
        Button btnAplicar   = view.findViewById(R.id.btnAplicarUbicacion);
        TextView tvLimpiar  = view.findViewById(R.id.tvLimpiarUbicacion);

        btnAplicar.setOnClickListener(v -> {
            int chipId = chipGroup.getCheckedChipId();
            if (chipId != View.NO_ID) {
                Chip chip = view.findViewById(chipId);
                String distrito = chip.getText().toString();
                tvTextoUbicacion.setText(distrito);
                tvTextoUbicacion.setTextColor(android.graphics.Color.parseColor("#087A82"));
                irADistrito(distrito);
                dialog.dismiss();
            }
        });
        tvLimpiar.setOnClickListener(v -> {
            chipGroup.clearCheck();
            tvTextoUbicacion.setText("Ubic.");
            tvTextoUbicacion.setTextColor(android.graphics.Color.parseColor("#222222"));
            dialog.dismiss();
        });
        dialog.show();
    }

    private void mostrarBottomSheetHabitaciones() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_habitaciones, null);
        dialog.setContentView(view);

        ChipGroup chipGroup = view.findViewById(R.id.chipGroupHabitaciones);
        Button btnAplicar   = view.findViewById(R.id.btnAplicarHabitaciones);
        TextView tvLimpiar  = view.findViewById(R.id.tvLimpiarHabitaciones);

        btnAplicar.setOnClickListener(v -> {
            int chipId = chipGroup.getCheckedChipId();
            if (chipId != View.NO_ID) {
                Chip chip = view.findViewById(chipId);
                tvTextoHabitaciones.setText(chip.getText() + " Hab.");
                tvTextoHabitaciones.setTextColor(android.graphics.Color.parseColor("#087A82"));
            }
            dialog.dismiss();
        });
        tvLimpiar.setOnClickListener(v -> {
            chipGroup.clearCheck();
            tvTextoHabitaciones.setText("Habit.");
            tvTextoHabitaciones.setTextColor(android.graphics.Color.parseColor("#222222"));
            dialog.dismiss();
        });
        dialog.show();
    }
}

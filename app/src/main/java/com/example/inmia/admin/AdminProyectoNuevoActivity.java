package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.adapters.SugerenciasAdapter;
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminFirestoreGateway.AdminContext;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.example.inmia.util.LogHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminProyectoNuevoActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private AdminFirestoreGateway gateway;
    private String companyId;
    private String companyName;

    private TextInputEditText etTitulo;
    private TextInputEditText etUbicacion;
    private TextInputEditText etDescripcion;
    private TextInputEditText etPrecio;
    private TextInputEditText etArea;
    private TextInputEditText etDormitorios;
    private TextInputEditText etBanos;
    private TextInputEditText etEstacionamiento;
    private Spinner spinnerEstadoProyecto;

    private SwitchMaterial switchPetFriendly;
    private SwitchMaterial switchConAscensor;

    private Spinner spinnerCertificadoEnergetico;
    private SwitchMaterial switchPatio;
    private SwitchMaterial switchTerraza;
    private SwitchMaterial switchBalcon;
    private SwitchMaterial switchAireAcondicionado;
    private SwitchMaterial switchCocinaIntegrada;
    private SwitchMaterial switchAmueblado;
    private SwitchMaterial switchPersianas;
    private TextInputEditText etClosets;
    private TextInputEditText etTipoPiso;
    private TextInputEditText etVentilacion;
    private TextInputEditText etTipoAcabados;

    private MaterialButton btnAgregarTipologia;
    private MaterialButton btnCrearProyecto;
    private RecyclerView rvTipologiasAgregadas;

    private List<Tipologia> tipologiasAgregadas = new ArrayList<>();
    private TipologiasAgregadasAdapter tipologiasAdapter;

    private static final LatLng LIMA_SW = new LatLng(-12.25, -77.20);
    private static final LatLng LIMA_NE = new LatLng(-11.85, -76.85);

    private RecyclerView rvSugerencias;
    private TextView tvDireccionSeleccionada;

    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private SugerenciasAdapter sugerenciasAdapter;

    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;

    private boolean isUpdatingAddress = false;

    private double selLat = 0;
    private double selLng = 0;
    private String selPlaceId = null;
    private String selDireccionFormateada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_proyecto_nuevo);

        gateway = new AdminFirestoreGateway();

        bottomNav = findViewById(R.id.bottomNavAdmin);
        View btnBack = findViewById(R.id.btnBackEditarProyecto);
        btnAgregarTipologia = findViewById(R.id.btnAgregarTipologia);
        btnCrearProyecto = findViewById(R.id.btnCrearProyecto);
        rvTipologiasAgregadas = findViewById(R.id.rvTipologiasAgregadas);

        etTitulo = findViewById(R.id.etTituloProyecto);
        etUbicacion = findViewById(R.id.etUbicacionProyecto);
        etDescripcion = findViewById(R.id.etDescripcionProyecto);
        etPrecio = findViewById(R.id.etPrecioProyecto);
        etArea = findViewById(R.id.etAreaProyecto);
        etDormitorios = findViewById(R.id.etDormitoriosProyecto);
        etBanos = findViewById(R.id.etBanosProyecto);
        etEstacionamiento = findViewById(R.id.etEstacionamientoProyecto);
        spinnerEstadoProyecto = findViewById(R.id.spinnerEstadoProyecto);

        switchPetFriendly = findViewById(R.id.switchPetFriendly);
        switchConAscensor = findViewById(R.id.switchConAscensor);

        spinnerCertificadoEnergetico = findViewById(R.id.spinnerCertificadoEnergetico);
        switchPatio = findViewById(R.id.switchPatio);
        switchTerraza = findViewById(R.id.switchTerraza);
        switchBalcon = findViewById(R.id.switchBalcon);
        switchAireAcondicionado = findViewById(R.id.switchAireAcondicionado);
        switchCocinaIntegrada = findViewById(R.id.switchCocinaIntegrada);
        switchAmueblado = findViewById(R.id.switchAmueblado);
        switchPersianas = findViewById(R.id.switchPersianas);
        etClosets = findViewById(R.id.etClosets);
        etTipoPiso = findViewById(R.id.etTipoPiso);
        etVentilacion = findViewById(R.id.etVentilacion);
        etTipoAcabados = findViewById(R.id.etTipoAcabados);

        rvTipologiasAgregadas.setLayoutManager(new LinearLayoutManager(this));
        tipologiasAdapter = new TipologiasAgregadasAdapter(tipologiasAgregadas,
                tipologia -> {
                    tipologiasAgregadas.remove(tipologia);
                    tipologiasAdapter.notifyDataSetChanged();
                },
                (tipologia, position) -> {
                }
        );
        rvTipologiasAgregadas.setAdapter(tipologiasAdapter);

        configurarSpinners();

        rvSugerencias = findViewById(R.id.rvSugerencias);
        tvDireccionSeleccionada = findViewById(R.id.tvDireccionSeleccionada);

        placesClient = com.google.android.libraries.places.api.Places.createClient(this);
        sessionToken = AutocompleteSessionToken.newInstance();

        sugerenciasAdapter = new SugerenciasAdapter(this::onSugerenciaSeleccionada);
        rvSugerencias.setLayoutManager(new LinearLayoutManager(this));
        rvSugerencias.setAdapter(sugerenciasAdapter);

        etUbicacion.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdatingAddress) return;
                selPlaceId = null;
                tvDireccionSeleccionada.setVisibility(View.GONE);

                if (debounceRunnable != null) {
                    debounceHandler.removeCallbacks(debounceRunnable);
                }
                String query = s.toString().trim();
                if (query.length() < 3) {
                    rvSugerencias.setVisibility(View.GONE);
                    return;
                }
                debounceRunnable = () -> buscarSugerencias(query);
                debounceHandler.postDelayed(debounceRunnable, 300);
            }
        });

        cargarDatosDesdeIntent();
        resolverContextoAdmin();

        btnBack.setOnClickListener(v -> finish());

        btnAgregarTipologia.setOnClickListener(v -> agregarTipologiaDesdeFormulario());

        btnCrearProyecto.setOnClickListener(v -> crearProyecto());

        bottomNav.setSelectedItemId(R.id.nav_proyectos);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                navegarATab(AdminHomeActivity.class);
                return true;
            } else if (id == R.id.nav_proyectos) {
                return true;
            } else if (id == R.id.nav_asesores) {
                navegarATab(AdminAsesoresActivity.class);
                return true;
            } else if (id == R.id.nav_reportes) {
                navegarATab(AdminReportesActivity.class);
                return true;
            } else if (id == R.id.nav_perfil) {
                navegarATab(AdminPerfilActivity.class);
                return true;
            }
            return false;
        });
    }

    private void resolverContextoAdmin() {
        gateway.resolveAdminContextByEmail(AdminSessionDefaults.DEFAULT_ADMIN_EMAIL, new AdminFirestoreGateway.FirestoreCallback<AdminContext>() {
            @Override
            public void onSuccess(AdminContext context) {
                companyId = context.getCompanyId();
                companyName = context.getCompanyName();
            }

            @Override
            public void onError(Exception e) {
                companyId = AdminSessionDefaults.DEFAULT_COMPANY_ID;
                companyName = "Inmobiliaria";
            }
        });
    }

    private void cargarDatosDesdeIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String titulo = intent.getStringExtra(EXTRA_PROYECTO_TITULO);
            String ubicacion = intent.getStringExtra(EXTRA_UBICACION);
            String descripcion = intent.getStringExtra(EXTRA_DESCRIPCION);
            if (titulo != null && !titulo.isEmpty()) etTitulo.setText(titulo);
            if (ubicacion != null && !ubicacion.isEmpty()) etUbicacion.setText(ubicacion);
            if (descripcion != null && !descripcion.isEmpty()) etDescripcion.setText(descripcion);
            String precio = intent.getStringExtra(EXTRA_PRECIO);
            String area = intent.getStringExtra(EXTRA_AREA);
            String dormitorios = intent.getStringExtra(EXTRA_DORMITORIOS);
            String banos = intent.getStringExtra(EXTRA_BANOS);
            String estacionamiento = intent.getStringExtra(EXTRA_ESTACIONAMIENTO);
            String estado = intent.getStringExtra(EXTRA_ESTADO);
            if (precio != null && !precio.isEmpty()) etPrecio.setText(precio);
            if (area != null && !area.isEmpty()) etArea.setText(area);
            if (dormitorios != null && !dormitorios.isEmpty()) etDormitorios.setText(dormitorios);
            if (banos != null && !banos.isEmpty()) etBanos.setText(banos);
            if (estacionamiento != null && !estacionamiento.isEmpty()) etEstacionamiento.setText(estacionamiento);
        }
    }

    private void configurarSpinners() {
        String[] estadosProyecto = {"En planos", "En preventa", "En venta", "Entregado"};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estadosProyecto);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstadoProyecto.setAdapter(estadoAdapter);

        String[] certificados = {"A+", "A", "B+", "B", "C+", "C", "D"};
        ArrayAdapter<String> certificadoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, certificados);
        certificadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCertificadoEnergetico.setAdapter(certificadoAdapter);
    }

    private void agregarTipologiaDesdeFormulario() {
        String area = texto(etArea);
        String dormitoriosStr = texto(etDormitorios);
        String banosStr = texto(etBanos);
        String precio = texto(etPrecio);
        String descripcion = texto(etDescripcion);
        String estacionamiento = texto(etEstacionamiento);
        String estado = spinnerEstadoProyecto != null ? spinnerEstadoProyecto.getSelectedItem().toString() : "Disponible";

        if (area.isEmpty() || dormitoriosStr.isEmpty()) {
            Toast.makeText(this, "Ingresa área y dormitorios mínimo", Toast.LENGTH_SHORT).show();
            return;
        }

        int dormitorios = parseInt(dormitoriosStr);
        int banos = banosStr.isEmpty() ? 0 : parseInt(banosStr);

        String nombre = area + " m² · " + dormitorios + "d";

        String certificado = spinnerCertificadoEnergetico != null ? spinnerCertificadoEnergetico.getSelectedItem().toString() : "B";

        Tipologia tipologia = new Tipologia(
                "tip_" + System.currentTimeMillis(),
                nombre,
                descripcion.isEmpty() ? "Tipología " + nombre : descripcion,
                area,
                dormitoriosStr,
                banosStr,
                estacionamiento.isEmpty() ? "-" : estacionamiento,
                precio.isEmpty() ? "-" : precio,
                estado.isEmpty() ? "Disponible" : estado,
                R.drawable.onboarding1,
                new int[]{R.drawable.onboarding1},
                switchPatio != null && switchPatio.isChecked(),
                certificado,
                switchTerraza != null && switchTerraza.isChecked(),
                switchBalcon != null && switchBalcon.isChecked(),
                switchAireAcondicionado != null && switchAireAcondicionado.isChecked(),
                switchCocinaIntegrada != null && switchCocinaIntegrada.isChecked(),
                parseInt(texto(etClosets)),
                texto(etTipoPiso).isEmpty() ? "estandar" : texto(etTipoPiso),
                switchAmueblado != null && switchAmueblado.isChecked(),
                texto(etVentilacion).isEmpty() ? "natural" : texto(etVentilacion),
                switchPersianas != null && switchPersianas.isChecked(),
                texto(etTipoAcabados).isEmpty() ? "basico" : texto(etTipoAcabados)
        );

        tipologiasAgregadas.add(tipologia);
        tipologiasAdapter.notifyItemInserted(tipologiasAgregadas.size() - 1);

        Toast.makeText(this, "Tipología agregada", Toast.LENGTH_SHORT).show();

        limpiarCamposTipologia();
    }

    private void limpiarCamposTipologia() {
        etArea.setText("");
        etDormitorios.setText("");
        etBanos.setText("");
        etPrecio.setText("");
        etDescripcion.setText("");
        etEstacionamiento.setText("");
        if (switchPatio != null) switchPatio.setChecked(false);
        if (switchTerraza != null) switchTerraza.setChecked(false);
        if (switchBalcon != null) switchBalcon.setChecked(false);
        if (switchAireAcondicionado != null) switchAireAcondicionado.setChecked(false);
        if (switchCocinaIntegrada != null) switchCocinaIntegrada.setChecked(false);
        if (switchAmueblado != null) switchAmueblado.setChecked(false);
        if (switchPersianas != null) switchPersianas.setChecked(false);
        etClosets.setText("");
        if (spinnerCertificadoEnergetico != null) spinnerCertificadoEnergetico.setSelection(1);
        etTipoPiso.setText("");
        etVentilacion.setText("");
        etTipoAcabados.setText("");
    }

    private void crearProyecto() {
        String titulo = texto(etTitulo);
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre del proyecto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tipologiasAgregadas.isEmpty()) {
            Toast.makeText(this, "Agrega al menos una tipología", Toast.LENGTH_SHORT).show();
            return;
        }

        String ubicacion = texto(etUbicacion);
        if (ubicacion.isEmpty()) {
            Toast.makeText(this, "Ingresa la dirección del proyecto", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selPlaceId == null) {
            Toast.makeText(this, "Selecciona una dirección de la lista de sugerencias", Toast.LENGTH_LONG).show();
            return;
        }

        String descripcion = texto(etDescripcion);
        String estado = spinnerEstadoProyecto != null ? spinnerEstadoProyecto.getSelectedItem().toString() : "En planos";

        boolean petFriendly = switchPetFriendly != null && switchPetFriendly.isChecked();
        boolean conAscensor = switchConAscensor != null && switchConAscensor.isChecked();

        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(titulo);
        proyecto.setUbicacion(ubicacion.isEmpty() ? "Sin ubicación" : ubicacion);
        proyecto.setLatitud(selLat);
        proyecto.setLongitud(selLng);
        proyecto.setDescripcion(descripcion.isEmpty() ? "Sin descripción" : descripcion);
        proyecto.setEstadoProyecto(estado.isEmpty() ? "En planos" : estado);
        proyecto.setInmobiliaria(companyName != null ? companyName : "Inmobiliaria");
        proyecto.setPetFriendly(petFriendly);
        proyecto.setConAscensor(conAscensor);
        proyecto.setAntiguedad("Nuevo");
        proyecto.setTipologias(tipologiasAgregadas);
        proyecto.setTipologiaPrincipal(tipologiasAgregadas.get(0));
        proyecto.setImagenHeroPrincipal(R.drawable.onboarding1);

        gateway.saveProject(proyecto, companyId, new AdminFirestoreGateway.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String projectId) {
                LogHelper.registrar(
                        "Se creó el proyecto " + titulo
                                + (companyName != null ? " en " + companyName : ""),
                        com.example.inmia.models.Log.TIPO_PROYECTO,
                        LogHelper.ROL_ADMIN);
                runOnUiThread(() -> {
                    Toast.makeText(AdminProyectoNuevoActivity.this, "Proyecto creado exitosamente", Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminProyectoNuevoActivity.this, "Error al crear proyecto: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void buscarSugerencias(String query) {
        RectangularBounds bounds = RectangularBounds.newInstance(LIMA_SW, LIMA_NE);

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setLocationBias(bounds)
                .setCountries("PE")
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    sugerenciasAdapter.setData(response.getAutocompletePredictions());
                    rvSugerencias.setVisibility(
                            response.getAutocompletePredictions().isEmpty() ? View.GONE : View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("AdminNuevo", "Autocomplete falló", e);
                    rvSugerencias.setVisibility(View.GONE);
                });
    }

    private void onSugerenciaSeleccionada(com.google.android.libraries.places.api.model.AutocompletePrediction p) {
        rvSugerencias.setVisibility(View.GONE);
        String fullText = p.getFullText(null).toString();
        isUpdatingAddress = true;
        etUbicacion.setText(fullText);
        etUbicacion.setSelection(fullText.length());
        isUpdatingAddress = false;

        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
        );

        FetchPlaceRequest fetchRequest = FetchPlaceRequest.builder(p.getPlaceId(), fields)
                .setSessionToken(sessionToken)
                .build();

        placesClient.fetchPlace(fetchRequest)
                .addOnSuccessListener(response -> {
                    Place place = response.getPlace();
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        selLat = latLng.latitude;
                        selLng = latLng.longitude;
                    }
                    selPlaceId = place.getId();
                    selDireccionFormateada = place.getAddress();

                    tvDireccionSeleccionada.setText(selDireccionFormateada
                            + "\n(" + selLat + ", " + selLng + ")");
                    tvDireccionSeleccionada.setVisibility(View.VISIBLE);

                    sessionToken = AutocompleteSessionToken.newInstance();
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("AdminNuevo", "fetchPlace falló", e);
                    Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                });
    }

    private String texto(TextInputEditText editText) {
        return editText != null && editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    public static final String EXTRA_PROYECTO_TITULO = "extra_proyecto_titulo";
    public static final String EXTRA_UBICACION = "extra_ubicacion";
    public static final String EXTRA_DESCRIPCION = "extra_descripcion";
    public static final String EXTRA_PRECIO = "extra_precio";
    public static final String EXTRA_AREA = "extra_area";
    public static final String EXTRA_DORMITORIOS = "extra_dormitorios";
    public static final String EXTRA_BANOS = "extra_banos";
    public static final String EXTRA_ESTACIONAMIENTO = "extra_estacionamiento";
    public static final String EXTRA_ESTADO = "extra_estado";
}
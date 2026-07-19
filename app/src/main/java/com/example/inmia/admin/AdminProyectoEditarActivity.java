package com.example.inmia.admin;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminProyectoEditarActivity extends AppCompatActivity {

    public static final String EXTRA_PROYECTO_ID = "extra_proyecto_id";

    private BottomNavigationView bottomNav;
    private AdminFirestoreGateway gateway;
    private String companyId;
    private String companyName;
    private String proyectoId;
    private View btnEditarDatosProyecto;
    private ListenerRegistration projectListener;

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
    private MaterialButton btnActualizarProyecto;
    private RecyclerView rvTipologiasAgregadas;

    private List<Tipologia> tipologiasAgregadas = new ArrayList<>();
    private TipologiasAgregadasAdapter tipologiasAdapter;
    private int tipologiaEditandoIndex = -1;

    private Proyecto proyectoOriginal;

    private static final LatLng LIMA_SW = new LatLng(-12.25, -77.20);
    private static final LatLng LIMA_NE = new LatLng(-11.85, -76.85);

    private PlacesClient placesClient;

    private double selLat = 0;
    private double selLng = 0;
    private String selPlaceId = null;
    private String selDistrito = null;

    private Uri heroFotoUri = null;
    private final List<Uri> newGaleriaUris = new ArrayList<>();
    private List<String> existingImageUrls = new ArrayList<>();
    private ImageView imgHeroProyectoEditar;
    private RecyclerView rvGaleriaEditar;
    private AdminProyectoNuevoActivity.GaleriaFotosAdapter galeriaAdapter;
    private Tipologia tipologiaPhotoTarget = null;

    private String extractDistrict(Place place) {
        if (place.getAddressComponents() == null) return "";
        List<com.google.android.libraries.places.api.model.AddressComponent> components = place.getAddressComponents().asList();
        for (com.google.android.libraries.places.api.model.AddressComponent component : components) {
            if (component.getTypes().contains("sublocality_level_1")) {
                return component.getName();
            }
        }
        for (com.google.android.libraries.places.api.model.AddressComponent component : components) {
            if (component.getTypes().contains("locality")) {
                return component.getName();
            }
        }
        return "";
    }

    private final ActivityResultLauncher<String> heroFotoLauncherEditar =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    heroFotoUri = uri;
                    Glide.with(this).load(uri).centerCrop().into(imgHeroProyectoEditar);
                }
            });

    private final ActivityResultLauncher<String> tipologiaFotoLauncherEditar =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && tipologiaPhotoTarget != null) {
                    List<String> urls = tipologiaPhotoTarget.getImagenesUrls();
                    urls.add(uri.toString());
                    tipologiaPhotoTarget.setImagenesUrls(urls);
                    tipologiasAdapter.notifyDataSetChanged();
                    tipologiaPhotoTarget = null;
                }
            });

    private final ActivityResultLauncher<String> galeriaFotoLauncherEditar =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    newGaleriaUris.add(uri);
                    if (galeriaAdapter != null) galeriaAdapter.notifyItemInserted(newGaleriaUris.size() - 1);
                }
            });

    private void subirNuevasFotosYGuardar(String projectId) {
        final List<String> projectUrls = new ArrayList<>(existingImageUrls);
        final Map<Integer, List<String>> tipUrlsMap = new HashMap<>();

        int localTipCount = 0;
        if (proyectoOriginal != null && proyectoOriginal.getTipologias() != null) {
            for (int t = 0; t < proyectoOriginal.getTipologias().size(); t++) {
                Tipologia tip = proyectoOriginal.getTipologias().get(t);
                if (tip.getImagenesUrls() != null) {
                    List<String> existing = new ArrayList<>();
                    for (String url : tip.getImagenesUrls()) {
                        if (url != null && url.startsWith("http")) existing.add(url);
                        else if (url != null && !url.isEmpty()) localTipCount++;
                    }
                    tipUrlsMap.put(t, existing);
                }
            }
        }

        final int total = (heroFotoUri != null ? 1 : 0) + newGaleriaUris.size() + localTipCount;
        final int[] completed = {0};
        final String pid = projectId;

        Runnable checkDone = () -> {
            completed[0]++;
            if (total == 0 || completed[0] == total) {
                // Build final Proyecto and do ONE updateProject call
                Proyecto p = new Proyecto();
                p.setNombre(texto(etTitulo));
                p.setDistrito(selDistrito != null ? selDistrito : "");
                p.setUbicacion(texto(etUbicacion));
                p.setLatitud(selLat);
                p.setLongitud(selLng);
                p.setDescripcion(texto(etDescripcion));
                p.setEstadoProyecto(spinnerEstadoProyecto.getSelectedItem().toString());
                p.setInmobiliaria(companyName != null ? companyName : "Inmobiliaria");
                p.setPetFriendly(switchPetFriendly.isChecked());
                p.setConAscensor(switchConAscensor.isChecked());
                p.setAntiguedad("Nuevo");
                p.setTipologias(tipologiasAgregadas);
                p.setTipologiaPrincipal(tipologiasAgregadas.get(0));
                p.setImagenesUrls(projectUrls);
                for (int i = 0; i < p.getTipologias().size(); i++) {
                    List<String> u = tipUrlsMap.get(i);
                    if (u != null) p.getTipologias().get(i).setImagenesUrls(u);
                }
                if (proyectoOriginal != null) {
                    p.setId(proyectoOriginal.getId());
                    p.setReferencia(proyectoOriginal.getReferencia());
                    p.setQrCode(proyectoOriginal.getQrCode());
                    p.setVendedores(proyectoOriginal.getVendedores() != null ? new ArrayList<>(proyectoOriginal.getVendedores()) : new ArrayList<String>());
                }

                gateway.updateProject(pid, p, companyId, new AdminFirestoreGateway.FirestoreCallback<Void>() {
                    @Override public void onSuccess(Void v) { runOnUiThread(() -> { Toast.makeText(AdminProyectoEditarActivity.this, "Proyecto actualizado", Toast.LENGTH_LONG).show(); setResult(RESULT_OK); finish(); }); }
                    @Override public void onError(Exception e) { runOnUiThread(() -> { Toast.makeText(AdminProyectoEditarActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show(); }); }
                });
            }
        };

        if (total == 0) {
            checkDone.run();
            return;
        }

        if (heroFotoUri != null) {
            StorageReference r = FirebaseStorage.getInstance().getReference().child("proyectos/" + pid + "/hero_0.jpg");
            r.putFile(heroFotoUri).addOnSuccessListener(t -> r.getDownloadUrl().addOnSuccessListener(u -> { if (!projectUrls.isEmpty()) projectUrls.set(0, u.toString()); else projectUrls.add(u.toString()); checkDone.run(); }).addOnFailureListener(e -> checkDone.run())).addOnFailureListener(e -> checkDone.run());
        }
        for (int i = 0; i < newGaleriaUris.size(); i++) {
            int fi = i;
            StorageReference r = FirebaseStorage.getInstance().getReference().child("proyectos/" + pid + "/gallery_" + (existingImageUrls.size() + fi) + ".jpg");
            r.putFile(newGaleriaUris.get(i)).addOnSuccessListener(t -> r.getDownloadUrl().addOnSuccessListener(u -> { projectUrls.add(u.toString()); checkDone.run(); }).addOnFailureListener(e -> checkDone.run())).addOnFailureListener(e -> checkDone.run());
        }
        if (proyectoOriginal != null && proyectoOriginal.getTipologias() != null) {
            for (int t = 0; t < proyectoOriginal.getTipologias().size(); t++) {
                Tipologia tip = proyectoOriginal.getTipologias().get(t);
                if (tip.getImagenesUrls() == null) continue;
                final int ft = t;
                for (int p = 0; p < tip.getImagenesUrls().size(); p++) {
                    String url = tip.getImagenesUrls().get(p);
                    if (url == null || url.isEmpty() || url.startsWith("http")) continue;
                    StorageReference r = FirebaseStorage.getInstance().getReference().child("proyectos/" + pid + "/tipologia_" + ft + "_" + p + ".jpg");
                    r.putFile(Uri.parse(url)).addOnSuccessListener(task -> r.getDownloadUrl().addOnSuccessListener(dl -> { synchronized(tipUrlsMap) { List<String> l = tipUrlsMap.get(ft); if (l == null) { l = new ArrayList<>(); tipUrlsMap.put(ft, l); } l.add(dl.toString()); } checkDone.run(); }).addOnFailureListener(e -> checkDone.run())).addOnFailureListener(e -> checkDone.run());
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_proyecto_editar);

        gateway = new AdminFirestoreGateway();

        proyectoId = getIntent() != null ? getIntent().getStringExtra(EXTRA_PROYECTO_ID) : null;

        bottomNav = findViewById(R.id.bottomNavAdmin);
        View btnBack = findViewById(R.id.btnBackEditarProyecto);
        btnEditarDatosProyecto = findViewById(R.id.btnEditarDatosProyecto);
        btnAgregarTipologia = findViewById(R.id.btnAgregarTipologia);
        btnActualizarProyecto = findViewById(R.id.btnActualizarProyecto);
        rvTipologiasAgregadas = findViewById(R.id.rvTipologiasAgregadas);

        etTitulo = findViewById(R.id.etTituloProyecto);
        etUbicacion = findViewById(R.id.etUbicacionProyecto);
        etTitulo.setEnabled(false);
        etUbicacion.setEnabled(false);
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
                    tipologiaEditandoIndex = position;
                    btnAgregarTipologia.setText("Actualizar tipología");
                    populateFormWithTipologia(tipologia);
                },
                (tipologia, position) -> {
                    tipologiaPhotoTarget = tipologia;
                    tipologiaFotoLauncherEditar.launch("image/*");
                },
                (tipologia, fotoIndex) -> {
                    List<String> urls = tipologia.getImagenesUrls();
                    if (fotoIndex >= 0 && fotoIndex < urls.size()) {
                        urls.remove(fotoIndex);
                        tipologia.setImagenesUrls(urls);
                        tipologiasAdapter.notifyDataSetChanged();
                    }
                }
        );
        rvTipologiasAgregadas.setAdapter(tipologiasAdapter);

        placesClient = com.google.android.libraries.places.api.Places.createClient(this);

        imgHeroProyectoEditar = findViewById(R.id.imgHeroProyectoEditar);
        rvGaleriaEditar = findViewById(R.id.rvGaleriaEditar);
        rvGaleriaEditar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        galeriaAdapter = new AdminProyectoNuevoActivity.GaleriaFotosAdapter(newGaleriaUris, idx -> {
            newGaleriaUris.remove(idx);
            galeriaAdapter.notifyDataSetChanged();
        });
        rvGaleriaEditar.setAdapter(galeriaAdapter);

        View cardHeroFoto = findViewById(R.id.cardHeroFotoEditar);
        cardHeroFoto.setOnClickListener(v -> heroFotoLauncherEditar.launch("image/*"));

        View btnAgregarFotoGaleria = findViewById(R.id.btnAgregarFotoGaleriaEditar);
        btnAgregarFotoGaleria.setOnClickListener(v -> galeriaFotoLauncherEditar.launch("image/*"));

        configurarSpinners();
        resolverContextoAdmin();
        cargarProyectoExistente();

        btnBack.setOnClickListener(v -> finish());

        btnEditarDatosProyecto.setOnClickListener(v -> mostrarDialogoEditarDatos());

        btnAgregarTipologia.setOnClickListener(v -> agregarTipologiaDesdeFormulario());

        btnActualizarProyecto.setOnClickListener(v -> actualizarProyecto());

        bottomNav.setSelectedItemId(R.id.nav_proyectos);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                navegarATab(AdminHomeActivity.class);
                return true;
            } else if (id == R.id.nav_proyectos) {
                navegarATab(AdminProyectosActivity.class);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (projectListener != null) {
            projectListener.remove();
            projectListener = null;
        }
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

    private void cargarProyectoExistente() {
        if (proyectoId == null || proyectoId.trim().isEmpty()) {
            Toast.makeText(this, "No se proporcionó ID del proyecto", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        projectListener = gateway.observeProjectById(proyectoId, new AdminFirestoreGateway.FirestoreCallback<Proyecto>() {
            @Override
            public void onSuccess(Proyecto proyecto) {
                proyectoOriginal = proyecto;
                
                if ("Entregado".equalsIgnoreCase(proyecto.getEstadoProyecto())) {
                    runOnUiThread(() -> {
                        Toast.makeText(AdminProyectoEditarActivity.this, 
                            "Proyecto entregado, no se puede editar", Toast.LENGTH_LONG).show();
                        finish();
                    });
                    return;
                }
                
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    etTitulo.setText(proyecto.getNombre());
                    etUbicacion.setText(proyecto.getUbicacion());
                    selLat = proyecto.getLatitud();
                    selLng = proyecto.getLongitud();
                    selPlaceId = null;
                    selDistrito = proyecto.getDistrito();
                    etDescripcion.setText(proyecto.getDescripcion());

                    // Load existing images
                    List<String> urls = proyecto.getImagenesUrls();
                    if (urls != null && !urls.isEmpty()) {
                        existingImageUrls = new ArrayList<>(urls);
                        Glide.with(AdminProyectoEditarActivity.this).load(urls.get(0))
                                .centerCrop().placeholder(R.drawable.ic_add).into(imgHeroProyectoEditar);
                    }

                    resolveProjectDistritoFromLatLng(proyecto);

                    String estado = proyecto.getEstadoProyecto();
                    if (estado != null) {
                        String[] estados = {"En planos", "En preventa", "En venta", "Entregado"};
                        for (int i = 0; i < estados.length; i++) {
                            if (estados[i].equals(estado)) {
                                spinnerEstadoProyecto.setSelection(i);
                                break;
                            }
                        }
                    }

                    switchPetFriendly.setChecked(proyecto.isPetFriendly());
                    switchConAscensor.setChecked(proyecto.isConAscensor());

                    if (proyecto.getTipologias() != null && !proyecto.getTipologias().isEmpty()) {
                        tipologiasAgregadas.addAll(proyecto.getTipologias());
                        tipologiasAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AdminProyectoEditarActivity.this, "Error al cargar proyecto", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
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

    private void populateFormWithTipologia(Tipologia tipologia) {
        etArea.setText(tipologia.getArea().replace(" m²", "").replace("m²", ""));
        etDormitorios.setText(tipologia.getDormitorios());
        etBanos.setText(tipologia.getBanos());
        etPrecio.setText(tipologia.getPrecio().replaceAll("[^\\d.]", ""));
        etDescripcion.setText(tipologia.getDescripcion());
        etEstacionamiento.setText(tipologia.getEstacionamiento());

        String[] certificados = {"A+", "A", "B+", "B", "C+", "C", "D"};
        String cert = tipologia.getCertificadoEnergetico();
        for (int i = 0; i < certificados.length; i++) {
            if (certificados[i].equals(cert)) {
                spinnerCertificadoEnergetico.setSelection(i);
                break;
            }
        }

        switchPatio.setChecked(tipologia.isPatio());
        switchTerraza.setChecked(tipologia.isTerraza());
        switchBalcon.setChecked(tipologia.isBalcon());
        switchAireAcondicionado.setChecked(tipologia.isAireAcondicionado());
        switchCocinaIntegrada.setChecked(tipologia.isCocinaIntegrada());
        switchAmueblado.setChecked(tipologia.isAmueblado());
        switchPersianas.setChecked(tipologia.isPersianasAutomaticas());
        etClosets.setText(String.valueOf(tipologia.getClosets()));
        etTipoPiso.setText(tipologia.getTipoPiso());
        etVentilacion.setText(tipologia.getVentilacion());
        etTipoAcabados.setText(tipologia.getTipoAcabados());
    }

    private void agregarTipologiaDesdeFormulario() {
        String area = texto(etArea);
        String dormitoriosStr = texto(etDormitorios);
        String banosStr = texto(etBanos);
        String precio = texto(etPrecio);
        String descripcion = texto(etDescripcion);
        String estacionamiento = texto(etEstacionamiento);
        String estado = spinnerEstadoProyecto.getSelectedItem().toString();

        Log.d("AdminEditar", "FORMULARIO: area='" + area + "', dormitorios='" + dormitoriosStr + "', precio='" + precio + "'");

        if (area.isEmpty() || dormitoriosStr.isEmpty()) {
            Toast.makeText(this, "Ingresa área y dormitorios mínimo", Toast.LENGTH_SHORT).show();
            return;
        }

        int dormitorios = parseInt(dormitoriosStr);
        int banos = banosStr.isEmpty() ? 0 : parseInt(banosStr);

        String nombre = area + " m² · " + dormitorios + "d";

        String certificado = spinnerCertificadoEnergetico.getSelectedItem().toString();

        Tipologia tipologia = new Tipologia(
                tipologiaEditandoIndex >= 0 ? tipologiasAgregadas.get(tipologiaEditandoIndex).getId() : "tip_" + System.currentTimeMillis(),
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
                switchPatio.isChecked(),
                certificado,
                switchTerraza.isChecked(),
                switchBalcon.isChecked(),
                switchAireAcondicionado.isChecked(),
                switchCocinaIntegrada.isChecked(),
                parseInt(texto(etClosets)),
                texto(etTipoPiso).isEmpty() ? "estandar" : texto(etTipoPiso),
                switchAmueblado.isChecked(),
                texto(etVentilacion).isEmpty() ? "natural" : texto(etVentilacion),
                switchPersianas.isChecked(),
                texto(etTipoAcabados).isEmpty() ? "basico" : texto(etTipoAcabados)
        );

        if (tipologiaEditandoIndex >= 0) {
            Tipologia oldTip = tipologiasAgregadas.get(tipologiaEditandoIndex);
            tipologiasAgregadas.set(tipologiaEditandoIndex, tipologia);
            Tipologia newTip = tipologiasAgregadas.get(tipologiaEditandoIndex);
            Log.d("AdminEditar", "UPDATE: old area=" + oldTip.getArea() + ", new area=" + newTip.getArea());
            tipologiasAdapter.notifyItemChanged(tipologiaEditandoIndex);
            Toast.makeText(this, "Tipología actualizada", Toast.LENGTH_SHORT).show();
        } else {
            tipologiasAgregadas.add(tipologia);
            tipologiasAdapter.notifyItemInserted(tipologiasAgregadas.size() - 1);
            Toast.makeText(this, "Tipología agregada", Toast.LENGTH_SHORT).show();
        }

        tipologiaEditandoIndex = -1;
        btnAgregarTipologia.setText("Agregar tipología");
        limpiarCamposTipologia();
    }

    private void limpiarCamposTipologia() {
        etArea.setText("");
        etDormitorios.setText("");
        etBanos.setText("");
        etPrecio.setText("");
        etDescripcion.setText("");
        etEstacionamiento.setText("");
        switchPatio.setChecked(false);
        switchTerraza.setChecked(false);
        switchBalcon.setChecked(false);
        switchAireAcondicionado.setChecked(false);
        switchCocinaIntegrada.setChecked(false);
        switchAmueblado.setChecked(false);
        switchPersianas.setChecked(false);
        etClosets.setText("");
        spinnerCertificadoEnergetico.setSelection(1);
        etTipoPiso.setText("");
        etVentilacion.setText("");
        etTipoAcabados.setText("");
    }

    private void actualizarProyecto() {
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
        String descripcion = texto(etDescripcion);
        String estado = spinnerEstadoProyecto.getSelectedItem().toString();

        boolean petFriendly = switchPetFriendly.isChecked();
        boolean conAscensor = switchConAscensor.isChecked();

        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(titulo);
        proyecto.setDistrito(selDistrito != null ? selDistrito : "");
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

        if (proyectoOriginal != null) {
            proyecto.setId(proyectoOriginal.getId());
            proyecto.setReferencia(proyectoOriginal.getReferencia());
            proyecto.setQrCode(proyectoOriginal.getQrCode());
            proyecto.setVendedores(proyectoOriginal.getVendedores() != null
                    ? new ArrayList<>(proyectoOriginal.getVendedores())
                    : new ArrayList<String>());
            }

        Log.d("AdminEditar", "tipologiasAgregadas tiene " + tipologiasAgregadas.size() + " tipologias");
        for (int i = 0; i < tipologiasAgregadas.size(); i++) {
            Tipologia t = tipologiasAgregadas.get(i);
            Log.d("AdminEditar", "Tipologia " + i + ": nombre=" + t.getNombre() + ", area=" + t.getArea() + ", precio=" + t.getPrecio());
        }
        Log.d("AdminEditar", "proyectoId a actualizar: " + proyectoId);

        btnActualizarProyecto.setEnabled(false);
        btnActualizarProyecto.setText("Subiendo fotos...");
        subirNuevasFotosYGuardar(proyectoId);
    }

    private void resolveProjectDistritoFromLatLng(Proyecto proyecto) {
        if (proyecto == null) return;
        if (proyecto.getDistrito() != null && !proyecto.getDistrito().isEmpty()) return;
        if (proyecto.getLatitud() == 0 && proyecto.getLongitud() == 0) return;

        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(proyecto.getLatitud(), proyecto.getLongitud(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String distrito = null;
                    if (address.getSubLocality() != null && !address.getSubLocality().isEmpty()) {
                        distrito = address.getSubLocality();
                    } else if (address.getLocality() != null && !address.getLocality().isEmpty()) {
                        distrito = address.getLocality();
                    }
                    if (distrito != null && !distrito.isEmpty()) {
                        final String distritoFinal = distrito;
                        runOnUiThread(() -> {
                            selDistrito = distritoFinal;
                            proyecto.setDistrito(distritoFinal);
                            if (proyectoOriginal != null) {
                                proyectoOriginal.setDistrito(distritoFinal);
                            }
                            gateway.updateProjectDistrito(proyecto.getId(), distritoFinal,
                                    new AdminFirestoreGateway.FirestoreCallback<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("AdminEditar", "Distrito actualizado: " + distritoFinal);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.e("AdminEditar", "Error al guardar distrito", e);
                                        }
                                    });
                        });
                    }
                }
            } catch (Exception e) {
                Log.e("AdminEditar", "Geocoder error", e);
            }
        }).start();
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

    private void mostrarDialogoEditarDatos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editar_datos_proyecto, null);
        builder.setView(dialogView);

        TextInputEditText etDialogTitulo = dialogView.findViewById(R.id.etDialogTitulo);
        TextInputEditText etDialogUbicacion = dialogView.findViewById(R.id.etDialogUbicacion);
        RecyclerView rvDialogSugerencias = dialogView.findViewById(R.id.rvDialogSugerencias);
        TextView tvDialogDireccionSeleccionada = dialogView.findViewById(R.id.tvDialogDireccionSeleccionada);
        MaterialButton btnCancelar = dialogView.findViewById(R.id.btnDialogCancelar);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnDialogGuardar);

        etDialogTitulo.setText(etTitulo.getText());
        etDialogUbicacion.setText(etUbicacion.getText());

        final AutocompleteSessionToken[] dialogSessionToken = { AutocompleteSessionToken.newInstance() };
        final boolean[] isUpdatingAddress = {false};
            SugerenciasAdapter dialogAdapter = new SugerenciasAdapter(prediction -> {
            rvDialogSugerencias.setVisibility(View.GONE);
            String fullText = prediction.getFullText(null).toString();
            isUpdatingAddress[0] = true;
            etDialogUbicacion.setText(fullText);
            etDialogUbicacion.setSelection(fullText.length());
            isUpdatingAddress[0] = false;

            List<Place.Field> fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS,
                    Place.Field.ADDRESS_COMPONENTS
            );

            FetchPlaceRequest fetchRequest = FetchPlaceRequest.builder(prediction.getPlaceId(), fields)
                    .setSessionToken(dialogSessionToken[0])
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
                        selDistrito = extractDistrict(place);

                        String address = place.getAddress();
                        tvDialogDireccionSeleccionada.setText(address
                                + (selDistrito != null && !selDistrito.isEmpty() ? " (" + selDistrito + ")" : "")
                                + "\n(" + selLat + ", " + selLng + ")");
                        tvDialogDireccionSeleccionada.setVisibility(View.VISIBLE);

                        dialogSessionToken[0] = AutocompleteSessionToken.newInstance();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AdminEditar", "fetchPlace falló", e);
                        Toast.makeText(AdminProyectoEditarActivity.this,
                                "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                    });
        });
        rvDialogSugerencias.setLayoutManager(new LinearLayoutManager(this));
        rvDialogSugerencias.setAdapter(dialogAdapter);

        Handler dialogDebounceHandler = new Handler(Looper.getMainLooper());
        Runnable[] dialogDebounceRunnable = new Runnable[1];

        etDialogUbicacion.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdatingAddress[0]) return;
                if (dialogDebounceRunnable[0] != null) {
                    dialogDebounceHandler.removeCallbacks(dialogDebounceRunnable[0]);
                }
                String query = s.toString().trim();
                if (query.length() < 3) {
                    rvDialogSugerencias.setVisibility(View.GONE);
                    return;
                }
                dialogDebounceRunnable[0] = () -> {
                    RectangularBounds bounds = RectangularBounds.newInstance(LIMA_SW, LIMA_NE);
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setSessionToken(dialogSessionToken[0])
                            .setLocationBias(bounds)
                            .setCountries("PE")
                            .setQuery(query)
                            .build();

                    placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener(response -> {
                                dialogAdapter.setData(response.getAutocompletePredictions());
                                rvDialogSugerencias.setVisibility(
                                        response.getAutocompletePredictions().isEmpty() ? View.GONE : View.VISIBLE);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("AdminEditar", "Dialog autocomplete falló", e);
                                rvDialogSugerencias.setVisibility(View.GONE);
                            });
                };
                dialogDebounceHandler.postDelayed(dialogDebounceRunnable[0], 300);
            }
        });

        AlertDialog dialog = builder.create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nuevoTitulo = etDialogTitulo.getText() != null ? etDialogTitulo.getText().toString().trim() : "";
            String nuevaUbicacion = etDialogUbicacion.getText() != null ? etDialogUbicacion.getText().toString().trim() : "";

            if (nuevoTitulo.isEmpty()) {
                Toast.makeText(AdminProyectoEditarActivity.this,
                        "El nombre del proyecto no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nuevaUbicacion.isEmpty()) {
                Toast.makeText(AdminProyectoEditarActivity.this,
                        "Ingresa la dirección del proyecto", Toast.LENGTH_SHORT).show();
                return;
            }

            etTitulo.setText(nuevoTitulo);
            etUbicacion.setText(nuevaUbicacion.isEmpty() ? "Sin ubicación" : nuevaUbicacion);

            if (proyectoOriginal != null) {
                proyectoOriginal.setNombre(nuevoTitulo);
                proyectoOriginal.setDistrito(selDistrito != null ? selDistrito : "");
                proyectoOriginal.setUbicacion(nuevaUbicacion.isEmpty() ? "Sin ubicación" : nuevaUbicacion);
                proyectoOriginal.setLatitud(selLat);
                proyectoOriginal.setLongitud(selLng);
            }

            dialog.dismiss();
            Toast.makeText(AdminProyectoEditarActivity.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}
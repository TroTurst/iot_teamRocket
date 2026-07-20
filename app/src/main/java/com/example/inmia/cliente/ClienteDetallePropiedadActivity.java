package com.example.inmia.cliente;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.models.Log;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.example.inmia.util.LogHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClienteDetallePropiedadActivity extends AppCompatActivity {


    private ImageView imgHeroProyecto;
    private TextView tvNombreProyecto, tvUbicacionProyecto, tvDescripcionProyecto;
    private TextView tvPrecioProyecto, tvPriceMain, tvPriceSub;
    private LinearLayout btnVerResenas;


    private TextView tvInmobiliariaProyecto, tvReferenciaProyecto;
    private TextView tvAntiguedadProyecto, tvFechaLanzamientoProyecto, tvEstadoGeneralProyecto;
    private ChipGroup chipGroupProyectoFeatures, chipGroupProyectoExtras;

    private TextView tvAreaProyecto, tvDormitoriosProyecto, tvBanosProyecto;
    private TextView tvEstacionamientoProyecto, tvPrecioEstimadoProyecto, tvEstadoProyecto;
    private TextView tvTipologiaNombre, tvTipologiaCertificado;
    private TextView tvTipologiaTipoPiso, tvTipologiaVentilacion, tvTipologiaAcabados;
    private ChipGroup chipGroupTipologiaFeatures;

    private RecyclerView rvTipologias;

    private View btnBack, btnCompartirQR, btnReservar;
    private MaterialButton btnAgendarVisita;

    private MapView mapaClienteProyecto;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Proyecto currentProyecto;
    private Tipologia currentTipologia;
    private String proyectoId = "";
    private String proyectoNombre = "";
    private String tipologiaSeleccionadaActual = "";
    private double montoSeparacionActual = 0.0;
    private String imagenProyectoUrl = "";
    private List<String> currentImageUrls = new ArrayList<>();


    private RecyclerView recyclerViewMiniaturasProyecto;
    private TextView tvVerMasImagenes;
    private com.example.inmia.admin.AdminProyectoMiniaturasAdapter miniaturasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_detalle_propiedad_cliente);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (getIntent() != null) {
            if (getIntent().hasExtra("PROYECTO_ID"))
                proyectoId = getIntent().getStringExtra("PROYECTO_ID");
            if (getIntent().hasExtra("PROYECTO_NOMBRE"))
                proyectoNombre = getIntent().getStringExtra("PROYECTO_NOMBRE");
        }

        inicializarVistas();
        configurarMapa();
        configurarListeners();
        cargarProyectoCompleto();
    }


    private void inicializarVistas() {
        imgHeroProyecto        = findViewById(R.id.imgHeroProyecto);
        tvNombreProyecto       = findViewById(R.id.tvNombreProyecto);
        tvUbicacionProyecto    = findViewById(R.id.tvUbicacionProyecto);
        tvDescripcionProyecto  = findViewById(R.id.tvDescripcionProyecto);
        tvPrecioProyecto       = findViewById(R.id.tvPrecioProyecto);
        tvPriceMain            = findViewById(R.id.tvPriceMain);
        tvPriceSub             = findViewById(R.id.tvPriceSub);
        btnVerResenas          = findViewById(R.id.btnVerResenas);

        tvInmobiliariaProyecto    = findViewById(R.id.tvInmobiliariaProyecto);
        tvReferenciaProyecto      = findViewById(R.id.tvReferenciaProyecto);
        tvAntiguedadProyecto      = findViewById(R.id.tvAntiguedadProyecto);
        tvFechaLanzamientoProyecto = findViewById(R.id.tvFechaLanzamientoProyecto);
        tvEstadoGeneralProyecto   = findViewById(R.id.tvEstadoGeneralProyecto);
        chipGroupProyectoFeatures = findViewById(R.id.chipGroupProyectoFeatures);
        chipGroupProyectoExtras   = findViewById(R.id.chipGroupProyectoExtras);

        tvAreaProyecto          = findViewById(R.id.tvAreaProyecto);
        tvDormitoriosProyecto   = findViewById(R.id.tvDormitoriosProyecto);
        tvBanosProyecto         = findViewById(R.id.tvBanosProyecto);
        tvEstacionamientoProyecto = findViewById(R.id.tvEstacionamientoProyecto);
        tvPrecioEstimadoProyecto  = findViewById(R.id.tvPrecioEstimadoProyecto);
        tvEstadoProyecto          = findViewById(R.id.tvEstadoProyecto);

        tvTipologiaNombre      = findViewById(R.id.tvTipologiaNombre);
        tvTipologiaCertificado = findViewById(R.id.tvTipologiaCertificado);
        tvTipologiaTipoPiso    = findViewById(R.id.tvTipologiaTipoPiso);
        tvTipologiaVentilacion = findViewById(R.id.tvTipologiaVentilacion);
        tvTipologiaAcabados    = findViewById(R.id.tvTipologiaAcabados);
        chipGroupTipologiaFeatures = findViewById(R.id.chipGroupTipologiaFeatures);

        rvTipologias = findViewById(R.id.recyclerViewTipologias);
        rvTipologias.setLayoutManager(new LinearLayoutManager(this));

        btnBack          = findViewById(R.id.btnBack);
        btnCompartirQR   = findViewById(R.id.btnCompartirQR);
        btnReservar      = findViewById(R.id.btnReservar);
        btnAgendarVisita = findViewById(R.id.btnAgendarVisita);

        recyclerViewMiniaturasProyecto = findViewById(R.id.recyclerViewMiniaturasProyecto);
        tvVerMasImagenes = findViewById(R.id.tvVerMasImagenes);

        if (recyclerViewMiniaturasProyecto != null) {
            recyclerViewMiniaturasProyecto.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            miniaturasAdapter = new com.example.inmia.admin.AdminProyectoMiniaturasAdapter(position -> {
                abrirImagenCompleta(currentImageUrls, position);
            });
            recyclerViewMiniaturasProyecto.setAdapter(miniaturasAdapter);
        }

        imgHeroProyecto.setOnClickListener(v -> {
            if (!currentImageUrls.isEmpty()) {
                abrirImagenCompleta(currentImageUrls, 0);
            }
        });

    }


    private void cargarProyectoCompleto() {
        if (proyectoId == null || proyectoId.isEmpty()) return;

        db.collection("proyectos").document(proyectoId)
                .addSnapshotListener((document, error) -> {
                    if (error != null || document == null || !document.exists()) {
                        Toast.makeText(this, "Error cargando proyecto", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Proyecto p = new Proyecto();
                    p.setId(document.getId());
                    p.setNombre(doc(document, "nombre"));
                    p.setDescripcion(doc(document, "descripcion"));
                    p.setEstadoProyecto(doc(document, "estado"));
                    p.setInmobiliaria(doc(document, "inmobiliariaNombre"));
                    p.setReferencia(doc(document, "referencia"));
                    p.setAntiguedad(doc(document, "antiguedad"));
                    p.setFechaLanzamiento(doc(document, "fechaLanzamiento"));

                    Map<String, Object> ubicacionMap =
                            (Map<String, Object>) document.get("ubicacion");
                    if (ubicacionMap != null) {
                        if (ubicacionMap.containsKey("direccion"))
                            p.setUbicacion(String.valueOf(ubicacionMap.get("direccion")));
                        if (ubicacionMap.containsKey("latitud"))
                            p.setLatitud(((Number) ubicacionMap.get("latitud")).doubleValue());
                        if (ubicacionMap.containsKey("longitud"))
                            p.setLongitud(((Number) ubicacionMap.get("longitud")).doubleValue());
                    }

                    List<String> imgs = (List<String>) document.get("imagenesUrls");
                    p.setImagenesUrls(imgs != null ? imgs : new ArrayList<>());

                    List<String> extras = (List<String>) document.get("areasComunes");
                    p.setExtras(extras);

                    Boolean petFriendly = document.getBoolean("petFriendly");
                    Boolean ascensor    = document.getBoolean("conAscensor");
                    p.setPetFriendly(petFriendly != null && petFriendly);
                    p.setConAscensor(ascensor != null && ascensor);

                    List<Map<String, Object>> tipologiasDB =
                            (List<Map<String, Object>>) document.get("tipologias");
                    List<Tipologia> listaTipologias = new ArrayList<>();

                    if (tipologiasDB != null) {
                        for (int i = 0; i < tipologiasDB.size(); i++) {
                            Map<String, Object> m = tipologiasDB.get(i);
                            Tipologia t = new Tipologia();

                            t.setNombre(m.containsKey("nombre")
                                    ? String.valueOf(m.get("nombre"))
                                    : "Departamento Tipo " + (i + 1));

                            t.setArea(m.containsKey("metraje")
                                    ? m.get("metraje") + " m²" : "N/A");

                            t.setDormitorios(m.containsKey("numeroCuartos")
                                    ? String.valueOf(m.get("numeroCuartos")) : "0");

                            t.setBanos(m.containsKey("numeroBanos")
                                    ? String.valueOf(m.get("numeroBanos")) : "0");

                            t.setEstacionamiento(m.containsKey("estacionamiento")
                                    ? String.valueOf(m.get("estacionamiento")) : "No incluye");

                            t.setPrecio(m.containsKey("precio")
                                    ? String.valueOf(m.get("precio")) : "0");

                            t.setEstado(m.containsKey("estado")
                                    ? String.valueOf(m.get("estado")) : "Disponible");

                            t.setCertificadoEnergetico(m.containsKey("certificadoEnergetico")
                                    ? String.valueOf(m.get("certificadoEnergetico")) : "-");

                            t.setTipoPiso(m.containsKey("tipoPiso")
                                    ? String.valueOf(m.get("tipoPiso")) : "-");

                            t.setVentilacion(m.containsKey("ventilacion")
                                    ? String.valueOf(m.get("ventilacion")) : "-");

                            t.setTipoAcabados(m.containsKey("tipoAcabados")
                                    ? String.valueOf(m.get("tipoAcabados")) : "-");

                            if (m.containsKey("balcon"))
                                t.setBalcon(Boolean.TRUE.equals(m.get("balcon")));
                            if (m.containsKey("cocinaIntegrada"))
                                t.setCocinaIntegrada(Boolean.TRUE.equals(m.get("cocinaIntegrada")));
                            if (m.containsKey("terraza"))
                                t.setTerraza(Boolean.TRUE.equals(m.get("terraza")));
                            if (m.containsKey("patio"))
                                t.setPatio(Boolean.TRUE.equals(m.get("patio")));
                            if (m.containsKey("aireAcondicionado"))
                                t.setAireAcondicionado(Boolean.TRUE.equals(m.get("aireAcondicionado")));
                            if (m.containsKey("amueblado"))
                                t.setAmueblado(Boolean.TRUE.equals(m.get("amueblado")));
                            if (m.containsKey("persianasAutomaticas"))
                                t.setPersianasAutomaticas(
                                        Boolean.TRUE.equals(m.get("persianasAutomaticas")));

                            listaTipologias.add(t);
                        }
                    }
                    p.setTipologias(listaTipologias);
                    currentProyecto = p;

                    bindProyecto(p);
                    configurarRecyclerTipologias(p);
                    actualizarMapa(p);
                    cargarRating(p.getId());
                });
    }


    private void bindProyecto(Proyecto p) {
        setText(tvNombreProyecto,        p.getNombre());
        setText(tvUbicacionProyecto,     p.getUbicacion());
        setText(tvDescripcionProyecto,   p.getDescripcion());
        setText(tvInmobiliariaProyecto,  p.getInmobiliaria());
        setText(tvReferenciaProyecto,    p.getReferencia());
        setText(tvAntiguedadProyecto,    p.getAntiguedad());
        setText(tvFechaLanzamientoProyecto, p.getFechaLanzamiento());
        setText(tvEstadoGeneralProyecto, p.getEstadoProyecto());

        List<String> urls = p.getImagenesUrls();
        if (urls != null && !urls.isEmpty() && !urls.get(0).isEmpty()) {
            currentImageUrls  = new ArrayList<>(urls);
            imagenProyectoUrl = urls.get(0);
            if (!isDestroyed())
                Glide.with(this).load(urls.get(0))
                        .placeholder(R.drawable.onboarding1).centerCrop()
                        .into(imgHeroProyecto);

            renderizarMiniaturasUrls(urls);
        } else {
            renderizarMiniaturasUrls(new ArrayList<>());
        }

        chipGroupProyectoFeatures.removeAllViews();
        addChip(chipGroupProyectoFeatures,
                p.isPetFriendly() ? "Pet friendly" : "No pet friendly");
        addChip(chipGroupProyectoFeatures,
                p.isConAscensor() ? "Con ascensor" : "Sin ascensor");

        chipGroupProyectoExtras.removeAllViews();
        if (p.getExtras() != null && !p.getExtras().isEmpty()) {
            for (String extra : p.getExtras()) {
                if (extra != null && !extra.trim().isEmpty()) {
                    String label = extra.substring(0, 1).toUpperCase() + extra.substring(1);
                    addChip(chipGroupProyectoExtras, label);
                }
            }
        } else {
            addChip(chipGroupProyectoExtras, "—");
        }
    }


    private void configurarRecyclerTipologias(Proyecto p) {
        if (p.getTipologias() == null || p.getTipologias().isEmpty()) return;

        Tipologia inicial = p.getTipologiaPrincipal() != null
                ? p.getTipologiaPrincipal()
                : p.getTipologias().get(0);
        currentTipologia = inicial;

        SimpleTipologiaAdapter adapter = new SimpleTipologiaAdapter(
                p.getTipologias(), tipologia -> {
            currentTipologia = tipologia;
            mostrarTipologia(tipologia);
        });
        rvTipologias.setAdapter(adapter);
        mostrarTipologia(inicial);
    }

    private void mostrarTipologia(Tipologia t) {
        tipologiaSeleccionadaActual = t.getNombre();

        setText(tvAreaProyecto,           t.getArea());
        setText(tvDormitoriosProyecto,    t.getDormitorios());
        setText(tvBanosProyecto,          t.getBanos());
        setText(tvEstacionamientoProyecto, t.getEstacionamiento());
        setText(tvPrecioEstimadoProyecto,  t.getPrecio());
        setText(tvEstadoProyecto,          t.getEstado());
        setText(tvTipologiaNombre,         t.getNombre());
        setText(tvTipologiaCertificado,    t.getCertificadoEnergetico());
        setText(tvTipologiaTipoPiso,       t.getTipoPiso());
        setText(tvTipologiaVentilacion,    t.getVentilacion());
        setText(tvTipologiaAcabados,       t.getTipoAcabados());

        String estado = t.getEstado() != null ? t.getEstado().toLowerCase() : "";
        if (estado.contains("dispon"))
            tvEstadoProyecto.setTextColor(
                    ContextCompat.getColor(this, android.R.color.holo_green_dark));
        else if (estado.contains("agot"))
            tvEstadoProyecto.setTextColor(
                    ContextCompat.getColor(this, android.R.color.holo_red_dark));
        else
            tvEstadoProyecto.setTextColor(
                    ContextCompat.getColor(this, R.color.inmia_teal_dark));

        chipGroupTipologiaFeatures.removeAllViews();
        if (t.isBalcon())             addChip(chipGroupTipologiaFeatures, "Balcón");
        if (t.isCocinaIntegrada())    addChip(chipGroupTipologiaFeatures, "Cocina integrada");
        if (t.isTerraza())            addChip(chipGroupTipologiaFeatures, "Terraza");
        if (t.isPatio())              addChip(chipGroupTipologiaFeatures, "Patio");
        if (t.isAireAcondicionado())  addChip(chipGroupTipologiaFeatures, "Aire acondicionado");
        if (t.isAmueblado())          addChip(chipGroupTipologiaFeatures, "Amueblado");
        if (t.isPersianasAutomaticas()) addChip(chipGroupTipologiaFeatures, "Persianas automáticas");

        double precioNum = 0.0;
        try {
            precioNum = Double.parseDouble(
                    t.getPrecio() != null ? t.getPrecio().replaceAll("[^\\d.]", "") : "0");
        } catch (Exception ignored) {}

        montoSeparacionActual = precioNum / 4;

        if (tvPriceMain != null)
            tvPriceMain.setText(String.format(Locale.US, "Desde S/ %,.0f", precioNum));
        if (tvPriceSub != null)
            tvPriceSub.setText(String.format(Locale.US, "Separación: S/ %,.0f",
                    montoSeparacionActual));
        if (tvPrecioProyecto != null)
            tvPrecioProyecto.setText(String.format(Locale.US, "Desde S/ %,.0f", precioNum));
    }


    private void cargarRating(String id) {
        db.collection("proyectos").document(id).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists() || btnVerResenas == null) return;

                    double promedio = doc.contains("promedioRating")
                            ? doc.getDouble("promedioRating") : 0.0;
                    long total = doc.contains("totalValoraciones")
                            ? doc.getLong("totalValoraciones") : 0L;

                    if (btnVerResenas.getChildCount() >= 2) {
                        View child = btnVerResenas.getChildAt(1);
                        if (child instanceof TextView) {
                            ((TextView) child).setText(total > 0
                                    ? String.format(Locale.US, "%.1f", promedio)
                                    : "Nuevo");
                        }
                    }
                });
    }


    private void configurarMapa() {
        Configuration.getInstance().load(this,
                android.preference.PreferenceManager.getDefaultSharedPreferences(this));
        mapaClienteProyecto = findViewById(R.id.mapaClienteProyecto);
        if (mapaClienteProyecto != null) {
            mapaClienteProyecto.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.OpenTopo);
            mapaClienteProyecto.setMultiTouchControls(true);
            mapaClienteProyecto.getController().setZoom(16.0);
            mapaClienteProyecto.getController().setCenter(
                    new GeoPoint(-12.046374, -77.042793));
        }
    }

    private void actualizarMapa(Proyecto p) {
        if (mapaClienteProyecto == null) return;
        if (p.getLatitud() == 0 && p.getLongitud() == 0) return;

        GeoPoint punto = new GeoPoint(p.getLatitud(), p.getLongitud());
        mapaClienteProyecto.getController().setZoom(16.0);
        mapaClienteProyecto.getController().setCenter(punto);

        mapaClienteProyecto.getOverlays().clear();
        Marker marker = new Marker(mapaClienteProyecto);
        marker.setPosition(punto);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(p.getNombre());
        marker.setSnippet(p.getUbicacion());
        mapaClienteProyecto.getOverlays().add(marker);
        mapaClienteProyecto.invalidate();
    }


    private void configurarListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnCompartirQR.setOnClickListener(this::showPopupMenu);

        if (btnVerResenas != null) {
            btnVerResenas.setOnClickListener(v -> {
                ReseniasProyectoBottomSheet sheet =
                        ReseniasProyectoBottomSheet.newInstance(proyectoId, proyectoNombre);
                sheet.show(getSupportFragmentManager(), "resenias");
            });
        }

        if (btnAgendarVisita != null) {
            btnAgendarVisita.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteRegistrarCitaActivity.class);
                intent.putExtra("PROYECTO_ID", proyectoId);
                intent.putExtra("PROYECTO_NOMBRE", proyectoNombre);
                intent.putExtra("TIPOLOGIA_NOMBRE", tipologiaSeleccionadaActual);
                startActivity(intent);
            });
        }

        if (btnReservar != null) {
            btnReservar.setOnClickListener(v -> {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (montoSeparacionActual <= 0) {
                    Toast.makeText(this, "Selecciona una tipología primero",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                crearSeparacion(currentUser.getUid());
            });
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        if (bottomNav != null) {
            bottomNav.getMenu().setGroupCheckable(0, false, true);
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_inicio) {
                    startActivity(new Intent(this, ClienteHomeActivity.class));
                    finish(); return true;
                } else if (id == R.id.nav_citas) {
                    startActivity(new Intent(this, ClienteCitasActivity.class));
                    finish(); return true;
                } else if (id == R.id.nav_chat) {
                    startActivity(new Intent(this, ClienteMensajesActivity.class));
                    finish(); return true;
                } else if (id == R.id.nav_separaciones) {
                    startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                    finish(); return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(this, ClientePerfilClienteActivity.class));
                    finish(); return true;
                }
                return false;
            });
        }
    }


    private void crearSeparacion(String uid) {
        Toast.makeText(this, "Solicitando separación...", Toast.LENGTH_SHORT).show();

        Map<String, Object> sep = new HashMap<>();
        sep.put("clienteId",        uid);
        sep.put("proyectoId",       proyectoId);
        sep.put("nombreProyecto",   currentProyecto != null
                ? currentProyecto.getNombre() : proyectoNombre);
        sep.put("ubicacion",        currentProyecto != null
                ? currentProyecto.getUbicacion() : "");
        sep.put("inmobiliariaNombre", currentProyecto != null
                ? currentProyecto.getInmobiliaria() : "");
        sep.put("tipologia",        tipologiaSeleccionadaActual);
        sep.put("montoSeparacion",  montoSeparacionActual);
        sep.put("imagenUrl",        imagenProyectoUrl);
        sep.put("estado",           "En proceso");
        sep.put("fechaCreacion",
                com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("separaciones").add(sep)
                .addOnSuccessListener(ref -> {
                    LogHelper.registrar(
                            "Se solicitó la separación de una unidad en "
                                    + (currentProyecto != null
                                    ? currentProyecto.getNombre()
                                    : proyectoNombre),
                            Log.TIPO_SEPARACION, LogHelper.ROL_CLIENTE);
                    Toast.makeText(this, "¡Solicitud enviada a validación!",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al solicitar", Toast.LENGTH_SHORT).show());
    }

    private void setText(TextView tv, String value) {
        if (tv == null) return;
        tv.setText(value != null && !value.trim().isEmpty() ? value : "-");
    }

    private String doc(com.google.firebase.firestore.DocumentSnapshot d, String key) {
        String val = d.getString(key);
        return val != null ? val : "";
    }

    private void addChip(ChipGroup group, String text) {
        if (group == null) return;
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setChipBackgroundColorResource(R.color.inmia_teal_light);
        chip.setTextColor(ContextCompat.getColor(this, R.color.inmia_teal_dark));
        group.addView(chip);
    }


    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenu().add("Compartir Proyecto");
        popup.getMenu().add("Descargar Brochure");

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle() != null ? item.getTitle().toString() : "";
            if (title.equals("Compartir Proyecto")) {
                mostrarDialogoQR(proyectoNombre);
                return true;
            } else if (title.equals("Descargar Brochure")) {
                String url = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750";
                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
                req.setTitle("Brochure " + proyectoNombre);
                req.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                req.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        proyectoNombre.replace(" ", "_") + ".jpg");
                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                if (dm != null) dm.enqueue(req);
                Toast.makeText(this, "Descarga iniciada", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void mostrarDialogoQR(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            Toast.makeText(this, "Cargando datos...", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            com.journeyapps.barcodescanner.BarcodeEncoder encoder =
                    new com.journeyapps.barcodescanner.BarcodeEncoder();
            android.graphics.Bitmap bmp = encoder.encodeBitmap(
                    nombre, com.google.zxing.BarcodeFormat.QR_CODE, 600, 600);

            ImageView imgQR = new ImageView(this);
            imgQR.setImageBitmap(bmp);
            imgQR.setPadding(50, 50, 50, 50);

            new android.app.AlertDialog.Builder(this)
                    .setTitle("Compartir Proyecto")
                    .setMessage("Muestra este código para ir directo a " + nombre)
                    .setView(imgQR)
                    .setNeutralButton("Descargar QR",
                            (d, w) -> descargarQR(bmp, nombre))
                    .setPositiveButton("Cerrar", null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al generar QR", Toast.LENGTH_SHORT).show();
        }
    }
    private void abrirImagenCompleta(List<String> urls, int selectedIndex) {
        if (urls == null || urls.isEmpty()) return;
        int index = Math.max(0, Math.min(selectedIndex, urls.size() - 1));

        Intent intent = new Intent(this, com.example.inmia.admin.AdminProyectoImagenActivity.class);
        intent.putStringArrayListExtra(com.example.inmia.admin.AdminProyectoImagenActivity.EXTRA_IMAGE_URLS, new ArrayList<>(urls));
        intent.putExtra(com.example.inmia.admin.AdminProyectoImagenActivity.EXTRA_SELECTED_INDEX, index);
        intent.putExtra(com.example.inmia.admin.AdminProyectoImagenActivity.EXTRA_TITLE,
                currentProyecto != null ? currentProyecto.getNombre() : proyectoNombre);

        startActivity(intent);
    }
    private void renderizarMiniaturasUrls(List<String> urls) {
        if (urls == null) urls = new ArrayList<>();

        int previewCount = Math.min(4, urls.size());
        List<String> preview = urls.subList(0, previewCount);

        if (miniaturasAdapter != null) {
            miniaturasAdapter.submitUrls(preview);
        }

        int restantes = urls.size() - previewCount;
        if (tvVerMasImagenes != null) {
            if (restantes > 0) {
                tvVerMasImagenes.setText("+" + restantes + " fotos");
                tvVerMasImagenes.setVisibility(View.VISIBLE);
                List<String> urlsFinal = urls;
                tvVerMasImagenes.setOnClickListener(v -> abrirImagenCompleta(urlsFinal, 0));
            } else {
                tvVerMasImagenes.setVisibility(View.GONE);
                tvVerMasImagenes.setOnClickListener(null);
            }
        }
    }

    private void descargarQR(android.graphics.Bitmap bmp, String nombre) {
        try {
            String fileName = "QR_" + nombre.replace(" ", "_")
                    + "_" + System.currentTimeMillis() + ".jpg";
            android.content.ContentValues cv = new android.content.ContentValues();
            cv.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, fileName);
            cv.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                cv.put(android.provider.MediaStore.Images.Media.RELATIVE_PATH,
                        android.os.Environment.DIRECTORY_PICTURES + "/INMIA");
                cv.put(android.provider.MediaStore.Images.Media.IS_PENDING, 1);
            }
            android.net.Uri uri = getContentResolver().insert(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            if (uri != null) {
                java.io.OutputStream os = getContentResolver().openOutputStream(uri);
                if (os != null) {
                    bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush(); os.close();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        cv.clear();
                        cv.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0);
                        getContentResolver().update(uri, cv, null, null);
                    }
                    Toast.makeText(this, "QR guardado en Pictures/INMIA",
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override protected void onResume() {
        super.onResume();
        if (mapaClienteProyecto != null) mapaClienteProyecto.onResume();
    }
    @Override protected void onPause() {
        super.onPause();
        if (mapaClienteProyecto != null) mapaClienteProyecto.onPause();
    }
    @Override protected void onDestroy() {
        super.onDestroy();
        if (mapaClienteProyecto != null) mapaClienteProyecto.onDetach();
    }
}
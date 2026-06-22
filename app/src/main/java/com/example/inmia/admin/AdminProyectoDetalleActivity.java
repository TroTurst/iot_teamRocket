package com.example.inmia.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminFirestoreGateway.AdminContext;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminProyectoDetalleActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGES = "extra_images";
    private static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_PROYECTO_ID = "proyecto_id";

    private BottomNavigationView bottomNav;
    private int totalNotificaciones;

    private AdminFirestoreGateway gateway;
    private String companyId;

    private ImageView imgHeroProyecto;
    private android.widget.TextView tvNombreProyecto;
    private android.widget.TextView tvUbicacionProyecto;
    private android.widget.TextView tvDescripcionProyecto;
    private android.widget.TextView tvPrecioProyecto;
    private android.widget.TextView tvVerMasImagenes;
    private RecyclerView recyclerViewMiniaturasProyecto;
    private AdminProyectoMiniaturasAdapter miniaturasAdapter;

    private RecyclerView recyclerViewTipologias;
    private TipologiaAdapter tipologiaAdapter;

    private android.widget.TextView tvAreaProyecto;
    private android.widget.TextView tvDormitoriosProyecto;
    private android.widget.TextView tvBanosProyecto;
    private android.widget.TextView tvEstacionamientoProyecto;
    private android.widget.TextView tvPrecioEstimadoProyecto;
    private android.widget.TextView tvEstadoProyecto;

    // Proyecto (campos completos)
    private android.widget.TextView tvInmobiliariaProyecto;
    private android.widget.TextView tvReferenciaProyecto;
    private android.widget.TextView tvAntiguedadProyecto;
    private android.widget.TextView tvFechaLanzamientoProyecto;
    private android.widget.TextView tvEstadoGeneralProyecto;
    private ChipGroup chipGroupProyectoFeatures;
    private ChipGroup chipGroupProyectoExtras;

    // Tipologia (campos completos)
    private android.widget.TextView tvTipologiaNombre;
    private android.widget.TextView tvTipologiaCertificado;
    private android.widget.TextView tvTipologiaTipoPiso;
    private android.widget.TextView tvTipologiaVentilacion;
    private android.widget.TextView tvTipologiaAcabados;
    private ChipGroup chipGroupTipologiaFeatures;

    private Proyecto currentProyecto;
    private Tipologia currentTipologia;

    private List<String> currentImageUrls = new ArrayList<>();

    private MapView mapaAdminProyecto;
    private View cardMapaProyecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_proyecto_detalle);

        gateway = new AdminFirestoreGateway();

        Context ctx = getApplicationContext();
        SharedPreferences prefs = ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE);
        Configuration.getInstance().load(ctx, prefs);
        Configuration.getInstance().setUserAgentValue(getPackageName());

        bottomNav = findViewById(R.id.bottomNavAdmin);
        View btnBack = findViewById(R.id.btnBackProyectoDetalle);
        View btnEditar = findViewById(R.id.btnEditarProyectoDetalle);

        imgHeroProyecto = findViewById(R.id.imgHeroProyecto);
        tvNombreProyecto = findViewById(R.id.tvNombreProyecto);
        tvUbicacionProyecto = findViewById(R.id.tvUbicacionProyecto);
        tvDescripcionProyecto = findViewById(R.id.tvDescripcionProyecto);
        tvPrecioProyecto = findViewById(R.id.tvPrecioProyecto);
        tvVerMasImagenes = findViewById(R.id.tvVerMasImagenes);
        recyclerViewMiniaturasProyecto = findViewById(R.id.recyclerViewMiniaturasProyecto);

        recyclerViewTipologias = findViewById(R.id.recyclerViewTipologias);

        tvAreaProyecto = findViewById(R.id.tvAreaProyecto);
        tvDormitoriosProyecto = findViewById(R.id.tvDormitoriosProyecto);
        tvBanosProyecto = findViewById(R.id.tvBanosProyecto);
        tvEstacionamientoProyecto = findViewById(R.id.tvEstacionamientoProyecto);
        tvPrecioEstimadoProyecto = findViewById(R.id.tvPrecioEstimadoProyecto);
        tvEstadoProyecto = findViewById(R.id.tvEstadoProyecto);

        // Ficha proyecto
        tvInmobiliariaProyecto = findViewById(R.id.tvInmobiliariaProyecto);
        tvReferenciaProyecto = findViewById(R.id.tvReferenciaProyecto);
        tvAntiguedadProyecto = findViewById(R.id.tvAntiguedadProyecto);
        tvFechaLanzamientoProyecto = findViewById(R.id.tvFechaLanzamientoProyecto);
        tvEstadoGeneralProyecto = findViewById(R.id.tvEstadoGeneralProyecto);
        chipGroupProyectoFeatures = findViewById(R.id.chipGroupProyectoFeatures);
        chipGroupProyectoExtras = findViewById(R.id.chipGroupProyectoExtras);

        // Ficha tipologia
        tvTipologiaNombre = findViewById(R.id.tvTipologiaNombre);
        tvTipologiaCertificado = findViewById(R.id.tvTipologiaCertificado);
        tvTipologiaTipoPiso = findViewById(R.id.tvTipologiaTipoPiso);
        tvTipologiaVentilacion = findViewById(R.id.tvTipologiaVentilacion);
        tvTipologiaAcabados = findViewById(R.id.tvTipologiaAcabados);
        chipGroupTipologiaFeatures = findViewById(R.id.chipGroupTipologiaFeatures);

        mapaAdminProyecto = findViewById(R.id.mapaAdminProyecto);
        cardMapaProyecto = findViewById(R.id.cardMapaProyecto);

        configurarSeleccion();
        configurarRecyclerMiniaturas();

        String proyectoId = getIntent() != null ? getIntent().getStringExtra(EXTRA_PROYECTO_ID) : null;
        if (proyectoId == null || proyectoId.trim().isEmpty()) {
            Toast.makeText(this, "No se encontró el proyecto seleccionado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        gateway.resolveAdminContextByEmail(AdminSessionDefaults.DEFAULT_ADMIN_EMAIL, new AdminFirestoreGateway.FirestoreCallback<AdminContext>() {
            @Override
            public void onSuccess(AdminContext context) {
                companyId = context.getCompanyId();
                totalNotificaciones = 0;

                gateway.observeUnreadNotifications(context.getUserId(), new AdminFirestoreGateway.FirestoreCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer count) {
                        totalNotificaciones = count != null ? count : 0;
                        configurarSeleccion();
                    }

                    @Override
                    public void onError(Exception e) {
                        totalNotificaciones = 0;
                        configurarSeleccion();
                    }
                });

                gateway.observeProjectById(proyectoId, new AdminFirestoreGateway.FirestoreCallback<Proyecto>() {
                    @Override
                    public void onSuccess(Proyecto proyecto) {
                        Log.d("AdminDetalle", "Proyecto cargado inicialmente: " + proyecto.getNombre() + " (id: " + proyecto.getId() + ")");
                        Log.d("AdminDetalle", "Tipologias count: " + (proyecto.getTipologias() != null ? proyecto.getTipologias().size() : 0));
                        currentProyecto = proyecto;
                        bindProyecto(currentProyecto);
                        configurarRecyclerTipologias(currentProyecto);
                        resolveProjectDistritoFromLatLng(currentProyecto);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AdminProyectoDetalleActivity.this,
                                "No se encontró el proyecto seleccionado",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminProyectoDetalleActivity.this,
                        "No se pudo cargar el proyecto",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnBack.setOnClickListener(v -> finish());
        btnEditar.setOnClickListener(v -> abrirEdicionProyecto());

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

    private void configurarSeleccion() {
        bottomNav.setSelectedItemId(R.id.nav_proyectos);
        if (totalNotificaciones > 0) {
            View badge = findViewById(R.id.tvBadgeNotif);
            if (badge instanceof android.widget.TextView) {
                ((android.widget.TextView) badge).setText(String.valueOf(totalNotificaciones));
                badge.setVisibility(View.VISIBLE);
            }
        }
    }

    private void configurarRecyclerTipologias(Proyecto proyecto) {
        recyclerViewTipologias.setLayoutManager(new LinearLayoutManager(this));

        if (proyecto.getTipologias() == null || proyecto.getTipologias().isEmpty()) {
            Toast.makeText(this, "Este proyecto no tiene tipologías registradas", Toast.LENGTH_SHORT).show();
            recyclerViewTipologias.setAdapter(null);
            return;
        }

        Tipologia seleccionInicial = proyecto.getTipologiaPrincipal();
        if (seleccionInicial == null && proyecto.getTipologias() != null && !proyecto.getTipologias().isEmpty()) {
            seleccionInicial = proyecto.getTipologias().get(0);
        }
        currentTipologia = seleccionInicial;

        tipologiaAdapter = new TipologiaAdapter(
                this,
                proyecto.getTipologias(),
                currentTipologia != null ? currentTipologia.getId() : null,
                tipologia -> {
                    currentTipologia = tipologia;
                    if (tipologiaAdapter != null) tipologiaAdapter.setSelectedTipologiaId(tipologia.getId());
                    mostrarTipologia(tipologia);
                }
        );

        recyclerViewTipologias.setAdapter(tipologiaAdapter);

        if (currentTipologia != null) {
            mostrarTipologia(currentTipologia);
        }
    }

    private void configurarRecyclerMiniaturas() {
        recyclerViewMiniaturasProyecto.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        miniaturasAdapter = new AdminProyectoMiniaturasAdapter(position ->
                abrirImagenCompleta(currentImageUrls, position)
        );
        recyclerViewMiniaturasProyecto.setAdapter(miniaturasAdapter);

        imgHeroProyecto.setOnClickListener(v -> {
            if (!currentImageUrls.isEmpty()) {
                abrirImagenCompleta(currentImageUrls, 0);
            }
        });
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
                            proyecto.setDistrito(distritoFinal);
                            gateway.updateProjectDistrito(proyecto.getId(), distritoFinal,
                                    new AdminFirestoreGateway.FirestoreCallback<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("AdminDetalle", "Distrito actualizado: " + distritoFinal);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.e("AdminDetalle", "Error al guardar distrito", e);
                                        }
                                    });
                        });
                    }
                }
            } catch (Exception e) {
                Log.e("AdminDetalle", "Geocoder error", e);
            }
        }).start();
    }

    private void bindProyecto(Proyecto p) {
        tvNombreProyecto.setText(p.getNombre());
        tvUbicacionProyecto.setText(p.getUbicacion());
        tvDescripcionProyecto.setText(p.getDescripcion());

        List<String> urls = p.getImagenesUrls();
        if (urls != null && !urls.isEmpty()) {
            currentImageUrls = new ArrayList<>(urls);
            Glide.with(this).load(urls.get(0)).placeholder(R.drawable.ic_add).centerCrop().into(imgHeroProyecto);
            renderizarMiniaturasUrls(urls);
        } else if (p.getImagenHeroPrincipal() != 0) {
            imgHeroProyecto.setImageResource(p.getImagenHeroPrincipal());
            currentImageUrls = new ArrayList<>();
        }

        configurarMapa(p);

        // Ficha proyecto
        setTextOrDash(tvInmobiliariaProyecto, p.getInmobiliaria());
        setTextOrDash(tvReferenciaProyecto, p.getReferencia());
        setTextOrDash(tvAntiguedadProyecto, p.getAntiguedad());
        setTextOrDash(tvFechaLanzamientoProyecto, p.getFechaLanzamiento());
        setTextOrDash(tvEstadoGeneralProyecto, p.getEstadoProyecto());

        // Chips features base del proyecto
        chipGroupProyectoFeatures.removeAllViews();
        addChip(chipGroupProyectoFeatures, p.isPetFriendly() ? "Pet friendly" : "No pet friendly");
        addChip(chipGroupProyectoFeatures, p.isConAscensor() ? "Con ascensor" : "Sin ascensor");

        // Chips extras
        chipGroupProyectoExtras.removeAllViews();
        if (p.getExtras() != null && !p.getExtras().isEmpty()) {
            for (String extra : p.getExtras()) {
                if (extra != null && !extra.trim().isEmpty()) addChip(chipGroupProyectoExtras, extra);
            }
        } else {
            addChip(chipGroupProyectoExtras, "—");
        }

        // Ocultar botón editar si el proyecto está entregado
        View btnEditar = findViewById(R.id.btnEditarProyectoDetalle);
        if (btnEditar != null) {
            if ("Entregado".equalsIgnoreCase(p.getEstadoProyecto())) {
                btnEditar.setVisibility(View.GONE);
            } else {
                btnEditar.setVisibility(View.VISIBLE);
            }
        }
    }

    private void configurarMapa(Proyecto p) {
        if (mapaAdminProyecto == null || cardMapaProyecto == null) return;

        if (p.getLatitud() == 0 && p.getLongitud() == 0) {
            cardMapaProyecto.setVisibility(View.GONE);
            return;
        }

        cardMapaProyecto.setVisibility(View.VISIBLE);
        mapaAdminProyecto.onResume();
        mapaAdminProyecto.setMultiTouchControls(true);

        GeoPoint puntoProyecto = new GeoPoint(p.getLatitud(), p.getLongitud());
        mapaAdminProyecto.getController().setZoom(15.0);
        mapaAdminProyecto.getController().setCenter(puntoProyecto);

        Marker marker = new Marker(mapaAdminProyecto);
        marker.setPosition(puntoProyecto);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(p.getNombre());
        marker.setSnippet(p.getUbicacion());
        marker.setOnMarkerClickListener((m, mapView) -> {
            m.showInfoWindow();
            return true;
        });
        mapaAdminProyecto.getOverlays().add(marker);
        mapaAdminProyecto.invalidate();
    }

    private void mostrarTipologia(Tipologia tipologia) {
        if (currentProyecto == null || tipologia == null) return;

        tvNombreProyecto.setText(currentProyecto.getNombre());
        tvUbicacionProyecto.setText(currentProyecto.getUbicacion());
        tvPrecioProyecto.setText(tipologia.getPrecio());

        List<String> tipUrls = tipologia.getImagenesUrls();
        if (tipUrls != null && !tipUrls.isEmpty()) {
            currentImageUrls = new ArrayList<>(tipUrls);
            Glide.with(this).load(tipUrls.get(0)).placeholder(R.drawable.ic_add).centerCrop().into(imgHeroProyecto);
            renderizarMiniaturasUrls(tipUrls);
        } else {
            List<String> projUrls = currentProyecto.getImagenesUrls();
            if (projUrls != null && !projUrls.isEmpty()) {
                currentImageUrls = new ArrayList<>(projUrls);
                Glide.with(this).load(projUrls.get(0)).placeholder(R.drawable.ic_add).centerCrop().into(imgHeroProyecto);
                renderizarMiniaturasUrls(projUrls);
            } else {
                currentImageUrls = new ArrayList<>();
                imgHeroProyecto.setImageResource(R.drawable.ic_add);
                renderizarMiniaturasUrls(new ArrayList<>());
            }
        }

        tvAreaProyecto.setText(tipologia.getArea());
        tvDormitoriosProyecto.setText(tipologia.getDormitorios());
        tvBanosProyecto.setText(tipologia.getBanos());
        tvEstacionamientoProyecto.setText(tipologia.getEstacionamiento());
        tvPrecioEstimadoProyecto.setText(tipologia.getPrecio());
        tvEstadoProyecto.setText(tipologia.getEstado());

        setTextOrDash(tvTipologiaNombre, tipologia.getNombre());
        setTextOrDash(tvTipologiaCertificado, tipologia.getCertificadoEnergetico());
        setTextOrDash(tvTipologiaTipoPiso, tipologia.getTipoPiso());
        setTextOrDash(tvTipologiaVentilacion, tipologia.getVentilacion());
        setTextOrDash(tvTipologiaAcabados, tipologia.getTipoAcabados());

        chipGroupTipologiaFeatures.removeAllViews();
        addChip(chipGroupTipologiaFeatures, tipologia.isPatio() ? "Patio" : "Sin patio");
        addChip(chipGroupTipologiaFeatures, tipologia.isTerraza() ? "Terraza" : "Sin terraza");
        addChip(chipGroupTipologiaFeatures, tipologia.isBalcon() ? "Balcón" : "Sin balcón");
        addChip(chipGroupTipologiaFeatures, tipologia.isAireAcondicionado() ? "A/C" : "Sin A/C");
        addChip(chipGroupTipologiaFeatures, tipologia.isCocinaIntegrada() ? "Cocina integrada" : "Cocina separada");
        addChip(chipGroupTipologiaFeatures, tipologia.isAmueblado() ? "Amueblado" : "Sin muebles");
        addChip(chipGroupTipologiaFeatures, tipologia.isPersianasAutomaticas() ? "Persianas autom." : "Persianas manuales");

        String estado = tipologia.getEstado() != null ? tipologia.getEstado().toLowerCase() : "";
        if (estado.contains("dispon")) {
            tvEstadoProyecto.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else if (estado.contains("agot")) {
            tvEstadoProyecto.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            tvEstadoProyecto.setTextColor(ContextCompat.getColor(this, R.color.inmia_teal_dark));
        }
    }

    private void setTextOrDash(android.widget.TextView tv, String value) {
        if (tv == null) return;
        if (value == null || value.trim().isEmpty()) {
            tv.setText("-");
        } else {
            tv.setText(value);
        }
    }

    private void addChip(ChipGroup group, String text) {
        if (group == null) return;
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setChipBackgroundColorResource(R.color.inmia_white);
        chip.setChipStrokeColorResource(R.color.inmia_teal_dark);
        chip.setChipStrokeWidth(1f);
        chip.setTextColor(ContextCompat.getColor(this, R.color.inmia_teal_dark));
        group.addView(chip);
    }

    private void renderizarMiniaturasUrls(List<String> urls) {
        if (urls == null) urls = new ArrayList<>();

        int previewCount = Math.min(4, urls.size());
        List<String> preview = urls.subList(0, previewCount);
        if (miniaturasAdapter != null) {
            miniaturasAdapter.submitUrls(preview);
        }

        int restantes = urls.size() - previewCount;
        if (restantes > 0) {
            tvVerMasImagenes.setText(getString(R.string.admin_project_gallery_more, restantes));
            tvVerMasImagenes.setVisibility(View.VISIBLE);
            List<String> urlsFinal = urls;
            tvVerMasImagenes.setOnClickListener(v -> abrirImagenCompleta(urlsFinal, 0));
        } else {
            tvVerMasImagenes.setVisibility(View.GONE);
            tvVerMasImagenes.setOnClickListener(null);
        }
    }

    private void abrirEdicionProyecto() {
        if (currentProyecto == null || currentProyecto.getId() == null) {
            Toast.makeText(this, "No hay proyecto seleccionado", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, AdminProyectoEditarActivity.class);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_PROYECTO_ID, currentProyecto.getId());
        startActivity(intent);
    }


    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void abrirImagenCompleta(List<String> urls, int selectedIndex) {
        if (urls == null || urls.isEmpty()) return;
        int index = Math.max(0, Math.min(selectedIndex, urls.size() - 1));

        Intent intent = new Intent(this, AdminProyectoImagenActivity.class);
        intent.putStringArrayListExtra(AdminProyectoImagenActivity.EXTRA_IMAGE_URLS, new ArrayList<>(urls));
        intent.putExtra(AdminProyectoImagenActivity.EXTRA_SELECTED_INDEX, index);
        intent.putExtra(AdminProyectoImagenActivity.EXTRA_TITLE,
                currentProyecto != null ? currentProyecto.getNombre() : "Proyecto");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("AdminDetalle", "onResume called");
        if (mapaAdminProyecto != null) mapaAdminProyecto.onResume();
        if (currentProyecto != null) {
            Log.d("AdminDetalle", "Recargando proyecto con getProjectById: " + currentProyecto.getNombre() + " (id: " + currentProyecto.getId() + ")");
            gateway.getProjectById(currentProyecto.getId(), new AdminFirestoreGateway.FirestoreCallback<Proyecto>() {
                @Override
                public void onSuccess(Proyecto proyecto) {
                    Log.d("AdminDetalle", "Proyecto recargado OK, tipologias: " + (proyecto.getTipologias() != null ? proyecto.getTipologias().size() : 0));
                    currentProyecto = proyecto;
                    runOnUiThread(() -> {
                        bindProyecto(currentProyecto);
                        configurarRecyclerTipologias(currentProyecto);
                        resolveProjectDistritoFromLatLng(currentProyecto);
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.e("AdminDetalle", "Error recargando proyecto: " + e.getMessage());
                }
            });
        } else {
            Log.d("AdminDetalle", "currentProyecto es null, no se recarga");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapaAdminProyecto != null) mapaAdminProyecto.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapaAdminProyecto != null) mapaAdminProyecto.onDetach();
    }
}

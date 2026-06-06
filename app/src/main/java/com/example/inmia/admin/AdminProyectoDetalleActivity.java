package com.example.inmia.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.admin.data.AdminRepository;
import com.example.inmia.admin.data.AdminRepositoryProvider;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

public class AdminProyectoDetalleActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGES = "extra_images";
    private static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_PROYECTO_ID = "proyecto_id";

    private BottomNavigationView bottomNav;
    private int totalNotificaciones;

    private AdminRepository repository;
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

    private List<Integer> imagenesTipologiaActual = new ArrayList<>();
    private int heroImageResActual = 0;

    private MapView mapaAdminProyecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_proyecto_detalle);

        repository = AdminRepositoryProvider.get();
        companyId = repository.getCompanyIdForEmail(AdminSessionDefaults.DEFAULT_ADMIN_EMAIL);
        totalNotificaciones = repository.getUnreadNotifications(companyId);

        Context ctx = getApplicationContext();
        SharedPreferences prefs = ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE);
        Configuration.getInstance().load(ctx, prefs);
        Configuration.getInstance().setUserAgentValue(getPackageName());

        bottomNav = findViewById(R.id.bottomNavAdmin);
        FrameLayout frameNotificaciones = findViewById(R.id.frameNotificaciones);
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
        if (mapaAdminProyecto != null) {
            mapaAdminProyecto.setMultiTouchControls(true);
            GeoPoint puntoInicio = new GeoPoint(-12.046374, -77.042793);
            mapaAdminProyecto.getController().setZoom(15.0);
            mapaAdminProyecto.getController().setCenter(puntoInicio);
        }

        configurarSeleccion();
        configurarRecyclerMiniaturas();

        // Cargar proyecto seleccionado
        String proyectoId = getIntent() != null ? getIntent().getStringExtra(EXTRA_PROYECTO_ID) : null;
        currentProyecto = repository.getProjectById(companyId, proyectoId);
        if (currentProyecto == null) {
            Toast.makeText(this, "No se encontró el proyecto seleccionado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindProyecto(currentProyecto);

        configurarRecyclerTipologias(currentProyecto);

        frameNotificaciones.setOnClickListener(v ->
                Toast.makeText(this, "Tienes " + totalNotificaciones + " notificaciones", Toast.LENGTH_SHORT).show());

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
                abrirImagenCompleta(imagenesTipologiaActual, position)
        );
        recyclerViewMiniaturasProyecto.setAdapter(miniaturasAdapter);

        imgHeroProyecto.setOnClickListener(v ->
                abrirImagenCompleta(imagenesTipologiaActual, getHeroIndex(imagenesTipologiaActual, heroImageResActual))
        );
    }

    private void bindProyecto(Proyecto p) {
        // Textos principales
        tvNombreProyecto.setText(p.getNombre());
        tvUbicacionProyecto.setText(p.getUbicacion());
        if (p.getImagenHeroPrincipal() != 0) {
            imgHeroProyecto.setImageResource(p.getImagenHeroPrincipal());
        }

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

    }

    private void mostrarTipologia(Tipologia tipologia) {
        if (currentProyecto == null || tipologia == null) return;

        tvNombreProyecto.setText(currentProyecto.getNombre());
        tvUbicacionProyecto.setText(currentProyecto.getUbicacion());
        tvDescripcionProyecto.setText(tipologia.getDescripcion());
        tvPrecioProyecto.setText(tipologia.getPrecio());
        if (tipologia.getImagenHero() != 0) {
            heroImageResActual = tipologia.getImagenHero();
            imgHeroProyecto.setImageResource(tipologia.getImagenHero());
        } else if (currentProyecto.getImagenHeroPrincipal() != 0) {
            heroImageResActual = currentProyecto.getImagenHeroPrincipal();
            imgHeroProyecto.setImageResource(currentProyecto.getImagenHeroPrincipal());
        } else {
            heroImageResActual = 0;
        }

        tvAreaProyecto.setText(tipologia.getArea());
        tvDormitoriosProyecto.setText(tipologia.getDormitorios());
        tvBanosProyecto.setText(tipologia.getBanos());
        tvEstacionamientoProyecto.setText(tipologia.getEstacionamiento());
        tvPrecioEstimadoProyecto.setText(tipologia.getPrecio());
        tvEstadoProyecto.setText(tipologia.getEstado());

        // Ficha tipologia (todos los campos)
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

        // Color de estado
        String estado = tipologia.getEstado() != null ? tipologia.getEstado().toLowerCase() : "";
        if (estado.contains("dispon")) {
            tvEstadoProyecto.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else if (estado.contains("agot")) {
            tvEstadoProyecto.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            tvEstadoProyecto.setTextColor(ContextCompat.getColor(this, R.color.inmia_teal_dark));
        }

        imagenesTipologiaActual = convertToList(tipologia.getImagenes());
        renderizarMiniaturas(imagenesTipologiaActual);
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

    private List<Integer> convertToList(int[] imagenes) {
        List<Integer> list = new ArrayList<>();
        if (imagenes == null) return list;
        for (int img : imagenes) list.add(img);
        return list;
    }

    private void renderizarMiniaturas(List<Integer> imagenes) {
        if (imagenes == null) {
            imagenes = new ArrayList<>();
        }

        imagenesTipologiaActual = new ArrayList<>(imagenes);

        int previewCount = Math.min(4, imagenes.size());
        List<Integer> preview = imagenes.subList(0, previewCount);
        if (miniaturasAdapter != null) {
            miniaturasAdapter.submitList(preview);
        }

        int restantes = imagenes.size() - previewCount;
        if (restantes > 0) {
            tvVerMasImagenes.setText(getString(R.string.admin_project_gallery_more, restantes));
            tvVerMasImagenes.setVisibility(View.VISIBLE);
            List<Integer> imagenesFinal = imagenes;
            tvVerMasImagenes.setOnClickListener(v -> abrirGaleriaCompleta(imagenesFinal));
        } else {
            tvVerMasImagenes.setVisibility(View.GONE);
            tvVerMasImagenes.setOnClickListener(null);
        }
    }

    private void abrirGaleriaCompleta(List<Integer> imagenes) {
        int[] imagenesArray = new int[imagenes.size()];
        for (int i = 0; i < imagenes.size(); i++) {
            imagenesArray[i] = imagenes.get(i);
        }

        Intent intent = new Intent(this, AdminProyectoGaleriaActivity.class);
        intent.putExtra(EXTRA_TITLE, currentProyecto != null ? currentProyecto.getNombre() : "Proyecto");
        intent.putExtra(EXTRA_IMAGES, imagenesArray);
        startActivity(intent);
    }

    private void abrirEdicionProyecto() {
        Tipologia tipologia = currentTipologia;
        if (currentProyecto == null || tipologia == null) {
            Toast.makeText(this, "No hay tipología seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        int[] imagenesArray = tipologia.getImagenes() != null ? tipologia.getImagenes() : new int[0];

        Intent intent = new Intent(this, AdminProyectoEditarActivity.class);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_PROYECTO_ID, currentProyecto.getId());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_PROYECTO_TITULO, currentProyecto.getNombre());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_UBICACION, currentProyecto.getUbicacion());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_DESCRIPCION, tipologia.getDescripcion());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_PRECIO, tipologia.getPrecio());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_AREA, tipologia.getArea());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_DORMITORIOS, tipologia.getDormitorios());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_BANOS, tipologia.getBanos());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_ESTACIONAMIENTO, tipologia.getEstacionamiento());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_ESTADO, tipologia.getEstado());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_IMAGEN_HERO, tipologia.getImagenHero());
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_IMAGENES, imagenesArray);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_TIPOLOGIA_ACTUAL, tipologia.getNombre());
        startActivity(intent);
    }


    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void abrirImagenCompleta(List<Integer> imagenes, int selectedIndex) {
        if (imagenes == null || imagenes.isEmpty()) return;
        int[] imagenesArray = new int[imagenes.size()];
        for (int i = 0; i < imagenes.size(); i++) {
            imagenesArray[i] = imagenes.get(i);
        }
        int index = selectedIndex;
        if (index < 0 || index >= imagenesArray.length) {
            index = 0;
        }

        Intent intent = new Intent(this, AdminProyectoImagenActivity.class);
        intent.putExtra(AdminProyectoImagenActivity.EXTRA_IMAGES, imagenesArray);
        intent.putExtra(AdminProyectoImagenActivity.EXTRA_SELECTED_INDEX, index);
        intent.putExtra(AdminProyectoImagenActivity.EXTRA_TITLE,
                currentProyecto != null ? currentProyecto.getNombre() : "Proyecto");
        startActivity(intent);
    }

    private int getHeroIndex(List<Integer> imagenes, int heroRes) {
        if (imagenes == null || imagenes.isEmpty()) return 0;
        if (heroRes == 0) return 0;
        for (int i = 0; i < imagenes.size(); i++) {
            if (imagenes.get(i) != null && imagenes.get(i) == heroRes) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapaAdminProyecto != null) mapaAdminProyecto.onResume();
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

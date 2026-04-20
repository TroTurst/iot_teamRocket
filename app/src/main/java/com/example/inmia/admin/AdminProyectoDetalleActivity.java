package com.example.inmia.admin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class AdminProyectoDetalleActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGES = "extra_images";
    private static final String EXTRA_TITLE = "extra_title";

    private BottomNavigationView bottomNav;
    private final int totalNotificaciones = 5;

    private ImageView imgHeroProyecto;
    private android.widget.TextView tvNombreProyecto;
    private android.widget.TextView tvUbicacionProyecto;
    private android.widget.TextView tvDescripcionProyecto;
    private android.widget.TextView tvPrecioProyecto;
    private android.widget.TextView tvVerMasImagenes;
    private LinearLayout layoutMiniaturasProyecto;

    private android.widget.TextView btnTipologia45;
    private android.widget.TextView btnTipologia65;
    private android.widget.TextView btnTipologia90;

    private android.widget.TextView tvAreaProyecto;
    private android.widget.TextView tvDormitoriosProyecto;
    private android.widget.TextView tvBanosProyecto;
    private android.widget.TextView tvEstacionamientoProyecto;
    private android.widget.TextView tvPrecioEstimadoProyecto;
    private android.widget.TextView tvEstadoProyecto;

    private TipologiaData tipologia45;
    private TipologiaData tipologia65;
    private TipologiaData tipologia90;
    private TipologiaData currentTipologia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_proyecto_detalle);

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
        layoutMiniaturasProyecto = findViewById(R.id.layoutMiniaturasProyecto);

        btnTipologia45 = findViewById(R.id.btnTipologia45);
        btnTipologia65 = findViewById(R.id.btnTipologia65);
        btnTipologia90 = findViewById(R.id.btnTipologia90);

        tvAreaProyecto = findViewById(R.id.tvAreaProyecto);
        tvDormitoriosProyecto = findViewById(R.id.tvDormitoriosProyecto);
        tvBanosProyecto = findViewById(R.id.tvBanosProyecto);
        tvEstacionamientoProyecto = findViewById(R.id.tvEstacionamientoProyecto);
        tvPrecioEstimadoProyecto = findViewById(R.id.tvPrecioEstimadoProyecto);
        tvEstadoProyecto = findViewById(R.id.tvEstadoProyecto);

        tipologia45 = crearTipologia(
                "45 m² · 1d",
                "Departamento compacto y moderno ideal para una persona o una pareja. Espacios funcionales, cocina integrada y vista despejada a la ciudad.",
                R.drawable.onboarding1,
                "45 m²",
                "1",
                "1",
                "Sin estacionamiento",
                "S/ 420,000",
                "Disponible",
                R.drawable.onboarding1,
                R.drawable.onboarding2,
                R.drawable.onboarding3,
                R.drawable.images_2,
                R.drawable.onboarding2,
                R.drawable.onboarding1
        );
        tipologia65 = crearTipologia(
                "65 m² · 2d",
                "Departamento de dos dormitorios pensado para familias pequeñas. Sala amplia, iluminación natural y zona de trabajo independiente.",
                R.drawable.onboarding2,
                "65 m²",
                "2",
                "2",
                "1 incluido",
                "S/ 648,000",
                "Disponible",
                R.drawable.onboarding2,
                R.drawable.onboarding3,
                R.drawable.onboarding1,
                R.drawable.images_2,
                R.drawable.onboarding3,
                R.drawable.onboarding2
        );
        tipologia90 = crearTipologia(
                "90 m² · 3d",
                "La tipología más amplia del proyecto, con tres dormitorios, ambientes premium y un diseño ideal para familias grandes o inversión de alto valor.",
                R.drawable.onboarding3,
                "90 m²",
                "3",
                "3",
                "2 incluidos",
                "S/ 915,000",
                "Disponible",
                R.drawable.images_2,
                R.drawable.onboarding1,
                R.drawable.onboarding2,
                R.drawable.onboarding3,
                R.drawable.images_2,
                R.drawable.onboarding1
        );

        configurarSeleccion();

        frameNotificaciones.setOnClickListener(v ->
                Toast.makeText(this, "Tienes " + totalNotificaciones + " notificaciones", Toast.LENGTH_SHORT).show());

        btnBack.setOnClickListener(v -> finish());
        btnEditar.setOnClickListener(v -> abrirEdicionProyecto());

        btnTipologia45.setOnClickListener(v -> mostrarTipologia(tipologia45, btnTipologia45));
        btnTipologia65.setOnClickListener(v -> mostrarTipologia(tipologia65, btnTipologia65));
        btnTipologia90.setOnClickListener(v -> mostrarTipologia(tipologia90, btnTipologia90));

        mostrarTipologia(tipologia45, btnTipologia45);

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

    private void mostrarTipologia(TipologiaData tipologia, android.widget.TextView botonSeleccionado) {
        currentTipologia = tipologia;
        tvNombreProyecto.setText("Palm Living");
        tvUbicacionProyecto.setText("San Isidro, Lima");
        tvDescripcionProyecto.setText(tipologia.descripcion);
        tvPrecioProyecto.setText(tipologia.precio);
        imgHeroProyecto.setImageResource(tipologia.imagenPrincipal);

        tvAreaProyecto.setText(tipologia.area);
        tvDormitoriosProyecto.setText(tipologia.dormitorios);
        tvBanosProyecto.setText(tipologia.banos);
        tvEstacionamientoProyecto.setText(tipologia.estacionamiento);
        tvPrecioEstimadoProyecto.setText(tipologia.precio);
        tvEstadoProyecto.setText(tipologia.estado);

        actualizarEstadoTipologias(botonSeleccionado);
        renderizarMiniaturas(tipologia.imagenes);
    }

    private void actualizarEstadoTipologias(android.widget.TextView seleccionado) {
        configurarBotonTipologia(btnTipologia45, btnTipologia45 == seleccionado);
        configurarBotonTipologia(btnTipologia65, btnTipologia65 == seleccionado);
        configurarBotonTipologia(btnTipologia90, btnTipologia90 == seleccionado);
    }

    private void configurarBotonTipologia(android.widget.TextView boton, boolean seleccionado) {
        boton.setBackgroundResource(seleccionado ? R.drawable.btn_rounded : R.drawable.badge_outline);
        boton.setTextColor(seleccionado
                ? ContextCompat.getColor(this, R.color.inmia_white)
                : ContextCompat.getColor(this, R.color.inmia_teal_dark));
        boton.setTypeface(null, seleccionado ? Typeface.BOLD : Typeface.NORMAL);
    }

    private void renderizarMiniaturas(List<Integer> imagenes) {
        layoutMiniaturasProyecto.removeAllViews();
        tvVerMasImagenes.setVisibility(View.GONE);

        int previewCount = Math.min(4, imagenes.size());
        for (int i = 0; i < previewCount; i++) {
            layoutMiniaturasProyecto.addView(crearMiniatura(imagenes.get(i)));
        }

        int restantes = imagenes.size() - previewCount;
        if (restantes > 0) {
            tvVerMasImagenes.setText("Ver " + restantes + " +");
            tvVerMasImagenes.setVisibility(View.VISIBLE);
            tvVerMasImagenes.setOnClickListener(v -> abrirGaleriaCompleta(imagenes));
        }
    }

    private View crearMiniatura(int imageRes) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(dp(86), dp(62));
        cardParams.setMarginEnd(dp(8));
        card.setLayoutParams(cardParams);
        card.setRadius(dp(12));
        card.setCardElevation(0f);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(imageRes);
        card.addView(imageView);
        return card;
    }

    private void abrirGaleriaCompleta(List<Integer> imagenes) {
        int[] imagenesArray = new int[imagenes.size()];
        for (int i = 0; i < imagenes.size(); i++) {
            imagenesArray[i] = imagenes.get(i);
        }

        Intent intent = new Intent(this, AdminProyectoGaleriaActivity.class);
        intent.putExtra(EXTRA_TITLE, "Palm Living");
        intent.putExtra(EXTRA_IMAGES, imagenesArray);
        startActivity(intent);
    }

    private void abrirEdicionProyecto() {
        TipologiaData tipologia = currentTipologia != null ? currentTipologia : tipologia45;

        int[] imagenesArray = new int[tipologia.imagenes.size()];
        for (int i = 0; i < tipologia.imagenes.size(); i++) {
            imagenesArray[i] = tipologia.imagenes.get(i);
        }

        Intent intent = new Intent(this, AdminProyectoEditarActivity.class);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_PROYECTO_TITULO, "Palm Living");
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_UBICACION, "San Isidro, Lima");
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_DESCRIPCION, tipologia.descripcion);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_PRECIO, tipologia.precio);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_AREA, tipologia.area);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_DORMITORIOS, tipologia.dormitorios);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_BANOS, tipologia.banos);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_ESTACIONAMIENTO, tipologia.estacionamiento);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_ESTADO, tipologia.estado);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_IMAGEN_HERO, tipologia.imagenPrincipal);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_IMAGENES, imagenesArray);
        intent.putExtra(AdminProyectoEditarActivity.EXTRA_TIPOLOGIA_ACTUAL, tipologia.nombre);
        startActivity(intent);
    }

    private TipologiaData crearTipologia(String nombre, String descripcion, int heroImage, int... imagenes) {
        return crearTipologia(nombre, descripcion, heroImage, "", "", "", "", "", "", imagenes);
    }

    private TipologiaData crearTipologia(String nombre,
                                         String descripcion,
                                         int heroImage,
                                         String area,
                                         String dormitorios,
                                         String banos,
                                         String estacionamiento,
                                         String precio,
                                         String estado,
                                         int... imagenes) {
        List<Integer> list = new ArrayList<>();
        for (int image : imagenes) {
            list.add(image);
        }
        return new TipologiaData(nombre, descripcion, heroImage, area, dormitorios, banos, estacionamiento, precio, estado, list);
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics());
    }

    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private static class TipologiaData {
        final String nombre;
        final String descripcion;
        final int imagenPrincipal;
        final String area;
        final String dormitorios;
        final String banos;
        final String estacionamiento;
        final String precio;
        final String estado;
        final List<Integer> imagenes;

        TipologiaData(String nombre,
                      String descripcion,
                      int imagenPrincipal,
                      String area,
                      String dormitorios,
                      String banos,
                      String estacionamiento,
                      String precio,
                      String estado,
                      List<Integer> imagenes) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.imagenPrincipal = imagenPrincipal;
            this.area = area;
            this.dormitorios = dormitorios;
            this.banos = banos;
            this.estacionamiento = estacionamiento;
            this.precio = precio;
            this.estado = estado;
            this.imagenes = imagenes;
        }
    }
}


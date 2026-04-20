package com.example.inmia.admin;

import android.content.res.ColorStateList;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AdminProyectoNuevoActivity extends AppCompatActivity {

    public static final String EXTRA_PROYECTO_TITULO = "extra_proyecto_titulo";
    public static final String EXTRA_UBICACION = "extra_ubicacion";
    public static final String EXTRA_DESCRIPCION = "extra_descripcion";
    public static final String EXTRA_PRECIO = "extra_precio";
    public static final String EXTRA_AREA = "extra_area";
    public static final String EXTRA_DORMITORIOS = "extra_dormitorios";
    public static final String EXTRA_BANOS = "extra_banos";
    public static final String EXTRA_ESTACIONAMIENTO = "extra_estacionamiento";
    public static final String EXTRA_ESTADO = "extra_estado";
    public static final String EXTRA_IMAGEN_HERO = "extra_imagen_hero";
    public static final String EXTRA_IMAGENES = "extra_imagenes";
    public static final String EXTRA_TIPOLOGIA_ACTUAL = "extra_tipologia_actual";

    private BottomNavigationView bottomNav;

    private TextInputEditText etTitulo;
    private TextInputEditText etUbicacion;
    private TextInputEditText etDescripcion;
    private TextInputEditText etPrecio;
    private TextInputEditText etArea;
    private TextInputEditText etDormitorios;
    private TextInputEditText etBanos;
    private TextInputEditText etEstacionamiento;
    private TextInputEditText etEstado;

    private ImageView imgPreviewHero;
    private ImageView imgMini1;
    private ImageView imgMini2;
    private ImageView imgMini3;
    private ImageView imgMini4;
    private TextView tvPreviewMore;
    private TextView tvPreviewTitulo;
    private TextView tvPreviewUbicacion;
    private TextView tvPreviewPrecio;
    private TextView tvPreviewDescripcion;
    private TextView tvPreviewArea;
    private TextView tvPreviewDormitorios;
    private TextView tvPreviewBanos;
    private TextView tvPreviewEstacionamiento;
    private TextView tvPreviewEstado;

    private MaterialButton btnTipologia45Edit;
    private MaterialButton btnTipologia65Edit;
    private MaterialButton btnTipologia90Edit;
    private int[] imagenesProyecto;
    private int imagenHeroProyecto;

    private TipologiaData tipologia45;
    private TipologiaData tipologia65;
    private TipologiaData tipologia90;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_proyecto_nuevo);

        bottomNav = findViewById(R.id.bottomNavAdmin);
        View btnBack = findViewById(R.id.btnBackEditarProyecto);
        MaterialButton btnPrevisualizar = findViewById(R.id.btnPrevisualizarProyecto);
        MaterialButton btnConfirmar = findViewById(R.id.btnConfirmarEdicionProyecto);
        btnTipologia45Edit = findViewById(R.id.btnTipologia45Edit);
        btnTipologia65Edit = findViewById(R.id.btnTipologia65Edit);
        btnTipologia90Edit = findViewById(R.id.btnTipologia90Edit);
        MaterialButton btnAgregarTipologiaEdit = findViewById(R.id.btnAgregarTipologiaEdit);

        etTitulo = findViewById(R.id.etTituloProyecto);
        etUbicacion = findViewById(R.id.etUbicacionProyecto);
        etDescripcion = findViewById(R.id.etDescripcionProyecto);
        etPrecio = findViewById(R.id.etPrecioProyecto);
        etArea = findViewById(R.id.etAreaProyecto);
        etDormitorios = findViewById(R.id.etDormitoriosProyecto);
        etBanos = findViewById(R.id.etBanosProyecto);
        etEstacionamiento = findViewById(R.id.etEstacionamientoProyecto);
        etEstado = findViewById(R.id.etEstadoProyecto);

        imgPreviewHero = findViewById(R.id.imgPreviewHeroProyecto);
        imgMini1 = findViewById(R.id.imgMini1);
        imgMini2 = findViewById(R.id.imgMini2);
        imgMini3 = findViewById(R.id.imgMini3);
        imgMini4 = findViewById(R.id.imgMini4);
        tvPreviewMore = findViewById(R.id.tvPreviewMore);
        tvPreviewTitulo = findViewById(R.id.tvPreviewTitulo);
        tvPreviewUbicacion = findViewById(R.id.tvPreviewUbicacion);
        tvPreviewPrecio = findViewById(R.id.tvPreviewPrecio);
        tvPreviewDescripcion = findViewById(R.id.tvPreviewDescripcion);
        tvPreviewArea = findViewById(R.id.tvPreviewArea);
        tvPreviewDormitorios = findViewById(R.id.tvPreviewDormitorios);
        tvPreviewBanos = findViewById(R.id.tvPreviewBanos);
        tvPreviewEstacionamiento = findViewById(R.id.tvPreviewEstacionamiento);
        tvPreviewEstado = findViewById(R.id.tvPreviewEstado);

        tipologia45 = new TipologiaData(
                "45 m² · 1d",
                "Departamento compacto y moderno ideal para una persona o una pareja. Espacios funcionales, cocina integrada y vista despejada a la ciudad.",
                R.drawable.onboarding1,
                "45 m²",
                "1",
                "1",
                "Sin estacionamiento",
                "S/ 420,000",
                "Disponible",
                new int[]{R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.images_2, R.drawable.onboarding2, R.drawable.onboarding1}
        );
        tipologia65 = new TipologiaData(
                "65 m² · 2d",
                "Departamento de dos dormitorios pensado para familias pequeñas. Sala amplia, iluminación natural y zona de trabajo independiente.",
                R.drawable.onboarding2,
                "65 m²",
                "2",
                "2",
                "1 incluido",
                "S/ 648,000",
                "Disponible",
                new int[]{R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.onboarding1, R.drawable.images_2, R.drawable.onboarding3, R.drawable.onboarding2}
        );
        tipologia90 = new TipologiaData(
                "90 m² · 3d",
                "La tipología más amplia del proyecto, con tres dormitorios, ambientes premium y un diseño ideal para familias grandes o inversión de alto valor.",
                R.drawable.onboarding3,
                "90 m²",
                "3",
                "3",
                "2 incluidos",
                "S/ 915,000",
                "Disponible",
                new int[]{R.drawable.onboarding3, R.drawable.images_2, R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.images_2, R.drawable.onboarding1}
        );

        cargarDatosDesdeIntent();
        configurarSelectorTipologias();
        prepararFormularioNuevo();

        btnBack.setOnClickListener(v -> finish());
        btnPrevisualizar.setOnClickListener(v -> {
            actualizarPrevisualizacion();
            Toast.makeText(this, "Previsualización actualizada", Toast.LENGTH_SHORT).show();
        });
        btnConfirmar.setOnClickListener(v -> {
            actualizarPrevisualizacion();
            Toast.makeText(this, "Proyecto creado", Toast.LENGTH_SHORT).show();
            finish();
        });
        btnAgregarTipologiaEdit.setOnClickListener(v ->
                Toast.makeText(this, "Agregar tipología próximamente", Toast.LENGTH_SHORT).show());

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

    private void cargarDatosDesdeIntent() {
        Intent intent = getIntent();
        etTitulo.setText(valor(intent.getStringExtra(EXTRA_PROYECTO_TITULO), ""));
        etUbicacion.setText(valor(intent.getStringExtra(EXTRA_UBICACION), ""));
        etDescripcion.setText(valor(intent.getStringExtra(EXTRA_DESCRIPCION), ""));
        etPrecio.setText(valor(intent.getStringExtra(EXTRA_PRECIO), ""));
        etArea.setText(valor(intent.getStringExtra(EXTRA_AREA), ""));
        etDormitorios.setText(valor(intent.getStringExtra(EXTRA_DORMITORIOS), ""));
        etBanos.setText(valor(intent.getStringExtra(EXTRA_BANOS), ""));
        etEstacionamiento.setText(valor(intent.getStringExtra(EXTRA_ESTACIONAMIENTO), ""));
        etEstado.setText(valor(intent.getStringExtra(EXTRA_ESTADO), ""));
        imagenHeroProyecto = intent.getIntExtra(EXTRA_IMAGEN_HERO, 0);
        imagenesProyecto = intent.getIntArrayExtra(EXTRA_IMAGENES);
        if (imagenesProyecto == null || imagenesProyecto.length == 0) {
            imagenesProyecto = new int[0];
        }
    }

    private void configurarSelectorTipologias() {
        btnTipologia45Edit.setOnClickListener(v -> seleccionarTipologia(tipologia45));
        btnTipologia65Edit.setOnClickListener(v -> seleccionarTipologia(tipologia65));
        btnTipologia90Edit.setOnClickListener(v -> seleccionarTipologia(tipologia90));
    }

    private void prepararFormularioNuevo() {
        configurarBotonTipologia(btnTipologia45Edit, false);
        configurarBotonTipologia(btnTipologia65Edit, false);
        configurarBotonTipologia(btnTipologia90Edit, false);
        imagenHeroProyecto = 0;
        imagenesProyecto = new int[0];
        actualizarPrevisualizacion();
    }

    private void seleccionarTipologia(String codigo) {
        if (codigo.contains("90")) {
            seleccionarTipologia(tipologia90);
        } else if (codigo.contains("65")) {
            seleccionarTipologia(tipologia65);
        } else {
            seleccionarTipologia(tipologia45);
        }
    }

    private void seleccionarTipologia(TipologiaData tipologia) {
        aplicarTipologiaEnFormulario(tipologia);
        marcarTipologiaSeleccionada(tipologia);
        actualizarPrevisualizacion();
    }

    private void aplicarTipologiaEnFormulario(TipologiaData tipologia) {
        etDescripcion.setText(tipologia.descripcion);
        etPrecio.setText(tipologia.precio);
        etArea.setText(tipologia.area);
        etDormitorios.setText(tipologia.dormitorios);
        etBanos.setText(tipologia.banos);
        etEstacionamiento.setText(tipologia.estacionamiento);
        etEstado.setText(tipologia.estado);

        imagenHeroProyecto = tipologia.imagenHero;
        imagenesProyecto = tipologia.imagenes;
    }

    private void marcarTipologiaSeleccionada(TipologiaData tipologia) {
        if (tipologia == null) {
            configurarBotonTipologia(btnTipologia45Edit, false);
            configurarBotonTipologia(btnTipologia65Edit, false);
            configurarBotonTipologia(btnTipologia90Edit, false);
            return;
        }
        configurarBotonTipologia(btnTipologia45Edit, tipologia == tipologia45);
        configurarBotonTipologia(btnTipologia65Edit, tipologia == tipologia65);
        configurarBotonTipologia(btnTipologia90Edit, tipologia == tipologia90);
    }

    private void configurarBotonTipologia(MaterialButton button, boolean selected) {
        button.setBackgroundTintList(ColorStateList.valueOf(
                selected ? ContextCompat.getColor(this, R.color.inmia_teal_dark) : ContextCompat.getColor(this, R.color.inmia_white)));
        button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.inmia_teal_dark)));
        button.setStrokeWidth(1);
        button.setTextColor(selected
                ? ContextCompat.getColor(this, R.color.inmia_white)
                : ContextCompat.getColor(this, R.color.inmia_teal_dark));
    }

    private void actualizarPrevisualizacion() {
        String titulo = texto(etTitulo);
        String ubicacion = texto(etUbicacion);
        String descripcion = texto(etDescripcion);
        String precio = texto(etPrecio);
        String area = texto(etArea);
        String dormitorios = texto(etDormitorios);
        String banos = texto(etBanos);
        String estacionamiento = texto(etEstacionamiento);
        String estado = texto(etEstado);

        tvPreviewTitulo.setText(titulo);
        tvPreviewUbicacion.setText(ubicacion);
        tvPreviewPrecio.setText(precio);
        tvPreviewDescripcion.setText(descripcion);
        tvPreviewArea.setText(area);
        tvPreviewDormitorios.setText(dormitorios);
        tvPreviewBanos.setText(banos);
        tvPreviewEstacionamiento.setText(estacionamiento);
        tvPreviewEstado.setText(estado);
        if (imagenesProyecto != null && imagenesProyecto.length > 0) {
            if (imagenHeroProyecto != 0) {
                imgPreviewHero.setImageResource(imagenHeroProyecto);
            } else {
                imgPreviewHero.setImageDrawable(null);
            }

            imgMini1.setImageResource(imagenesProyecto[0]);
            imgMini2.setImageResource(imagenesProyecto[Math.min(1, imagenesProyecto.length - 1)]);
            imgMini3.setImageResource(imagenesProyecto[Math.min(2, imagenesProyecto.length - 1)]);
            imgMini4.setImageResource(imagenesProyecto[Math.min(3, imagenesProyecto.length - 1)]);

            int restantes = Math.max(0, imagenesProyecto.length - 4);
            if (restantes > 0) {
                tvPreviewMore.setText(getString(R.string.admin_project_gallery_more, restantes));
                tvPreviewMore.setVisibility(View.VISIBLE);
            } else {
                tvPreviewMore.setVisibility(View.GONE);
            }
        } else {
            imgPreviewHero.setImageDrawable(null);
            imgMini1.setImageDrawable(null);
            imgMini2.setImageDrawable(null);
            imgMini3.setImageDrawable(null);
            imgMini4.setImageDrawable(null);
            tvPreviewMore.setVisibility(View.GONE);
        }
    }

    private String texto(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private String valor(String actual, String fallback) {
        return actual != null && !actual.trim().isEmpty() ? actual : fallback;
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
        final int imagenHero;
        final String area;
        final String dormitorios;
        final String banos;
        final String estacionamiento;
        final String precio;
        final String estado;
        final int[] imagenes;

        TipologiaData(String nombre,
                      String descripcion,
                      int imagenHero,
                      String area,
                      String dormitorios,
                      String banos,
                      String estacionamiento,
                      String precio,
                      String estado,
                      int[] imagenes) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.imagenHero = imagenHero;
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










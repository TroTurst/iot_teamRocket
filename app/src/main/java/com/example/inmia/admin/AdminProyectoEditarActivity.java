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
import com.example.inmia.admin.data.AdminRepository;
import com.example.inmia.admin.data.AdminRepositoryProvider;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminProyectoEditarActivity extends AppCompatActivity {
    public static final String EXTRA_MODO_NUEVO = "extra_modo_nuevo";
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
    public static final String EXTRA_PROYECTO_ID = "extra_proyecto_id";

    private BottomNavigationView bottomNav;
    private TextView tvTituloFormulario;
    private TextView tvSubtituloFormulario;

    private AdminRepository repository;
    private String companyId;

    private TextInputEditText etTitulo;
    private TextInputEditText etUbicacion;
    private TextInputEditText etDescripcion;
    private TextInputEditText etPrecio;
    private TextInputEditText etArea;
    private TextInputEditText etDormitorios;
    private TextInputEditText etBanos;
    private TextInputEditText etEstacionamiento;
    private TextInputEditText etEstado;
    private TextInputEditText etInmobiliaria;

    private SwitchMaterial switchPetFriendly;
    private SwitchMaterial switchConAscensor;

    private TextInputEditText etCertificadoEnergetico;
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
    private MaterialButton btnAgregarTipologiaEdit;

    private int[] imagenesProyecto;
    private int imagenHeroProyecto;
    private boolean modoNuevo;
    private String proyectoId;

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

        setContentView(R.layout.activity_admin_proyecto_editar);

        repository = AdminRepositoryProvider.get();
        companyId = repository.getCompanyIdForEmail(AdminSessionDefaults.DEFAULT_ADMIN_EMAIL);

        bottomNav = findViewById(R.id.bottomNavAdmin);
        tvTituloFormulario = findViewById(R.id.tvTituloFormularioProyecto);
        tvSubtituloFormulario = findViewById(R.id.tvSubtituloFormularioProyecto);
        View btnBack = findViewById(R.id.btnBackEditarProyecto);
        MaterialButton btnPrevisualizar = findViewById(R.id.btnPrevisualizarProyecto);
        MaterialButton btnConfirmar = findViewById(R.id.btnConfirmarEdicionProyecto);
        btnTipologia45Edit = findViewById(R.id.btnTipologia45Edit);
        btnTipologia65Edit = findViewById(R.id.btnTipologia65Edit);
        btnTipologia90Edit = findViewById(R.id.btnTipologia90Edit);
        btnAgregarTipologiaEdit = findViewById(R.id.btnAgregarTipologiaEdit);

        modoNuevo = getIntent().getBooleanExtra(EXTRA_MODO_NUEVO, false);
        proyectoId = getIntent().getStringExtra(EXTRA_PROYECTO_ID);

        etTitulo = findViewById(R.id.etTituloProyecto);
        etUbicacion = findViewById(R.id.etUbicacionProyecto);
        etDescripcion = findViewById(R.id.etDescripcionProyecto);
        etPrecio = findViewById(R.id.etPrecioProyecto);
        etArea = findViewById(R.id.etAreaProyecto);
        etDormitorios = findViewById(R.id.etDormitoriosProyecto);
        etBanos = findViewById(R.id.etBanosProyecto);
        etEstacionamiento = findViewById(R.id.etEstacionamientoProyecto);
        etEstado = findViewById(R.id.etEstadoProyecto);
        etInmobiliaria = findViewById(R.id.etInmobiliariaProyecto);

        switchPetFriendly = findViewById(R.id.switchPetFriendly);
        switchConAscensor = findViewById(R.id.switchConAscensor);

        etCertificadoEnergetico = findViewById(R.id.etCertificadoEnergetico);
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
                new int[]{R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.images_2, R.drawable.onboarding2, R.drawable.onboarding1},
                false,
                "B",
                false,
                false,
                false,
                false,
                0,
                "estandar",
                false,
                "natural",
                false,
                "basico"
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
                new int[]{R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.onboarding1, R.drawable.images_2, R.drawable.onboarding3, R.drawable.onboarding2},
                true,
                "B",
                true,
                true,
                true,
                true,
                2,
                "premium",
                true,
                "forzada",
                true,
                "medio"
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
                new int[]{R.drawable.onboarding3, R.drawable.images_2, R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.images_2, R.drawable.onboarding1},
                true,
                "A",
                true,
                true,
                true,
                true,
                3,
                "premium",
                true,
                "forzada",
                true,
                "alto"
        );

        cargarDatosDesdeIntent(modoNuevo);
        configurarSelectorTipologias();
        if (modoNuevo) {
            prepararFormularioNuevo();
        } else {
            seleccionarTipologia(inicialTipologiaDesdeIntent());
        }

        if (modoNuevo) {
            tvTituloFormulario.setText("Nuevo proyecto");
            tvSubtituloFormulario.setText("Completa el formulario para crear un proyecto nuevo");
            btnConfirmar.setText("Crear proyecto");
        }

        btnBack.setOnClickListener(v -> finish());
        btnPrevisualizar.setOnClickListener(v -> {
            actualizarPrevisualizacion();
            Toast.makeText(this, "Previsualización actualizada", Toast.LENGTH_SHORT).show();
        });
        btnConfirmar.setOnClickListener(v -> {
            actualizarPrevisualizacion();
            if (modoNuevo) {
                Proyecto nuevoProyecto = construirProyectoDesdeFormulario();
                if (nuevoProyecto != null) {
                    repository.addProject(companyId, nuevoProyecto);
                    Toast.makeText(this, "Proyecto creado", Toast.LENGTH_SHORT).show();
                }
            } else {
                guardarEdicionProyecto();
            }
            Toast.makeText(this, modoNuevo ? "Proyecto creado" : "Edicion confirmada", Toast.LENGTH_SHORT).show();
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

    private void cargarDatosDesdeIntent(boolean modoNuevo) {
        if (!modoNuevo && proyectoId != null) {
            Proyecto proyecto = repository.getProjectById(companyId, proyectoId);
            if (proyecto != null) {
                cargarDesdeProyecto(proyecto);
                return;
            }
        }
        Intent intent = getIntent();
        etTitulo.setText(valor(intent.getStringExtra(EXTRA_PROYECTO_TITULO), modoNuevo ? "" : "Palm Living"));
        etUbicacion.setText(valor(intent.getStringExtra(EXTRA_UBICACION), modoNuevo ? "" : "San Isidro, Lima"));
        etDescripcion.setText(valor(intent.getStringExtra(EXTRA_DESCRIPCION), modoNuevo ? "" : ""));
        etPrecio.setText(valor(intent.getStringExtra(EXTRA_PRECIO), modoNuevo ? "" : ""));
        etArea.setText(valor(intent.getStringExtra(EXTRA_AREA), modoNuevo ? "" : ""));
        etDormitorios.setText(valor(intent.getStringExtra(EXTRA_DORMITORIOS), modoNuevo ? "" : ""));
        etBanos.setText(valor(intent.getStringExtra(EXTRA_BANOS), modoNuevo ? "" : ""));
        etEstacionamiento.setText(valor(intent.getStringExtra(EXTRA_ESTACIONAMIENTO), modoNuevo ? "" : ""));
        etEstado.setText(valor(intent.getStringExtra(EXTRA_ESTADO), modoNuevo ? "" : ""));
        imagenHeroProyecto = intent.getIntExtra(EXTRA_IMAGEN_HERO, modoNuevo ? 0 : R.drawable.onboarding1);
        imagenesProyecto = intent.getIntArrayExtra(EXTRA_IMAGENES);
        if (imagenesProyecto == null || imagenesProyecto.length == 0) {
            if (modoNuevo) {
                imagenesProyecto = new int[0];
                return;
            }
            imagenesProyecto = new int[]{R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3, R.drawable.images_2};
        }
    }

    private void cargarDesdeProyecto(Proyecto proyecto) {
        etTitulo.setText(valor(proyecto.getNombre(), ""));
        etUbicacion.setText(valor(proyecto.getUbicacion(), ""));
        etDescripcion.setText(valor(proyecto.getDescripcion(), ""));
        etEstado.setText(valor(proyecto.getEstadoProyecto(), ""));
        etInmobiliaria.setText(valor(proyecto.getInmobiliaria(), ""));
        imagenHeroProyecto = proyecto.getImagenHeroPrincipal();
        imagenesProyecto = proyecto.getImagenes() != null ? proyecto.getImagenes() : new int[0];

        if (switchPetFriendly != null) switchPetFriendly.setChecked(proyecto.isPetFriendly());
        if (switchConAscensor != null) switchConAscensor.setChecked(proyecto.isConAscensor());

        Tipologia tipologia = proyecto.getTipologiaPrincipal();
        if (tipologia != null) {
            etPrecio.setText(valor(tipologia.getPrecio(), ""));
            etArea.setText(valor(tipologia.getArea(), ""));
            etDormitorios.setText(valor(tipologia.getDormitorios(), ""));
            etBanos.setText(valor(tipologia.getBanos(), ""));
            etEstacionamiento.setText(valor(tipologia.getEstacionamiento(), ""));
            etCertificadoEnergetico.setText(valor(tipologia.getCertificadoEnergetico(), ""));
            etClosets.setText(String.valueOf(tipologia.getClosets()));
            etTipoPiso.setText(valor(tipologia.getTipoPiso(), ""));
            etVentilacion.setText(valor(tipologia.getVentilacion(), ""));
            etTipoAcabados.setText(valor(tipologia.getTipoAcabados(), ""));

            if (switchPatio != null) switchPatio.setChecked(tipologia.isPatio());
            if (switchTerraza != null) switchTerraza.setChecked(tipologia.isTerraza());
            if (switchBalcon != null) switchBalcon.setChecked(tipologia.isBalcon());
            if (switchAireAcondicionado != null) switchAireAcondicionado.setChecked(tipologia.isAireAcondicionado());
            if (switchCocinaIntegrada != null) switchCocinaIntegrada.setChecked(tipologia.isCocinaIntegrada());
            if (switchAmueblado != null) switchAmueblado.setChecked(tipologia.isAmueblado());
            if (switchPersianas != null) switchPersianas.setChecked(tipologia.isPersianasAutomaticas());
        }
    }

    private String inicialTipologiaDesdeIntent() {
        String tipologia = getIntent().getStringExtra(EXTRA_TIPOLOGIA_ACTUAL);
        return tipologia != null ? tipologia : "45";
    }

    private void configurarSelectorTipologias() {
        btnTipologia45Edit.setOnClickListener(v -> seleccionarTipologia(tipologia45));
        btnTipologia65Edit.setOnClickListener(v -> seleccionarTipologia(tipologia65));
        btnTipologia90Edit.setOnClickListener(v -> seleccionarTipologia(tipologia90));
    }

    private void prepararFormularioNuevo() {
        currentTipologia = null;
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
        currentTipologia = tipologia;
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
        etCertificadoEnergetico.setText(tipologia.certificadoEnergetico);
        etClosets.setText(String.valueOf(tipologia.closets));
        etTipoPiso.setText(tipologia.tipoPiso);
        etVentilacion.setText(tipologia.ventilacion);
        etTipoAcabados.setText(tipologia.tipoAcabados);

        if (switchPatio != null) switchPatio.setChecked(tipologia.patio);
        if (switchTerraza != null) switchTerraza.setChecked(tipologia.terraza);
        if (switchBalcon != null) switchBalcon.setChecked(tipologia.balcon);
        if (switchAireAcondicionado != null) switchAireAcondicionado.setChecked(tipologia.aireAcondicionado);
        if (switchCocinaIntegrada != null) switchCocinaIntegrada.setChecked(tipologia.cocinaIntegrada);
        if (switchAmueblado != null) switchAmueblado.setChecked(tipologia.amueblado);
        if (switchPersianas != null) switchPersianas.setChecked(tipologia.persianasAutomaticas);

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

    private Proyecto construirProyectoDesdeFormulario() {
        String titulo = texto(etTitulo);
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Ingresa un titulo de proyecto", Toast.LENGTH_SHORT).show();
            return null;
        }
        String ubicacion = texto(etUbicacion);
        String descripcion = texto(etDescripcion);
        String precio = texto(etPrecio);
        String area = texto(etArea);
        String dormitorios = texto(etDormitorios);
        String banos = texto(etBanos);
        String estacionamiento = texto(etEstacionamiento);
        String estado = texto(etEstado);
        String inmobiliaria = texto(etInmobiliaria);

        boolean petFriendly = switchPetFriendly != null && switchPetFriendly.isChecked();
        boolean conAscensor = switchConAscensor != null && switchConAscensor.isChecked();

        String certificado = texto(etCertificadoEnergetico);
        boolean patio = switchPatio != null && switchPatio.isChecked();
        boolean terraza = switchTerraza != null && switchTerraza.isChecked();
        boolean balcon = switchBalcon != null && switchBalcon.isChecked();
        boolean aireAcondicionado = switchAireAcondicionado != null && switchAireAcondicionado.isChecked();
        boolean cocinaIntegrada = switchCocinaIntegrada != null && switchCocinaIntegrada.isChecked();
        boolean amueblado = switchAmueblado != null && switchAmueblado.isChecked();
        boolean persianas = switchPersianas != null && switchPersianas.isChecked();
        int closets = parseInt(etClosets);
        String tipoPiso = texto(etTipoPiso);
        String ventilacion = texto(etVentilacion);
        String tipoAcabados = texto(etTipoAcabados);

        int hero = imagenHeroProyecto != 0 ? imagenHeroProyecto : R.drawable.onboarding1;
        int[] galeria = imagenesProyecto != null && imagenesProyecto.length > 0
                ? imagenesProyecto
                : new int[]{hero};

        String tipologiaId = "tip_" + System.currentTimeMillis();
        Tipologia tipologia = new Tipologia(
                tipologiaId,
                area.isEmpty() ? "Tipologia" : area + " · " + dormitorios + "d",
                descripcion.isEmpty() ? "Sin descripcion" : descripcion,
                area.isEmpty() ? "-" : area,
                dormitorios.isEmpty() ? "0" : dormitorios,
                banos.isEmpty() ? "0" : banos,
                estacionamiento.isEmpty() ? "-" : estacionamiento,
                precio.isEmpty() ? "-" : precio,
                estado.isEmpty() ? "Disponible" : estado,
                hero,
                galeria,
                patio,
                certificado.isEmpty() ? "B" : certificado,
                terraza,
                balcon,
                aireAcondicionado,
                cocinaIntegrada,
                closets,
                tipoPiso.isEmpty() ? "estandar" : tipoPiso,
                amueblado,
                ventilacion.isEmpty() ? "natural" : ventilacion,
                persianas,
                tipoAcabados.isEmpty() ? "basico" : tipoAcabados
        );

        List<Tipologia> tipologias = new ArrayList<>();
        tipologias.add(tipologia);

        String idProyecto = "proy_" + System.currentTimeMillis();
        return new Proyecto(
                idProyecto,
                titulo,
                ubicacion.isEmpty() ? "Sin ubicacion" : ubicacion,
                descripcion.isEmpty() ? "Sin descripcion" : descripcion,
                new ArrayList<>(),
                estado.isEmpty() ? "En planos" : estado,
                galeria,
                hero,
                tipologias,
                inmobiliaria.isEmpty() ? "Inmobiliaria INMIA" : inmobiliaria,
                conAscensor,
                "Nuevo",
                "",
                "REF-" + idProyecto,
                petFriendly,
                new ArrayList<>(),
                tipologia,
                ""
        );
    }

    private void guardarEdicionProyecto() {
        if (proyectoId == null) {
            Toast.makeText(this, "No se encontro el proyecto", Toast.LENGTH_SHORT).show();
            return;
        }
        Proyecto proyecto = repository.getProjectById(companyId, proyectoId);
        if (proyecto == null) {
            Toast.makeText(this, "No se encontro el proyecto", Toast.LENGTH_SHORT).show();
            return;
        }

        String titulo = texto(etTitulo);
        String ubicacion = texto(etUbicacion);
        String descripcion = texto(etDescripcion);
        String estado = texto(etEstado);
        String inmobiliaria = texto(etInmobiliaria);

        boolean petFriendly = switchPetFriendly != null && switchPetFriendly.isChecked();
        boolean conAscensor = switchConAscensor != null && switchConAscensor.isChecked();

        Tipologia tipologia = construirTipologiaDesdeFormulario();
        List<Tipologia> tipologias = proyecto.getTipologias() != null ? new ArrayList<>(proyecto.getTipologias()) : new ArrayList<>();
        if (tipologias.isEmpty()) {
            tipologias.add(tipologia);
        } else {
            tipologias.set(0, tipologia);
        }

        proyecto.setNombre(titulo);
        proyecto.setUbicacion(ubicacion);
        proyecto.setDescripcion(descripcion);
        proyecto.setEstadoProyecto(estado);
        proyecto.setInmobiliaria(inmobiliaria);
        proyecto.setPetFriendly(petFriendly);
        proyecto.setConAscensor(conAscensor);
        proyecto.setImagenHeroPrincipal(imagenHeroProyecto);
        proyecto.setImagenes(imagenesProyecto);
        proyecto.setTipologias(tipologias);
        proyecto.setTipologiaPrincipal(tipologia);
    }

    private Tipologia construirTipologiaDesdeFormulario() {
        String descripcion = texto(etDescripcion);
        String precio = texto(etPrecio);
        String area = texto(etArea);
        String dormitorios = texto(etDormitorios);
        String banos = texto(etBanos);
        String estacionamiento = texto(etEstacionamiento);
        String estado = texto(etEstado);
        String certificado = texto(etCertificadoEnergetico);
        boolean patio = switchPatio != null && switchPatio.isChecked();
        boolean terraza = switchTerraza != null && switchTerraza.isChecked();
        boolean balcon = switchBalcon != null && switchBalcon.isChecked();
        boolean aireAcondicionado = switchAireAcondicionado != null && switchAireAcondicionado.isChecked();
        boolean cocinaIntegrada = switchCocinaIntegrada != null && switchCocinaIntegrada.isChecked();
        boolean amueblado = switchAmueblado != null && switchAmueblado.isChecked();
        boolean persianas = switchPersianas != null && switchPersianas.isChecked();
        int closets = parseInt(etClosets);
        String tipoPiso = texto(etTipoPiso);
        String ventilacion = texto(etVentilacion);
        String tipoAcabados = texto(etTipoAcabados);

        int hero = imagenHeroProyecto != 0 ? imagenHeroProyecto : R.drawable.onboarding1;
        int[] galeria = imagenesProyecto != null && imagenesProyecto.length > 0
                ? imagenesProyecto
                : new int[]{hero};

        String tipologiaId = "tip_" + System.currentTimeMillis();
        return new Tipologia(
                tipologiaId,
                area.isEmpty() ? "Tipologia" : area + " · " + dormitorios + "d",
                descripcion.isEmpty() ? "Sin descripcion" : descripcion,
                area.isEmpty() ? "-" : area,
                dormitorios.isEmpty() ? "0" : dormitorios,
                banos.isEmpty() ? "0" : banos,
                estacionamiento.isEmpty() ? "-" : estacionamiento,
                precio.isEmpty() ? "-" : precio,
                estado.isEmpty() ? "Disponible" : estado,
                hero,
                galeria,
                patio,
                certificado.isEmpty() ? "B" : certificado,
                terraza,
                balcon,
                aireAcondicionado,
                cocinaIntegrada,
                closets,
                tipoPiso.isEmpty() ? "estandar" : tipoPiso,
                amueblado,
                ventilacion.isEmpty() ? "natural" : ventilacion,
                persianas,
                tipoAcabados.isEmpty() ? "basico" : tipoAcabados
        );
    }

    private int parseInt(TextInputEditText editText) {
        String value = texto(editText);
        try {
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
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
        final boolean patio;
        final String certificadoEnergetico;
        final boolean terraza;
        final boolean balcon;
        final boolean aireAcondicionado;
        final boolean cocinaIntegrada;
        final int closets;
        final String tipoPiso;
        final boolean amueblado;
        final String ventilacion;
        final boolean persianasAutomaticas;
        final String tipoAcabados;

        TipologiaData(String nombre,
                      String descripcion,
                      int imagenHero,
                      String area,
                      String dormitorios,
                      String banos,
                      String estacionamiento,
                      String precio,
                      String estado,
                      int[] imagenes,
                      boolean patio,
                      String certificadoEnergetico,
                      boolean terraza,
                      boolean balcon,
                      boolean aireAcondicionado,
                      boolean cocinaIntegrada,
                      int closets,
                      String tipoPiso,
                      boolean amueblado,
                      String ventilacion,
                      boolean persianasAutomaticas,
                      String tipoAcabados) {
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
            this.patio = patio;
            this.certificadoEnergetico = certificadoEnergetico;
            this.terraza = terraza;
            this.balcon = balcon;
            this.aireAcondicionado = aireAcondicionado;
            this.cocinaIntegrada = cocinaIntegrada;
            this.closets = closets;
            this.tipoPiso = tipoPiso;
            this.amueblado = amueblado;
            this.ventilacion = ventilacion;
            this.persianasAutomaticas = persianasAutomaticas;
            this.tipoAcabados = tipoAcabados;
        }
    }
}

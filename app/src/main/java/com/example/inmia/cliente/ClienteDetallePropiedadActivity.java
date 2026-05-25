package com.example.inmia.cliente;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Tipologia;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

public class ClienteDetallePropiedadActivity extends AppCompatActivity {

    // Vistas principales
    private TextView tvNombreProyecto, tvUbicacionProyecto, tvDescripcionProyecto, tvPrecioProyecto;
    private TextView tvInmobiliariaProyecto, tvReferenciaProyecto, tvAntiguedadProyecto, tvFechaLanzamientoProyecto, tvEstadoGeneralProyecto;
    private ChipGroup chipGroupProyectoFeatures, chipGroupProyectoExtras;

    // Datos del departamento
    private TextView tvAreaProyecto, tvDormitoriosProyecto, tvBanosProyecto, tvEstacionamientoProyecto, tvPrecioEstimadoProyecto, tvEstadoProyecto;

    // Ficha Tipología
    private TextView tvTipologiaNombre, tvTipologiaCertificado, tvTipologiaTipoPiso, tvTipologiaVentilacion, tvTipologiaAcabados;
    private ChipGroup chipGroupTipologiaFeatures;

    private ImageView imgHeroProyecto;
    private View btnBack, btnCompartirQR, btnReservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_detalle_propiedad_cliente);

        inicializarVistas();
        configurarListeners();
        llenarDatosDemo();
        configurarMapa();
        configurarRecyclerTipologias();
    }

    private void inicializarVistas() {
        tvNombreProyecto = findViewById(R.id.tvNombreProyecto);
        tvUbicacionProyecto = findViewById(R.id.tvUbicacionProyecto);
        tvDescripcionProyecto = findViewById(R.id.tvDescripcionProyecto);
        tvPrecioProyecto = findViewById(R.id.tvPrecioProyecto);
        imgHeroProyecto = findViewById(R.id.imgHeroProyecto);

        tvInmobiliariaProyecto = findViewById(R.id.tvInmobiliariaProyecto);
        tvReferenciaProyecto = findViewById(R.id.tvReferenciaProyecto);
        tvAntiguedadProyecto = findViewById(R.id.tvAntiguedadProyecto);
        tvFechaLanzamientoProyecto = findViewById(R.id.tvFechaLanzamientoProyecto);
        tvEstadoGeneralProyecto = findViewById(R.id.tvEstadoGeneralProyecto);
        chipGroupProyectoFeatures = findViewById(R.id.chipGroupProyectoFeatures);
        chipGroupProyectoExtras = findViewById(R.id.chipGroupProyectoExtras);

        tvAreaProyecto = findViewById(R.id.tvAreaProyecto);
        tvDormitoriosProyecto = findViewById(R.id.tvDormitoriosProyecto);
        tvBanosProyecto = findViewById(R.id.tvBanosProyecto);
        tvEstacionamientoProyecto = findViewById(R.id.tvEstacionamientoProyecto);
        tvPrecioEstimadoProyecto = findViewById(R.id.tvPrecioEstimadoProyecto);
        tvEstadoProyecto = findViewById(R.id.tvEstadoProyecto);

        tvTipologiaNombre = findViewById(R.id.tvTipologiaNombre);
        tvTipologiaCertificado = findViewById(R.id.tvTipologiaCertificado);
        tvTipologiaTipoPiso = findViewById(R.id.tvTipologiaTipoPiso);
        tvTipologiaVentilacion = findViewById(R.id.tvTipologiaVentilacion);
        tvTipologiaAcabados = findViewById(R.id.tvTipologiaAcabados);
        chipGroupTipologiaFeatures = findViewById(R.id.chipGroupTipologiaFeatures);

        btnBack = findViewById(R.id.btnBack);
        btnCompartirQR = findViewById(R.id.btnCompartirQR);
        btnReservar = findViewById(R.id.btnReservar);
    }

    private void configurarListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnCompartirQR.setOnClickListener(v -> showPopupMenu(v));

        btnReservar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ClienteRegistrarCitaActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        if (bottomNav != null) {
            bottomNav.getMenu().setGroupCheckable(0, false, true);
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_inicio) { startActivity(new Intent(this, ClienteHomeActivity.class)); finish(); return true; }
                else if (id == R.id.nav_citas) { startActivity(new Intent(this, ClienteCitasActivity.class)); finish(); return true; }
                else if (id == R.id.nav_chat) { startActivity(new Intent(this, ClienteMensajesActivity.class)); finish(); return true; }
                else if (id == R.id.nav_separaciones) { startActivity(new Intent(this, ClienteSeparacionesActivity.class)); finish(); return true; }
                else if (id == R.id.nav_perfil) { startActivity(new Intent(this, ClientePerfilClienteActivity.class)); finish(); return true; }
                return false;
            });
        }
    }

    private void llenarDatosDemo() {

        tvNombreProyecto.setText("Palm Living");
        tvUbicacionProyecto.setText("San Isidro, Lima");
        tvDescripcionProyecto.setText("Moderno departamento con vista panorámica a la ciudad...");
        imgHeroProyecto.setImageResource(R.drawable.onboarding1);
        tvPrecioProyecto.setText("Desde S/ 648,000");

        tvInmobiliariaProyecto.setText("GALEÓN INMOBILIARIA");
        tvReferenciaProyecto.setText("REF-PL-890");
        tvAntiguedadProyecto.setText("En construcción");
        tvFechaLanzamientoProyecto.setText("Octubre 2026");
        tvEstadoGeneralProyecto.setText("En preventa");


        agregarChip(chipGroupProyectoFeatures, "Con Ascensor");
        agregarChip(chipGroupProyectoFeatures, "Pet Friendly");
        agregarChip(chipGroupProyectoExtras, "Piscina Infinity");
        agregarChip(chipGroupProyectoExtras, "Coworking");


        tvAreaProyecto.setText("85 m²");
        tvDormitoriosProyecto.setText("3");
        tvBanosProyecto.setText("2");
        tvEstacionamientoProyecto.setText("1 incluido");
        tvPrecioEstimadoProyecto.setText("S/ 648,000");
        tvEstadoProyecto.setText("Disponible");
        tvTipologiaNombre.setText("Flat 85m² Vista Calle");
        tvTipologiaCertificado.setText("A+ (Alta Eficiencia)");
        tvTipologiaTipoPiso.setText("Madera Estructurada");
        tvTipologiaVentilacion.setText("Natural Cruzada");
        tvTipologiaAcabados.setText("Premium");
        agregarChip(chipGroupTipologiaFeatures, "Balcón amplio");
        agregarChip(chipGroupTipologiaFeatures, "Cocina Equipada");

    }

    private void agregarChip(ChipGroup chipGroup, String texto) {
        Chip chip = new Chip(this);
        chip.setText(texto);
        chip.setChipBackgroundColorResource(R.color.inmia_teal_light);
        chip.setTextColor(getResources().getColor(R.color.inmia_teal_dark));
        chipGroup.addView(chip);
    }

    private void configurarMapa() {
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this));
        MapView mapa = findViewById(R.id.mapaClienteProyecto);
        mapa.setMultiTouchControls(true);
        IMapController mapController = mapa.getController();
        mapController.setZoom(18.0);
        mapController.setCenter(new GeoPoint(-12.0975, -77.0366));
    }

    private void configurarRecyclerTipologias() {
        RecyclerView rv = findViewById(R.id.recyclerViewTipologias);
        rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        List<Tipologia> lista = new ArrayList<>();
        lista.add(new Tipologia("1", "Flat 85m² Vista Calle",
                "Moderno", "85m²", "3",
                "2", "Sí", "S/ 648,000",
                "Disponible", 0, null,
                false, "A+", false,
                true, false, true,
                2, "Madera", false,
                "Natural", false, "Premium"));
        lista.add(new Tipologia("2", "Flat 120m² Penthouse",
                "Lujoso.", "120m²", "4",
                "3", "Sí", "S/ 950,000",
                "Disponible", 0, null,
                false, "A+", false,
                true, false, true,
                3, "Porcelanato", false,
                "Natural", false, "Lujo"));

        SimpleTipologiaAdapter adapter = new SimpleTipologiaAdapter(lista, tp -> {
            actualizarFicha(tp);
        });
        rv.setAdapter(adapter);

        if (!lista.isEmpty()) actualizarFicha(lista.get(0));
    }

    private void actualizarFicha(Tipologia tp) {
        tvTipologiaNombre.setText(tp.getNombre());
        tvTipologiaCertificado.setText(tp.getCertificadoEnergetico());
        tvTipologiaTipoPiso.setText(tp.getTipoPiso());
        tvTipologiaVentilacion.setText(tp.getVentilacion());
        tvTipologiaAcabados.setText(tp.getTipoAcabados());

        chipGroupTipologiaFeatures.removeAllViews();
        if(tp.isBalcon()) agregarChip(chipGroupTipologiaFeatures, "Balcón");
        if(tp.isCocinaIntegrada()) agregarChip(chipGroupTipologiaFeatures, "Cocina Integrada");
    }


    private void descargarBrochureOQR() {
        String url = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Brochure Palm Living");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Palm_Living.jpg");
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) manager.enqueue(request);
        Toast.makeText(this, "Descarga iniciada", Toast.LENGTH_SHORT).show();
    }
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);

        popup.getMenu().add("Compartir Proyecto");
        popup.getMenu().add("Descargar Brochure");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Compartir Proyecto":
                    Toast.makeText(this, "proximamente", Toast.LENGTH_SHORT).show();
                    return true;
                case "Descargar Brochure":
                    descargarBrochureOQR();
                default:
                    return false;
            }
        });

        popup.show();
    }
}
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

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.models.Log;
import com.example.inmia.models.Tipologia;
import com.example.inmia.util.LogHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteDetallePropiedadActivity extends AppCompatActivity {

    private TextView tvNombreProyecto, tvUbicacionProyecto, tvDescripcionProyecto, tvPrecioProyecto;
    private TextView tvInmobiliariaProyecto, tvReferenciaProyecto, tvAntiguedadProyecto, tvFechaLanzamientoProyecto, tvEstadoGeneralProyecto;
    private ChipGroup chipGroupProyectoFeatures, chipGroupProyectoExtras;

    private TextView tvPriceMain, tvPriceSub;


    private TextView tvTipologiaNombre, tvTipologiaCertificado, tvTipologiaTipoPiso, tvTipologiaVentilacion, tvTipologiaAcabados;
    private TextView tvAreaProyecto, tvDormitoriosProyecto, tvBanosProyecto, tvEstacionamientoProyecto, tvPrecioEstimadoProyecto, tvEstadoProyecto;
    private ChipGroup chipGroupTipologiaFeatures;

    private ImageView imgHeroProyecto;
    private View btnBack, btnCompartirQR, btnReservar;
    private MaterialButton btnAgendarVisita;
    private RecyclerView rvTipologias;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String proyectoId = "";
    private String proyectoNombre = "";
    private String tipologiaSeleccionadaActual = "";

    private double montoSeparacionActual = 0.0;
    private String imagenProyectoUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_detalle_propiedad_cliente);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (getIntent() != null) {
            if (getIntent().hasExtra("PROYECTO_ID")) proyectoId = getIntent().getStringExtra("PROYECTO_ID");
            if (getIntent().hasExtra("PROYECTO_NOMBRE")) proyectoNombre = getIntent().getStringExtra("PROYECTO_NOMBRE");
        }

        inicializarVistas();
        configurarListeners();
        configurarMapa();

        cargarDatosDeFirestore();
    }

    private void inicializarVistas() {
        tvNombreProyecto = findViewById(R.id.tvNombreProyecto);
        tvUbicacionProyecto = findViewById(R.id.tvUbicacionProyecto);
        tvDescripcionProyecto = findViewById(R.id.tvDescripcionProyecto);
        tvPrecioProyecto = findViewById(R.id.tvPrecioProyecto);
        imgHeroProyecto = findViewById(R.id.imgHeroProyecto);

        tvPriceMain = findViewById(R.id.tvPriceMain);
        tvPriceSub = findViewById(R.id.tvPriceSub);

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

        rvTipologias = findViewById(R.id.recyclerViewTipologias);
        rvTipologias.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        btnBack = findViewById(R.id.btnBack);
        btnCompartirQR = findViewById(R.id.btnCompartirQR);
        btnReservar = findViewById(R.id.btnReservar);
        btnAgendarVisita = findViewById(R.id.btnAgendarVisita);
    }

    private void cargarDatosDeFirestore() {
        if (proyectoId.isEmpty()) return;

        db.collection("proyectos").document(proyectoId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        proyectoNombre = document.getString("nombre");
                        tvNombreProyecto.setText(proyectoNombre);
                        tvDescripcionProyecto.setText(document.getString("descripcion"));
                        tvEstadoGeneralProyecto.setText(document.getString("estado"));

                        Map<String, Object> ubicacion = (Map<String, Object>) document.get("ubicacion");
                        if (ubicacion != null && ubicacion.get("direccion") != null) {
                            tvUbicacionProyecto.setText(ubicacion.get("direccion").toString());
                        }

                        List<String> imagenesUrls = (List<String>) document.get("imagenesUrls");
                        if (imagenesUrls != null && !imagenesUrls.isEmpty() && !imagenesUrls.get(0).isEmpty()) {
                            imagenProyectoUrl = imagenesUrls.get(0);
                            Glide.with(this).load(imagenProyectoUrl).placeholder(R.drawable.onboarding1).into(imgHeroProyecto);
                        }

                        List<String> areasComunes = (List<String>) document.get("areasComunes");
                        chipGroupProyectoExtras.removeAllViews();
                        if (areasComunes != null) {
                            for (String area : areasComunes) agregarChip(chipGroupProyectoExtras, area.substring(0, 1).toUpperCase() + area.substring(1));
                        }

                        List<Map<String, Object>> tipologiasDB = (List<Map<String, Object>>) document.get("tipologias");
                        List<Tipologia> listaTipologias = new ArrayList<>();

                        if (tipologiasDB != null) {
                            for (int i = 0; i < tipologiasDB.size(); i++) {
                                Map<String, Object> mapaTipo = tipologiasDB.get(i);
                                String metraje = mapaTipo.get("metraje") != null ? mapaTipo.get("metraje").toString() + " m²" : "N/A";
                                String cuartos = mapaTipo.get("numeroCuartos") != null ? mapaTipo.get("numeroCuartos").toString() : "0";
                                String precio = mapaTipo.get("precio") != null ? mapaTipo.get("precio").toString() : "0";
                                String nombreTipo = "Departamento Tipo " + (i + 1);

                                Tipologia tipo = new Tipologia(String.valueOf(i), nombreTipo,
                                        "Moderno", metraje, cuartos,
                                        "2", "Sí", precio,
                                        "Disponible", 0,
                                        null, false, "A+",
                                        false, true, false,
                                        true, Integer.parseInt(cuartos),
                                        "Laminado", false,
                                        "Natural", false,
                                        "Premium");
                                listaTipologias.add(tipo);
                            }
                        }

                        SimpleTipologiaAdapter adapter = new SimpleTipologiaAdapter(listaTipologias, this::actualizarFichaYPrecio);
                        rvTipologias.setAdapter(adapter);

                        if (!listaTipologias.isEmpty()) actualizarFichaYPrecio(listaTipologias.get(0));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error cargando proyecto", Toast.LENGTH_SHORT).show());
    }

    private void actualizarFichaYPrecio(Tipologia tp) {
        tipologiaSeleccionadaActual = tp.getNombre();

        tvTipologiaNombre.setText(tp.getNombre());
        tvTipologiaCertificado.setText(tp.getCertificadoEnergetico());
        tvTipologiaTipoPiso.setText(tp.getTipoPiso());
        tvTipologiaVentilacion.setText(tp.getVentilacion());
        tvTipologiaAcabados.setText(tp.getTipoAcabados());
        tvAreaProyecto.setText(tp.getArea());
        tvDormitoriosProyecto.setText(tp.getDormitorios());

        chipGroupTipologiaFeatures.removeAllViews();
        if(tp.isBalcon()) agregarChip(chipGroupTipologiaFeatures, "Balcón");
        if(tp.isCocinaIntegrada()) agregarChip(chipGroupTipologiaFeatures, "Cocina Integrada");

        double precioNumerico = 0.0;
        try {
            String precioLimpio = tp.getPrecio().replaceAll("[^\\d.]", "");
            precioNumerico = Double.parseDouble(precioLimpio);
        } catch (Exception ignored) {}

        montoSeparacionActual = precioNumerico / 4;

        tvPrecioEstimadoProyecto.setText(String.format("S/ %,.2f", precioNumerico));
        tvPrecioProyecto.setText(String.format("Desde S/ %,.2f", precioNumerico));
        tvPriceMain.setText(String.format("Desde S/ %,.2f", precioNumerico));
        tvPriceSub.setText(String.format("Separación: S/ %,.2f", montoSeparacionActual));
    }

    private void agregarChip(ChipGroup chipGroup, String texto) {
        Chip chip = new Chip(this);
        chip.setText(texto);
        chip.setChipBackgroundColorResource(R.color.inmia_teal_light);
        chip.setTextColor(getResources().getColor(R.color.inmia_teal_dark));
        chipGroup.addView(chip);
    }

    private void configurarListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnCompartirQR.setOnClickListener(this::showPopupMenu);

        if (btnAgendarVisita != null) {
            btnAgendarVisita.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteRegistrarCitaActivity.class);
                intent.putExtra("PROYECTO_ID", proyectoId);
                intent.putExtra("PROYECTO_NOMBRE", proyectoNombre);
                intent.putExtra("TIPOLOGIA_NOMBRE", tipologiaSeleccionadaActual);
                startActivity(intent);
            });
        }

        btnReservar.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
                return;
            }

            if (montoSeparacionActual <= 0) {
                Toast.makeText(this, "Cargando precios...", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Solicitando separación...", Toast.LENGTH_SHORT).show();

            Map<String, Object> nuevaSeparacion = new HashMap<>();
            nuevaSeparacion.put("clienteId", currentUser.getUid());
            nuevaSeparacion.put("proyectoId", proyectoId);
            nuevaSeparacion.put("nombreProyecto", proyectoNombre);
            nuevaSeparacion.put("ubicacion", tvUbicacionProyecto.getText().toString());
            nuevaSeparacion.put("inmobiliariaNombre", tvInmobiliariaProyecto.getText().toString());
            nuevaSeparacion.put("tipologia", tipologiaSeleccionadaActual);
            nuevaSeparacion.put("montoSeparacion", montoSeparacionActual);
            nuevaSeparacion.put("imagenUrl", imagenProyectoUrl);
            nuevaSeparacion.put("estado", "En proceso");
            nuevaSeparacion.put("fechaCreacion", com.google.firebase.firestore.FieldValue.serverTimestamp());

            db.collection("separaciones").add(nuevaSeparacion)
                    .addOnSuccessListener(documentReference -> {
                        LogHelper.registrar(
                                "Se solicitó la separación de una unidad en "
                                        + (proyectoNombre != null ? proyectoNombre : "un proyecto"),
                                Log.TIPO_SEPARACION, LogHelper.ROL_CLIENTE);

                        Toast.makeText(this, "¡Solicitud enviada a validación!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, ClienteSeparacionesActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al solicitar", Toast.LENGTH_SHORT).show());
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

    private void configurarMapa() {
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this));
        MapView mapa = findViewById(R.id.mapaClienteProyecto);
        mapa.setMultiTouchControls(true);
        IMapController mapController = mapa.getController();
        mapController.setZoom(18.0);
        mapController.setCenter(new GeoPoint(-12.0975, -77.0366));
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenu().add("Compartir Proyecto");
        popup.getMenu().add("Descargar Brochure");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().toString().equals("Compartir Proyecto")) {
                mostrarDialogoQR(proyectoNombre);
                return true;

            } else if (item.getTitle().toString().equals("Descargar Brochure")) {
                String url = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750";
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setTitle("Brochure " + proyectoNombre);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, proyectoNombre.replace(" ", "_") + ".jpg");
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                if (manager != null) manager.enqueue(request);
                Toast.makeText(this, "Descarga iniciada", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void mostrarDialogoQR(String nombreProyecto) {
        if (nombreProyecto == null || nombreProyecto.isEmpty()) {
            Toast.makeText(this, "Cargando datos del proyecto...", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            com.journeyapps.barcodescanner.BarcodeEncoder barcodeEncoder = new com.journeyapps.barcodescanner.BarcodeEncoder();
            android.graphics.Bitmap bitmap = barcodeEncoder.encodeBitmap(nombreProyecto, com.google.zxing.BarcodeFormat.QR_CODE, 600, 600);

            ImageView imgQR = new ImageView(this);
            imgQR.setImageBitmap(bitmap);
            imgQR.setPadding(50, 50, 50, 50);

            new android.app.AlertDialog.Builder(this)
                    .setTitle("Compartir Proyecto")
                    .setMessage("Muestra este código para que otro usuario vaya directo a " + nombreProyecto)
                    .setView(imgQR)
                    .setNeutralButton("Descargar QR", (dialog, which) -> descargarImagenQR(bitmap, nombreProyecto))
                    .setPositiveButton("Cerrar", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(this, "Error al generar el QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void descargarImagenQR(android.graphics.Bitmap bitmap, String nombreProyecto) {
        try {
            String nombreArchivo = "QR_" + nombreProyecto.replace(" ", "_") + "_" + System.currentTimeMillis() + ".jpg";
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo);
            values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                values.put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/INMIA");
                values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 1);
            }

            android.net.Uri uri = getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                java.io.OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    // Le avisamos a Android que ya terminamos para que lo muestre
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        values.clear();
                        values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0);
                        getContentResolver().update(uri, values, null, null);
                    }

                    Toast.makeText(this, "¡QR guardado! Revisa la carpeta Pictures/INMIA", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminFirestoreGateway.AdminContext;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.example.inmia.util.LogHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminProyectoEditarActivity extends AppCompatActivity {

    public static final String EXTRA_PROYECTO_ID = "extra_proyecto_id";

    private BottomNavigationView bottomNav;
    private AdminFirestoreGateway gateway;
    private String companyId;
    private String companyName;
    private String proyectoId;

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
        btnAgregarTipologia = findViewById(R.id.btnAgregarTipologia);
        btnActualizarProyecto = findViewById(R.id.btnActualizarProyecto);
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
                    tipologiaEditandoIndex = position;
                    btnAgregarTipologia.setText("Actualizar tipología");
                    populateFormWithTipologia(tipologia);
                }
        );
        rvTipologiasAgregadas.setAdapter(tipologiasAdapter);

        configurarSpinners();
        resolverContextoAdmin();
        cargarProyectoExistente();

        btnBack.setOnClickListener(v -> finish());

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

        gateway.observeProjectById(proyectoId, new AdminFirestoreGateway.FirestoreCallback<Proyecto>() {
            @Override
            public void onSuccess(Proyecto proyecto) {
                proyectoOriginal = proyecto;
                runOnUiThread(() -> {
                    etTitulo.setText(proyecto.getNombre());
                    etUbicacion.setText(proyecto.getUbicacion());
                    etDescripcion.setText(proyecto.getDescripcion());

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
        proyecto.setUbicacion(ubicacion.isEmpty() ? "Sin ubicación" : ubicacion);
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
        }

        Log.d("AdminEditar", "tipologiasAgregadas tiene " + tipologiasAgregadas.size() + " tipologias");
        for (int i = 0; i < tipologiasAgregadas.size(); i++) {
            Tipologia t = tipologiasAgregadas.get(i);
            Log.d("AdminEditar", "Tipologia " + i + ": nombre=" + t.getNombre() + ", area=" + t.getArea() + ", precio=" + t.getPrecio());
        }
        Log.d("AdminEditar", "proyectoId a actualizar: " + proyectoId);

        gateway.updateProject(proyectoId, proyecto, companyId, new AdminFirestoreGateway.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AdminEditar", "Proyecto actualizado exitosamente en Firebase");
                LogHelper.registrar(
                        "Se editó el proyecto " + titulo,
                        com.example.inmia.models.Log.TIPO_PROYECTO,
                        LogHelper.ROL_ADMIN);
                runOnUiThread(() -> {
                    Toast.makeText(AdminProyectoEditarActivity.this, "Proyecto actualizado", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("AdminEditar", "Error actualizando proyecto: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(AdminProyectoEditarActivity.this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
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
}
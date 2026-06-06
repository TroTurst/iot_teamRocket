package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.admin.data.AdminRepository;
import com.example.inmia.admin.data.AdminRepositoryProvider;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.example.inmia.models.Asesor;
import com.example.inmia.models.CitaAsesor;
import com.example.inmia.models.Proyecto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminAsesorDetalleCarlosActivity extends AppCompatActivity {

    private AdminRepository repository;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_asesor_detalle_carlos);

        repository = AdminRepositoryProvider.get();
        companyId = repository.getCompanyIdForEmail(AdminSessionDefaults.DEFAULT_ADMIN_EMAIL);

        View btnBack = findViewById(R.id.btnBackDetalleCarlos);
        View btnDetalleCita = findViewById(R.id.btnDetalleCitaCarlos);
        View btnGestionProyectos = findViewById(R.id.btnGestionProyectosCarlos);
        View btnAsignarMetas = findViewById(R.id.btnAsignarMetasCarlos);
        View layoutProyectosActivos = findViewById(R.id.layoutProyectosActivosCarlos);
        View layoutCitas = findViewById(R.id.layoutCitasCarlos);
        NestedScrollView scrollView = findViewById(R.id.scrollViewAsesorDetalleCarlos);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavAdmin);

        Asesor asesor = cargarAsesor();
        if (asesor == null) {
            Toast.makeText(this, "No se encontró asesor", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        poblarPerfil(asesor);

        RecyclerView recyclerViewProyectos = findViewById(R.id.recyclerViewProyectosCarlos);
        recyclerViewProyectos.setLayoutManager(new LinearLayoutManager(this));
        List<Proyecto> proyectos = filtrarProyectosPorAsesor(asesor.getNombre());
        AdminProyectoAdapter adapter = new AdminProyectoAdapter(this, proyectos);
        recyclerViewProyectos.setAdapter(adapter);

        RecyclerView recyclerViewCitas = findViewById(R.id.recyclerViewCitasCarlos);
        recyclerViewCitas.setLayoutManager(new LinearLayoutManager(this));
        List<CitaAsesor> citas = cargarCitasMock();
        AdminCitasAdapter citasAdapter = new AdminCitasAdapter(citas);
        recyclerViewCitas.setAdapter(citasAdapter);

        bottomNav.setSelectedItemId(R.id.nav_asesores);

        btnBack.setOnClickListener(v -> finish());

        btnDetalleCita.setOnClickListener(v -> {
            layoutCitas.setVisibility(layoutCitas.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            if (layoutCitas.getVisibility() == View.VISIBLE) {
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
        });

        btnGestionProyectos.setOnClickListener(v -> {
            layoutProyectosActivos.setVisibility(layoutProyectosActivos.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            if (layoutProyectosActivos.getVisibility() == View.VISIBLE) {
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
        });

        btnAsignarMetas.setOnClickListener(v -> mostrarDialogoMetas(asesor));

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

    private Asesor cargarAsesor() {
        String asesorId = getIntent().getStringExtra("asesor_id");
        Asesor asesor = repository.getAdvisorById(companyId, asesorId);
        if (asesor == null) {
            List<Asesor> asesores = repository.getAdvisors(companyId);
            asesor = !asesores.isEmpty() ? asesores.get(0) : null;
        }
        return asesor;
    }

    private void poblarPerfil(Asesor asesor) {
        TextView tvNombreHeader = findViewById(R.id.tvNombreHeaderCarlos);
        TextView tvNombre = findViewById(R.id.tvNombreCarlos);
        TextView tvRol = findViewById(R.id.tvRolCarlos);
        TextView tvEmail = findViewById(R.id.tvEmailCarlos);
        TextView tvTelefono = findViewById(R.id.tvTelefonoCarlos);
        TextView tvDni = findViewById(R.id.tvDniCarlos);
        TextView tvEstado = findViewById(R.id.tvEstadoCarlos);
        TextView tvZonaTrabajo = findViewById(R.id.tvZonaTrabajoCarlos);
        TextView tvCitas = findViewById(R.id.tvCitasCarlos);
        TextView tvVentas = findViewById(R.id.tvVentasCarlos);
        TextView tvMetaVentas = findViewById(R.id.tvMetaVentasCarlos);
        TextView tvMetaCitas = findViewById(R.id.tvMetaCitasCarlos);
        TextView tvMetaGanancias = findViewById(R.id.tvMetaGananciasCarlos);
        ImageView imgFoto = findViewById(R.id.imgFotoCarlos);

        tvNombreHeader.setText(asesor.getNombre());
        tvNombre.setText(asesor.getNombre());
        tvRol.setText(asesor.getRol());
        tvEmail.setText(asesor.getEmail());
        tvTelefono.setText(asesor.getTelefono());
        tvDni.setText(asesor.getDni());
        tvEstado.setText(asesor.getEstado());
        tvZonaTrabajo.setText(asesor.getZonaTrabajo());
        tvCitas.setText(String.valueOf(asesor.getCitasMensualActual()));
        tvVentas.setText(String.valueOf(asesor.getVentasMensualActual()));
        tvMetaVentas.setText(String.valueOf(asesor.getMetaVentasMensual()));
        tvMetaCitas.setText(String.valueOf(asesor.getMetaCitasMensual()));
        tvMetaGanancias.setText(formatearSoles(asesor.getMetaGananciasMensual()));

        if (asesor.getFotoResId() != 0) {
            imgFoto.setImageResource(asesor.getFotoResId());
        }
    }

    private List<Proyecto> filtrarProyectosPorAsesor(String nombreAsesor) {
        List<Proyecto> filtrados = new ArrayList<>();
        for (Proyecto proyecto : repository.getProjects(companyId)) {
            if (proyecto.getVendedores() != null && proyecto.getVendedores().contains(nombreAsesor)) {
                filtrados.add(proyecto);
            }
        }
        return filtrados;
    }

    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void mostrarDialogoMetas(Asesor asesor) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_asignar_metas, null);
        TextInputEditText etMetaVentas = contentView.findViewById(R.id.etMetaVentas);
        TextInputEditText etMetaCitas = contentView.findViewById(R.id.etMetaCitas);
        TextInputEditText etMetaGanancias = contentView.findViewById(R.id.etMetaGanancias);

        etMetaVentas.setText(String.valueOf(asesor.getMetaVentasMensual()));
        etMetaCitas.setText(String.valueOf(asesor.getMetaCitasMensual()));
        etMetaGanancias.setText(String.valueOf(asesor.getMetaGananciasMensual()));

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.admin_metas_titulo)
                .setView(contentView)
                .setPositiveButton(R.string.admin_metas_guardar, (dialog, which) -> {
                    int metaVentas = parseIntSafe(etMetaVentas.getText());
                    int metaCitas = parseIntSafe(etMetaCitas.getText());
                    int metaGanancias = parseIntSafe(etMetaGanancias.getText());

                    asesor.setMetaVentasMensual(metaVentas);
                    asesor.setMetaCitasMensual(metaCitas);
                    asesor.setMetaGananciasMensual(metaGanancias);

                    poblarPerfil(asesor);
                })
                .setNegativeButton(R.string.admin_metas_cancelar, null)
                .show();
    }

    private int parseIntSafe(CharSequence value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String formatearSoles(int valor) {
        return "S/ " + String.format("%,d", valor);
    }

    private List<CitaAsesor> cargarCitasMock() {
        List<CitaAsesor> citas = new ArrayList<>();

        citas.add(new CitaAsesor("Mariana Torres", "12/05/2026", "10:00", "Palm Living", "Confirmada"));
        citas.add(new CitaAsesor("Alberto Rios", "14/05/2026", "12:30", "Vista Verde", "En proceso"));
        citas.add(new CitaAsesor("Paula Reyes", "16/05/2026", "09:15", "Mirador Sur", "Completada"));
        citas.add(new CitaAsesor("Diego Salazar", "18/05/2026", "16:00", "Skyline Tower", "Cancelada"));
        citas.add(new CitaAsesor("Lucia Paredes", "20/05/2026", "11:45", "Costa Azul", "Confirmada"));

        return citas;
    }
}

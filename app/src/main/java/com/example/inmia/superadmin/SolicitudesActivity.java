package com.example.inmia.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Solicitud;
import com.example.inmia.superadmin.db.AppDatabase;
import com.example.inmia.superadmin.db.SolicitudEntity;
import com.example.inmia.superadmin.NotificacionHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesActivity extends AppCompatActivity
        implements SolicitudAdapter.OnSolicitudListener {

    private RecyclerView recyclerSolicitudes;
    private SolicitudAdapter adapter;
    private List<Solicitud> listaSolicitudes;
    private TextView tvContador;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.sa_activity_solicitudes);

        // Vincular vistas
        recyclerSolicitudes = findViewById(R.id.recyclerSolicitudes);
        tvContador          = findViewById(R.id.tvContador);
        bottomNav           = findViewById(R.id.bottomNavSuperAdmin);

        // Botón atrás
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Inicializar datos
        inicializarDatos();

        // Configurar RecyclerView
        recyclerSolicitudes.setLayoutManager(
                new LinearLayoutManager(this));
        adapter = new SolicitudAdapter(this, listaSolicitudes, this);
        recyclerSolicitudes.setAdapter(adapter);

        // Actualizar contador
        actualizarContador();

        // Bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_usuarios);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                finish();
                return true;
            } else if (id == R.id.nav_usuarios) {
                irAGestionUsuarios();
                return true;
            } else if (id == R.id.nav_reportes) {
                startActivity(new Intent(this, ReportesActivity.class));
                return true;
            } else if (id == R.id.nav_logs) {
                startActivity(new Intent(this, LogsActivity.class));
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }
            return false;
        });
    }

    // ── Datos hardcodeados ───────────────────────────────────────────────────

    private void inicializarDatos() {
        listaSolicitudes = new ArrayList<>();

        // Cargar solicitudes reales desde Room (creadas por el admin de inmobiliaria)
        List<SolicitudEntity> desdeRoom =
                AppDatabase.getInstance(this).solicitudDao().obtenerPendientes();
        for (SolicitudEntity e : desdeRoom) {
            String iniciales = obtenerIniciales(e.nombre, e.apellidos);
            listaSolicitudes.add(new Solicitud(
                    e.id,
                    e.nombre + " " + e.apellidos,
                    e.inmobiliaria,
                    iniciales,
                    e.correo,
                    e.telefono,
                    "Recién enviado",
                    e.documento,
                    e.fechaNac,
                    e.domicilio
            ));
        }

        // Si no hay solicitudes reales, mostrar datos de demo
        if (listaSolicitudes.isEmpty()) {
            listaSolicitudes.add(new Solicitud(
                    "María García López", "INMIA San Isidro", "MG",
                    "m.garcia@inmia.com", "+51 987 654 321", "Hace 2 horas",
                    "DNI · 45678901", "15/03/1995", "Av. Javier Prado 1234, San Isidro"));
            listaSolicitudes.add(new Solicitud(
                    "Carlos Ramos Torres", "INMIA Miraflores", "CR",
                    "c.ramos@inmia.com", "+51 912 345 678", "Hace 5 horas",
                    "DNI · 32156789", "22/07/1990", "Calle Las Flores 567, Miraflores"));
            listaSolicitudes.add(new Solicitud(
                    "Juan Sánchez Pérez", "INMIA Surco", "JS",
                    "j.sanchez@inmia.com", "+51 956 789 012", "Ayer 11:30 pm",
                    "DNI · 78234561", "08/11/1988", "Jr. Los Pinos 890, Surco"));
        }
    }

    private String obtenerIniciales(String nombre, String apellidos) {
        String n = (nombre != null && !nombre.isEmpty()) ? String.valueOf(nombre.charAt(0)) : "";
        String a = (apellidos != null && !apellidos.isEmpty()) ? String.valueOf(apellidos.charAt(0)) : "";
        return (n + a).toUpperCase();
    }

    // ── Callbacks del adapter ────────────────────────────────────────────────

    @Override
    public void onHabilitar(Solicitud solicitud, int position) {
        if (solicitud.getRoomId() > 0) {
            AppDatabase.getInstance(this)
                    .solicitudDao().marcarProcesada(solicitud.getRoomId());
        }
        adapter.eliminarItem(position);
        actualizarContador();

        NotificacionHelper.enviar(
                this,
                "Asesor habilitado",
                solicitud.getNombre() + " ha sido habilitado como asesor de ventas.",
                NotificacionHelper.TIPO_ASESOR_HABILITADO
        );

        Toast.makeText(this,
                solicitud.getNombre() + " ha sido habilitado como asesor",
                Toast.LENGTH_SHORT).show();

        if (listaSolicitudes.isEmpty()) irAGestionUsuarios();
    }

    @Override
    public void onRechazar(Solicitud solicitud, int position) {
        if (solicitud.getRoomId() > 0) {
            AppDatabase.getInstance(this)
                    .solicitudDao().marcarProcesada(solicitud.getRoomId());
        }
        adapter.eliminarItem(position);
        actualizarContador();

        NotificacionHelper.enviar(
                this,
                "Solicitud rechazada",
                "La solicitud de " + solicitud.getNombre() + " ha sido rechazada.",
                NotificacionHelper.TIPO_ASESOR_RECHAZADO
        );

        Toast.makeText(this,
                "Solicitud de " + solicitud.getNombre() + " rechazada",
                Toast.LENGTH_SHORT).show();

        if (listaSolicitudes.isEmpty()) irAGestionUsuarios();
    }

    @Override
    public void onVerPerfil(Solicitud solicitud) {
        Intent intent = new Intent(this, PerfilAsesorActivity.class);
        intent.putExtra(PerfilAsesorActivity.EXTRA_NOMBRE,
                solicitud.getNombre());
        intent.putExtra(PerfilAsesorActivity.EXTRA_INMOBILIARIA,
                solicitud.getInmobiliaria());
        intent.putExtra(PerfilAsesorActivity.EXTRA_DOCUMENTO,
                solicitud.getDocumento());
        intent.putExtra(PerfilAsesorActivity.EXTRA_FECHA_NAC,
                solicitud.getFechaNac());
        intent.putExtra(PerfilAsesorActivity.EXTRA_CORREO,
                solicitud.getCorreo());
        intent.putExtra(PerfilAsesorActivity.EXTRA_TELEFONO,
                solicitud.getTelefono());
        intent.putExtra(PerfilAsesorActivity.EXTRA_DOMICILIO,
                solicitud.getDomicilio());
        startActivity(intent);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void actualizarContador() {
        int total = listaSolicitudes.size();
        tvContador.setText(total + (total == 1
                ? " solicitud pendiente"
                : " solicitudes pendientes"));
    }

    private void irAGestionUsuarios() {
        Intent intent = new Intent(this, GestionUsuariosActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
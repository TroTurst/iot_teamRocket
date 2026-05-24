package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.example.inmia.superadmin.NotificacionHelper;
import com.example.inmia.superadmin.db.AppDatabase;
import com.example.inmia.superadmin.db.SolicitudEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AdminAsesorNuevoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_asesor_nuevo);

        View btnBack = findViewById(R.id.btnBackNuevoAsesor);
        MaterialButton btnCrear = findViewById(R.id.btnCrearAsesor);

        TextInputEditText etNombre = findViewById(R.id.etNombreAsesor);
        TextInputEditText etApellido = findViewById(R.id.etApellidoAsesor);
        TextInputEditText etDni = findViewById(R.id.etDniAsesor);
        TextInputEditText etEmail = findViewById(R.id.etEmailAsesor);
        TextInputEditText etTelefono = findViewById(R.id.etTelefonoAsesor);
        TextInputEditText etZona = findViewById(R.id.etZonaAsesor);
        TextInputEditText etEspecialidad = findViewById(R.id.etEspecialidadAsesor);
        TextInputEditText etEstado = findViewById(R.id.etEstadoAsesor);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavAdmin);
        bottomNav.setSelectedItemId(R.id.nav_asesores);

        btnBack.setOnClickListener(v -> finish());
        btnCrear.setOnClickListener(v -> {
            String nombre    = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
            String apellido  = etApellido.getText() != null ? etApellido.getText().toString().trim() : "";
            String dni       = etDni.getText() != null ? etDni.getText().toString().trim() : "";
            String email     = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String telefono  = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
            String zona      = etZona.getText() != null ? etZona.getText().toString().trim() : "";

            // Guardar solicitud pendiente en Room
            SolicitudEntity solicitud = new SolicitudEntity();
            solicitud.nombre       = nombre;
            solicitud.apellidos    = apellido;
            solicitud.inmobiliaria = zona;
            solicitud.correo       = email;
            solicitud.telefono     = telefono;
            solicitud.documento    = "DNI · " + dni;
            solicitud.fechaNac     = "";
            solicitud.domicilio    = "";
            solicitud.timestamp    = System.currentTimeMillis();
            solicitud.pendiente    = true;
            AppDatabase.getInstance(this).solicitudDao().insertar(solicitud);

            // Notificar al superadmin
            NotificacionHelper.enviar(
                    this,
                    "Nueva solicitud de asesor",
                    nombre + " " + apellido + " solicita ser habilitado como asesor.",
                    NotificacionHelper.TIPO_NUEVA_SOLICITUD_ASESOR
            );

            Toast.makeText(this,
                    "Solicitud enviada. Esperando aprobación del superadmin.",
                    Toast.LENGTH_LONG).show();
            finish();
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                navegarATab(AdminHomeActivity.class);
                return true;
            } else if (id == R.id.nav_proyectos) {
                navegarATab(AdminProyectosActivity.class);
                return true;
            } else if (id == R.id.nav_asesores) {
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

    private void navegarATab(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}



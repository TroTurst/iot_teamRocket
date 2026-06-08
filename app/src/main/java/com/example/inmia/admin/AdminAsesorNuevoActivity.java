package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
import com.example.inmia.admin.data.AdminFirestoreGateway;
import com.example.inmia.admin.data.AdminSessionDefaults;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AdminAsesorNuevoActivity extends AppCompatActivity {

    private AdminFirestoreGateway gateway;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_asesor_nuevo);

        gateway = new AdminFirestoreGateway();

        View btnBack = findViewById(R.id.btnBackNuevoAsesor);
        MaterialButton btnCrear = findViewById(R.id.btnCrearAsesor);

        TextInputEditText etNombre = findViewById(R.id.etNombreAsesor);
        TextInputEditText etApellido = findViewById(R.id.etApellidoAsesor);
        TextInputEditText etDni = findViewById(R.id.etDniAsesor);
        TextInputEditText etEmail = findViewById(R.id.etEmailAsesor);
        TextInputEditText etTelefono = findViewById(R.id.etTelefonoAsesor);
        TextInputEditText etZona = findViewById(R.id.etZonaAsesor);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavAdmin);
        bottomNav.setSelectedItemId(R.id.nav_asesores);

        btnBack.setOnClickListener(v -> finish());

        gateway.resolveAdminContextByEmail(AdminSessionDefaults.DEFAULT_ADMIN_EMAIL,
                new AdminFirestoreGateway.FirestoreCallback<AdminFirestoreGateway.AdminContext>() {
            @Override
            public void onSuccess(AdminFirestoreGateway.AdminContext context) {
                companyId = context.getCompanyId();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminAsesorNuevoActivity.this,
                        "Error al obtener contexto de empresa",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnCrear.setOnClickListener(v -> {
            String nombre   = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
            String apellido = etApellido.getText() != null ? etApellido.getText().toString().trim() : "";
            String dni      = etDni.getText() != null ? etDni.getText().toString().trim() : "";
            String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
            String zona     = etZona.getText() != null ? etZona.getText().toString().trim() : "";

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
                Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (companyId == null || companyId.isEmpty()) {
                Toast.makeText(this, "Error: No se encontró la empresa", Toast.LENGTH_SHORT).show();
                return;
            }

            gateway.saveAsesor(nombre, apellido, email, telefono, dni, zona, companyId,
                    new AdminFirestoreGateway.FirestoreCallback<String>() {
                @Override
                public void onSuccess(String asesorId) {
                    runOnUiThread(() -> {
                        Toast.makeText(AdminAsesorNuevoActivity.this,
                                "Asesor creado exitosamente",
                                Toast.LENGTH_LONG).show();
                        finish();
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AdminAsesorNuevoActivity.this,
                                "Error al crear asesor: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                }
            });
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
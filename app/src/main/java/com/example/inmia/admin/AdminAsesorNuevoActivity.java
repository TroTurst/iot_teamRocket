package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;
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
            Toast.makeText(this, "Asesor creado", Toast.LENGTH_SHORT).show();
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



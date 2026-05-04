package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

public class AdminProyectoGaleriaActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_IMAGES = "extra_images";

    private RecyclerView recyclerView;
    private TextView tvTituloGaleria;
    private TextView tvContador;
    private View btnCerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_proyecto_galeria);

        recyclerView = findViewById(R.id.recyclerViewGaleria);
        tvTituloGaleria = findViewById(R.id.tvTituloGaleria);
        tvContador = findViewById(R.id.tvContadorGaleria);
        btnCerrar = findViewById(R.id.btnCerrarGaleria);

        Intent intent = getIntent();
        String titulo = intent.getStringExtra(EXTRA_TITLE);
        int[] imagenes = intent.getIntArrayExtra(EXTRA_IMAGES);
        if (titulo == null) {
            titulo = "Galería";
        }
        if (imagenes == null || imagenes.length == 0) {
            imagenes = new int[]{R.drawable.onboarding1};
        }

        tvTituloGaleria.setText(titulo);
        tvContador.setText("Total: " + imagenes.length);
        final String tituloFinal = titulo;
        final int[] imagenesFinal = imagenes;

        AdminProyectoGaleriaAdapter adapter = new AdminProyectoGaleriaAdapter(
                imagenesFinal,
                (imageRes, position) -> abrirImagenCompleta(imagenesFinal, position, tituloFinal)
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);

        btnCerrar.setOnClickListener(v -> finish());
    }

    private void abrirImagenCompleta(int[] imagenes, int selectedIndex, String titulo) {
        Intent intent = new Intent(this, AdminProyectoImagenActivity.class);
        intent.putExtra(AdminProyectoImagenActivity.EXTRA_IMAGES, imagenes);
        intent.putExtra(AdminProyectoImagenActivity.EXTRA_SELECTED_INDEX, selectedIndex);
        intent.putExtra(AdminProyectoImagenActivity.EXTRA_TITLE, titulo);
        startActivity(intent);
    }
}

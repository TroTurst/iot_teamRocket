package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.inmia.R;

public class AdminProyectoGaleriaActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_IMAGES = "extra_images";

    private ViewPager2 viewPager;
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

        viewPager = findViewById(R.id.viewPagerGaleria);
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
        tvContador.setText("1/" + imagenes.length);

        AdminProyectoGaleriaAdapter adapter = new AdminProyectoGaleriaAdapter(imagenes);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0, false);

        int finalTotal = imagenes.length;
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvContador.setText((position + 1) + "/" + finalTotal);
            }
        });

        btnCerrar.setOnClickListener(v -> finish());
    }
}


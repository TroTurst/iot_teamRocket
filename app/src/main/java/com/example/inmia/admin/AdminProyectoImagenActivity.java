package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inmia.R;

public class AdminProyectoImagenActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGES = "extra_images";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SELECTED_INDEX = "extra_selected_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_admin_proyecto_imagen);

        View btnCerrar = findViewById(R.id.btnCerrarImagen);
        TextView tvTitulo = findViewById(R.id.tvTituloImagen);
        TextView tvContador = findViewById(R.id.tvContadorImagen);
        androidx.viewpager2.widget.ViewPager2 viewPager = findViewById(R.id.viewPagerImagenes);

        Intent intent = getIntent();
        int[] images = intent.getIntArrayExtra(EXTRA_IMAGES);
        int selectedIndex = intent.getIntExtra(EXTRA_SELECTED_INDEX, 0);
        String titulo = intent.getStringExtra(EXTRA_TITLE);

        if (titulo == null || titulo.trim().isEmpty()) {
            titulo = "Imagen";
        }
        if (images == null || images.length == 0) {
            images = new int[]{R.drawable.onboarding1};
        }
        if (selectedIndex < 0 || selectedIndex >= images.length) {
            selectedIndex = 0;
        }

        tvTitulo.setText(titulo);
        tvContador.setText((selectedIndex + 1) + "/" + images.length);

        AdminProyectoImagenPagerAdapter adapter = new AdminProyectoImagenPagerAdapter(images);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedIndex, false);

        int finalTotal = images.length;
        viewPager.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvContador.setText((position + 1) + "/" + finalTotal);
            }
        });

        btnCerrar.setOnClickListener(v -> finish());
    }
}


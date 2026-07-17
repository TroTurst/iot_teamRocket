package com.example.inmia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.inmia.R;

import java.util.ArrayList;

public class AdminProyectoImagenActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGES = "extra_images";
    public static final String EXTRA_IMAGE_URLS = "extra_image_urls";
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
        ViewPager2 viewPager = findViewById(R.id.viewPagerImagenes);

        Intent intent = getIntent();
        ArrayList<String> imageUrls = intent.getStringArrayListExtra(EXTRA_IMAGE_URLS);
        int selectedIndex = intent.getIntExtra(EXTRA_SELECTED_INDEX, 0);
        String titulo = intent.getStringExtra(EXTRA_TITLE);

        if (titulo == null || titulo.trim().isEmpty()) {
            titulo = "Imagen";
        }
        if (imageUrls == null || imageUrls.isEmpty()) {
            imageUrls = new ArrayList<>();
        }
        if (selectedIndex < 0 || selectedIndex >= imageUrls.size()) {
            selectedIndex = 0;
        }

        tvTitulo.setText(titulo);
        tvContador.setText(imageUrls.isEmpty() ? "0/0" : (selectedIndex + 1) + "/" + imageUrls.size());

        AdminProyectoImagenPagerAdapter adapter = new AdminProyectoImagenPagerAdapter(imageUrls);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedIndex, false);

        int finalTotal = imageUrls.size();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (finalTotal > 0) {
                    tvContador.setText((position + 1) + "/" + finalTotal);
                }
            }
        });

        btnCerrar.setOnClickListener(v -> finish());
    }
}

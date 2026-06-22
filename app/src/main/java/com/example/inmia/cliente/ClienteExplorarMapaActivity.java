package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ClienteExplorarMapaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MaterialCardView cardFloatingProperty;
    private TextView tvTextoUbicacion;
    private TextView tvTextoPrecio;
    private TextView tvTextoHabitaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_explorar_mapa_cliente);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        cardFloatingProperty = findViewById(R.id.cardFloatingProperty);
        cardFloatingProperty.setOnClickListener(v -> {
            Intent intent = new Intent(ClienteExplorarMapaActivity.this, ClienteDetallePropiedadActivity.class);
            intent.putExtra("PROYECTO_NOMBRE", "Palm Living");
            startActivity(intent);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);
        android.widget.FrameLayout btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        bottomNav.setSelectedItemId(R.id.nav_citas);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, ClienteHomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(this, ClienteCitasActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ClienteMensajesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, ClientePerfilClienteActivity.class));
                return true;
            } else if (id == R.id.nav_separaciones) {
                startActivity(new Intent(this, ClienteSeparacionesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        tvTextoUbicacion = findViewById(R.id.tvTextoUbicacion);
        LinearLayout btnFiltroPrecio = findViewById(R.id.btnFiltroPrecio);
        LinearLayout btnFiltroUbicacion = findViewById(R.id.btnFiltroUbicacion);
        LinearLayout btnFiltroHabitaciones = findViewById(R.id.btnFiltroHabitaciones);

        btnFiltroPrecio.setOnClickListener(v -> mostrarBottomSheetPrecio());
        btnFiltroUbicacion.setOnClickListener(v -> mostrarBottomSheetUbicacion());
        btnFiltroHabitaciones.setOnClickListener(v -> mostrarBottomSheetHabitaciones());
        tvTextoUbicacion = findViewById(R.id.tvTextoUbicacion);
        tvTextoPrecio = findViewById(R.id.tvTextoPrecio);
        tvTextoHabitaciones = findViewById(R.id.tvTextoHabitaciones);
        LinearLayout btnFiltroMas = findViewById(R.id.btnFiltroMas);
        btnFiltroMas.setOnClickListener(v -> mostrarBottomSheetMasFiltros());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng palmLiving = new LatLng(-12.0976, -77.0365);
        googleMap.addMarker(new MarkerOptions()
            .position(palmLiving)
            .title("Palm Living")
            .snippet("San Isidro, Lima"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(palmLiving, 15f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMarkerClickListener(marker -> {
            cardFloatingProperty.setVisibility(View.VISIBLE);
            return false;
        });
    }
    private void mostrarBottomSheetMasFiltros() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_mas_filtros, null);
        dialog.setContentView(view);


        dialog.show();
    }
    private void mostrarBottomSheetPrecio() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_precio, null);
        dialog.setContentView(view);

        com.google.android.material.textfield.TextInputEditText etMin = view.findViewById(R.id.etPrecioMin);
        com.google.android.material.textfield.TextInputEditText etMax = view.findViewById(R.id.etPrecioMax);
        Button btnAplicar = view.findViewById(R.id.btnAplicarPrecio);
        TextView tvLimpiar = view.findViewById(R.id.tvLimpiarPrecio);

        btnAplicar.setOnClickListener(v -> {
            String min = etMin.getText() != null ? etMin.getText().toString() : "";
            String max = etMax.getText() != null ? etMax.getText().toString() : "";

            if (!min.isEmpty() || !max.isEmpty()) {
                String textoFiltro = "S/ " + (min.isEmpty() ? "0" : min) + " - " + (max.isEmpty() ? "Max" : max);
                tvTextoPrecio.setText(textoFiltro);
                tvTextoPrecio.setTextColor(android.graphics.Color.parseColor("#087A82"));
            }
            dialog.dismiss();
        });

        tvLimpiar.setOnClickListener(v -> {
            etMin.setText("");
            etMax.setText("");
            tvTextoPrecio.setText("Precio");
            tvTextoPrecio.setTextColor(android.graphics.Color.parseColor("#222222"));
            dialog.dismiss();
        });

        dialog.show();
    }

    private void mostrarBottomSheetUbicacion() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_ubicacion, null);
        dialog.setContentView(view);

        ChipGroup chipGroup = view.findViewById(R.id.chipGroupUbicacion);
        Button btnAplicar = view.findViewById(R.id.btnAplicarUbicacion);
        TextView tvLimpiar = view.findViewById(R.id.tvLimpiarUbicacion);

        btnAplicar.setOnClickListener(v -> {
            int chipSeleccionadoId = chipGroup.getCheckedChipId();

            if (chipSeleccionadoId != View.NO_ID) {
                Chip chip = view.findViewById(chipSeleccionadoId);
                String distritoElegido = chip.getText().toString();
                tvTextoUbicacion.setText(distritoElegido);
                tvTextoUbicacion.setTextColor(android.graphics.Color.parseColor("#087A82"));
            }
            dialog.dismiss();
        });

        tvLimpiar.setOnClickListener(v -> {
            chipGroup.clearCheck();
            tvTextoUbicacion.setText("Ubic.");
            tvTextoUbicacion.setTextColor(android.graphics.Color.parseColor("#222222"));
            dialog.dismiss();
        });

        dialog.show();
    }

    private void mostrarBottomSheetHabitaciones() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_habitaciones, null);
        dialog.setContentView(view);

        com.google.android.material.chip.ChipGroup chipGroup = view.findViewById(R.id.chipGroupHabitaciones);
        Button btnAplicar = view.findViewById(R.id.btnAplicarHabitaciones);
        TextView tvLimpiar = view.findViewById(R.id.tvLimpiarHabitaciones);

        btnAplicar.setOnClickListener(v -> {
            int chipSeleccionadoId = chipGroup.getCheckedChipId();

            if (chipSeleccionadoId != View.NO_ID) {
                com.google.android.material.chip.Chip chip = view.findViewById(chipSeleccionadoId);
                String habElegida = chip.getText().toString();
                tvTextoHabitaciones.setText(habElegida + " Hab.");
                tvTextoHabitaciones.setTextColor(android.graphics.Color.parseColor("#087A82"));
            }
            dialog.dismiss();
        });

        tvLimpiar.setOnClickListener(v -> {
            chipGroup.clearCheck();
            tvTextoHabitaciones.setText("Habit.");
            tvTextoHabitaciones.setTextColor(android.graphics.Color.parseColor("#222222"));
            dialog.dismiss();
        });

        dialog.show();
    }
}

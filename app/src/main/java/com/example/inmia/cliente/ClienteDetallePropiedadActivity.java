package com.example.inmia.cliente;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inmia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClienteDetallePropiedadActivity extends AppCompatActivity {
    private ImageView imgHeroProyecto;
    private TextView btn45, btn65, btn90;
    private TextView tvArea, tvDormitorios, tvBanos, tvPrecio, tvPrecioEstimado;

    private com.google.android.material.button.MaterialButton btnReservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_detalle_propiedad_cliente);
        android.widget.FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        android.widget.FrameLayout btnCompartirQR = findViewById(R.id.btnCompartirQR);
        if (btnCompartirQR != null) {
            btnCompartirQR.setOnClickListener(v -> mostrarDialogoQR());
        }

        inicializarVistas();
        configurarTipologias();
        if (btnReservar != null) {
            btnReservar.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteRegistrarCitaActivity.class);
                startActivity(intent);
            });
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavCliente);

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

        LinearLayout btnVerResenas = findViewById(R.id.btnVerResenas);
        if (btnVerResenas != null) {
            btnVerResenas.setOnClickListener(v -> {
                Intent intent = new Intent(this, ClienteReviewsActivity.class);
                startActivity(intent);
            });
        }


    }
    private void mostrarDialogoQR() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ialog_qr_compartir);

        if(dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnGuardarQr = dialog.findViewById(R.id.btnGuardarQr);
        btnGuardarQr.setOnClickListener(v -> {
            Toast.makeText(this, "QR Guardado exitosamente", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void inicializarVistas() {
        imgHeroProyecto = findViewById(R.id.imgHeroProyecto);

        btn45 = findViewById(R.id.btnTipologia45);
        btn65 = findViewById(R.id.btnTipologia65);
        btn90 = findViewById(R.id.btnTipologia90);

        tvArea = findViewById(R.id.tvAreaProyecto);
        tvDormitorios = findViewById(R.id.tvDormitoriosProyecto);
        tvBanos = findViewById(R.id.tvBanosProyecto);

        tvPrecio = findViewById(R.id.tvPrecioProyecto);
        tvPrecioEstimado = findViewById(R.id.tvPrecioEstimadoProyecto);
        btnReservar = findViewById(R.id.btnReservar);
    }
    private void configurarTipologias() {
        if (btn45 != null) btn45.setOnClickListener(v -> actualizarVista(1));
        if (btn65 != null) btn65.setOnClickListener(v -> actualizarVista(2));
        if (btn90 != null) btn90.setOnClickListener(v -> actualizarVista(3));
    }
    private void actualizarVista(int opcion) {
        resetearBotones();

        switch (opcion) {
            case 1:
                marcarSeleccionado(btn45);
                imgHeroProyecto.setImageResource(R.drawable.onboarding1);
                tvArea.setText("45 m²");
                tvDormitorios.setText("1");
                tvBanos.setText("1");
                tvPrecio.setText("S/ 320,000");
                tvPrecioEstimado.setText("S/ 320,000");
                break;
            case 2:
                marcarSeleccionado(btn65);
                imgHeroProyecto.setImageResource(R.drawable.onboarding2);
                tvArea.setText("65 m²");
                tvDormitorios.setText("2");
                tvBanos.setText("2");
                tvPrecio.setText("S/ 450,000");
                tvPrecioEstimado.setText("S/ 450,000");
                break;
            case 3:
                marcarSeleccionado(btn90);
                imgHeroProyecto.setImageResource(R.drawable.onboarding3);
                tvArea.setText("90 m²");
                tvDormitorios.setText("3");
                tvBanos.setText("2");
                tvPrecio.setText("S/ 648,000");
                tvPrecioEstimado.setText("S/ 648,000");
                break;
        }
    }

    private void resetearBotones() {
        int colorTeal = ContextCompat.getColor(this, R.color.inmia_teal_dark);
        TextView[] botones = {btn45, btn65, btn90};

        for (TextView btn : botones) {
            if (btn != null) {
                btn.setBackgroundTintList(null);

                btn.setBackgroundResource(R.drawable.badge_outline);
                btn.setTextColor(colorTeal);
                btn.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    private void marcarSeleccionado(TextView btn) {
        if (btn != null) {
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.inmia_teal_dark));
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btn.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }
}
package com.example.inmia.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ClientePagoSeparacionActivity extends AppCompatActivity {

    private MaterialButton btnPagar;
    private FrameLayout btnBack;
    private ShapeableImageView imgProyectoPago;
    private TextView tvNombreProyectoPago, tvDetallesProyectoPago, tvMontoPagoHeader;

    private LinearLayout formLayout;
    private TextView tvCardNumberVisual, tvCardNameVisual;

    private TextView tvTimerText;
    private ProgressBar pbTimer;
    private CountDownTimer countDownTimer;
    private final long TIEMPO_MAXIMO_MS = 600000;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String separacionId = "";
    private double montoSeparacion = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_pago_separacion_cliente);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        inicializarVistas();
        recibirDatosYPintarUI();
        verificarTarjetaGuardada();
        iniciarTemporizador();

        if (btnPagar != null) btnPagar.setOnClickListener(v -> procesarPagoYActualizarBD());
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void inicializarVistas() {
        btnPagar = findViewById(R.id.btnPagar);
        btnBack = findViewById(R.id.btnBack);

        imgProyectoPago = findViewById(R.id.imgProyectoPago);
        tvNombreProyectoPago = findViewById(R.id.tvNombreProyectoPago);
        tvDetallesProyectoPago = findViewById(R.id.tvDetallesProyectoPago);
        tvMontoPagoHeader = findViewById(R.id.tvMontoPagoHeader);

        formLayout = findViewById(R.id.formLayout);
        tvCardNumberVisual = findViewById(R.id.tvCardNumberVisual);
        tvCardNameVisual = findViewById(R.id.tvCardNameVisual);

        tvTimerText = findViewById(R.id.tvTimerText);
        pbTimer = findViewById(R.id.pbTimer);
    }

    private void recibirDatosYPintarUI() {
        if (getIntent() != null) {
            separacionId = getIntent().getStringExtra("SEPARACION_ID");
            String proyectoNombre = getIntent().getStringExtra("PROYECTO_NOMBRE");
            String tipologia = getIntent().getStringExtra("TIPOLOGIA");
            String imagenUrl = getIntent().getStringExtra("IMAGEN_URL");
            montoSeparacion = getIntent().getDoubleExtra("MONTO_SEPARACION", 0.0);

            if (proyectoNombre != null) tvNombreProyectoPago.setText(proyectoNombre);
            if (tipologia != null) tvDetallesProyectoPago.setText(tipologia);

            String montoFormateado = String.format("S/ %,.2f", montoSeparacion);
            tvMontoPagoHeader.setText(montoFormateado);
            btnPagar.setText("Pagar " + montoFormateado);

            if (imagenUrl != null && !imagenUrl.isEmpty()) {
                Glide.with(this).load(imagenUrl).placeholder(R.drawable.onboarding1).into(imgProyectoPago);
            }
        }
    }

    private void verificarTarjetaGuardada() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("usuarios").document(currentUser.getUid()).collection("tarjetas").limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String nombre = doc.getString("nombreTitular");
                            String ultimos4 = doc.getString("ultimos4Digitos");

                            formLayout.setVisibility(View.GONE);

                            tvCardNameVisual.setText(nombre != null ? nombre.toUpperCase() : "CLIENTE INMIA");
                            tvCardNumberVisual.setText("•••• •••• •••• " + (ultimos4 != null ? ultimos4 : "0000"));

                            Toast.makeText(this, "Usando tarjeta guardada", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        formLayout.setVisibility(View.VISIBLE);
                        tvCardNameVisual.setText("NOMBRE DEL TITULAR");
                        tvCardNumberVisual.setText("•••• •••• •••• ••••");
                    }
                });
    }

    private void iniciarTemporizador() {
        countDownTimer = new CountDownTimer(TIEMPO_MAXIMO_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutos = (int) (millisUntilFinished / 1000) / 60;
                int segundos = (int) (millisUntilFinished / 1000) % 60;
                tvTimerText.setText(String.format("%02d:%02d", minutos, segundos));

                int progreso = (int) ((millisUntilFinished * 100) / TIEMPO_MAXIMO_MS);
                pbTimer.setProgress(progreso);
            }

            @Override
            public void onFinish() {
                tvTimerText.setText("00:00");
                pbTimer.setProgress(0);
                Toast.makeText(ClientePagoSeparacionActivity.this, "El tiempo de pago ha expirado.", Toast.LENGTH_LONG).show();
                finish();
            }
        }.start();
    }

    private void procesarPagoYActualizarBD() {
        if (separacionId == null || separacionId.isEmpty()) return;

        btnPagar.setEnabled(false);
        btnPagar.setText("Procesando pago...");
        if (countDownTimer != null) countDownTimer.cancel();

        db.collection("separaciones").document(separacionId)
                .update("estado", "Pagada")
                .addOnSuccessListener(aVoid -> {

                    Intent intent = new Intent(this, ClientePagoExitosoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    TextView tvProyecto = findViewById(R.id.tvNombreProyectoPago);
                    TextView tvTipologia = findViewById(R.id.tvDetallesProyectoPago);

                    intent.putExtra("PROYECTO_NOMBRE", tvProyecto.getText().toString());
                    intent.putExtra("TIPOLOGIA", tvTipologia.getText().toString());
                    intent.putExtra("MONTO_SEPARACION", montoSeparacion);

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnPagar.setEnabled(true);
                    btnPagar.setText("Reintentar Pago");
                    if (countDownTimer != null) countDownTimer.start();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
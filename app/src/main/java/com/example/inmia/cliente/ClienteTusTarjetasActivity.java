package com.example.inmia.cliente;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Tarjeta;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteTusTarjetasActivity extends AppCompatActivity {

    private RecyclerView rvTarjetas;
    private FloatingActionButton fabAddCard;
    private TarjetaAdapter adapter;
    private List<Tarjeta> listaTarjetas = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_tus_tarjetas_cliente);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }

        inicializarVistas();
        cargarTarjetasDeFirestore();
    }

    private void inicializarVistas() {
        rvTarjetas = findViewById(R.id.rvTarjetas);
        rvTarjetas.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TarjetaAdapter(listaTarjetas, this::marcarComoPredeterminada);
        rvTarjetas.setAdapter(adapter);

        FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        fabAddCard = findViewById(R.id.fabAddCard);
        fabAddCard.setOnClickListener(v -> mostrarDialogoAgregarTarjeta());
    }

    private void cargarTarjetasDeFirestore() {
        if (userId == null) return;

        db.collection("usuarios").document(userId).collection("tarjetas")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    listaTarjetas.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value) {
                            String titular = doc.getString("nombreTitular");
                            String ultimos4 = doc.getString("ultimos4Digitos");
                            String fechaExp = doc.getString("fechaExpiracion");
                            String marca = doc.getString("marca");
                            Double saldoBD = doc.getDouble("saldo");
                            Boolean predBD = doc.getBoolean("predeterminada");

                            double saldo = saldoBD != null ? saldoBD : 0.0;
                            boolean predeterminada = predBD != null ? predBD : false;

                            listaTarjetas.add(new Tarjeta(doc.getId(), titular, ultimos4, fechaExp, marca, saldo, predeterminada));
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void marcarComoPredeterminada(Tarjeta tarjetaSeleccionada) {
        if (userId == null) return;

        db.collection("usuarios").document(userId).collection("tarjetas").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        boolean esEsta = doc.getId().equals(tarjetaSeleccionada.getId());
                        batch.update(doc.getReference(), "predeterminada", esEsta);
                    }

                    batch.commit().addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Tarjeta predeterminada actualizada", Toast.LENGTH_SHORT).show();
                    });
                });
    }

    private void mostrarDialogoAgregarTarjeta() {
        if (userId == null) return;

        final EditText etUltimos4 = new EditText(this);
        etUltimos4.setHint("Últimos 4 dígitos (Ej: 4235)");
        etUltimos4.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(etUltimos4);

        new AlertDialog.Builder(this)
                .setTitle("Agregar Tarjeta (Simulación)")
                .setMessage("Para probar, ingresa 4 números. Se generará un saldo aleatorio.")
                .setView(layout)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String ultimos4 = etUltimos4.getText().toString().trim();
                    if (ultimos4.length() == 4) {
                        guardarNuevaTarjetaEnBD(ultimos4);
                    } else {
                        Toast.makeText(this, "Deben ser 4 dígitos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarNuevaTarjetaEnBD(String ultimos4) {
        String marca = (ultimos4.startsWith("4")) ? "VISA" : "Mastercard";
        double saldoAleatorio = 1000 + (Math.random() * 5000);

        Map<String, Object> nuevaTarjeta = new HashMap<>();
        nuevaTarjeta.put("nombreTitular", "DANILO OCANA");
        nuevaTarjeta.put("ultimos4Digitos", ultimos4);
        nuevaTarjeta.put("fechaExpiracion", "12/28");
        nuevaTarjeta.put("marca", marca);
        nuevaTarjeta.put("saldo", saldoAleatorio);
        nuevaTarjeta.put("predeterminada", listaTarjetas.isEmpty());

        db.collection("usuarios").document(userId).collection("tarjetas").add(nuevaTarjeta)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Tarjeta vinculada con éxito", Toast.LENGTH_SHORT).show();
                });
    }
}
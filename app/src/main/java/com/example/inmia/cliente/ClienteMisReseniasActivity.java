package com.example.inmia.cliente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class ClienteMisReseniasActivity extends AppCompatActivity {

    private RecyclerView rvResenias;
    private LinearLayout layoutEmpty;
    private TextView tvSubtitulo;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    static class ReseniaItem {
        String nombreProyecto;
        int estrellas;
        String observacion;
        String fecha;
        Timestamp timestamp;

        ReseniaItem(String nombreProyecto, int estrellas,
                    String observacion, String fecha, Timestamp timestamp) {
            this.nombreProyecto = nombreProyecto;
            this.estrellas      = estrellas;
            this.observacion    = observacion;
            this.fecha          = fecha;
            this.timestamp      = timestamp;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_mis_resenias);

        db          = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        FrameLayout btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        tvSubtitulo = findViewById(R.id.tvSubtitulo);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        rvResenias  = findViewById(R.id.rvResenias);
        rvResenias.setLayoutManager(new LinearLayoutManager(this));

        cargarResenias();
    }

    private void cargarResenias() {
        if (currentUser == null) return;

        db.collectionGroup("valoraciones")
                .whereEqualTo("usuarioId", currentUser.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        mostrarResenias(new ArrayList<>());
                        return;
                    }

                    List<ReseniaItem> lista = new ArrayList<>();

                    AtomicInteger pendientes = new AtomicInteger(snapshot.size());

                    for (QueryDocumentSnapshot doc : snapshot) {

                        String proyectoId = doc.getReference()
                                .getParent()
                                .getParent()
                                .getId();

                        int estrellas = 0;
                        if (doc.contains("estrellas")) {
                            Object val = doc.get("estrellas");
                            if (val instanceof Long) estrellas = ((Long) val).intValue();
                            else if (val instanceof Integer) estrellas = (Integer) val;
                        }

                        String observacion = doc.getString("observacion");
                        if (observacion == null) observacion = "";

                        Timestamp ts = doc.getTimestamp("fecha");
                        String fechaStr = "";
                        if (ts != null) {
                            fechaStr = new SimpleDateFormat(
                                    "dd/MM/yyyy", Locale.getDefault()).format(ts.toDate());
                        }

                        final int    starsFinal = estrellas;
                        final String obsFinal   = observacion;
                        final String fechaFinal = fechaStr;
                        final Timestamp tsFinal = ts;

                        db.collection("proyectos").document(proyectoId).get()
                                .addOnSuccessListener(proyDoc -> {
                                    String nombre = proyDoc.getString("nombre");
                                    if (nombre == null) nombre = proyectoId;

                                    lista.add(new ReseniaItem(
                                            nombre, starsFinal,
                                            obsFinal, fechaFinal, tsFinal));


                                    if (pendientes.decrementAndGet() == 0) {

                                        Collections.sort(lista, (a, b) -> {
                                            if (a.timestamp == null) return 1;
                                            if (b.timestamp == null) return -1;
                                            return b.timestamp.compareTo(a.timestamp);
                                        });
                                        mostrarResenias(lista);
                                    }
                                })
                                .addOnFailureListener(e -> {

                                    lista.add(new ReseniaItem(
                                            proyectoId, starsFinal,
                                            obsFinal, fechaFinal, tsFinal));
                                    if (pendientes.decrementAndGet() == 0) {
                                        mostrarResenias(lista);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("MisResenias", "Error: " + e.getMessage());

                    mostrarResenias(new ArrayList<>());
                });
    }

    private void mostrarResenias(List<ReseniaItem> lista) {
        runOnUiThread(() -> {
            int total = lista.size();
            tvSubtitulo.setText(total + (total == 1
                    ? " reseña publicada" : " reseñas publicadas"));

            if (total == 0) {
                layoutEmpty.setVisibility(View.VISIBLE);
                rvResenias.setVisibility(View.GONE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
                rvResenias.setVisibility(View.VISIBLE);
                rvResenias.setAdapter(new ReseniasAdapter(lista));
            }
        });
    }


    static class ReseniasAdapter extends RecyclerView.Adapter<ReseniasAdapter.VH> {

        private final List<ReseniaItem> lista;
        ReseniasAdapter(List<ReseniaItem> lista) { this.lista = lista; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_resenia, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            ReseniaItem item = lista.get(pos);
            h.tvNombre.setText(item.nombreProyecto);
            h.tvFecha.setText(item.fecha);
            h.tvNum.setText(item.estrellas + "/5");

            ImageView[] stars = {h.s1, h.s2, h.s3, h.s4, h.s5};
            for (int i = 0; i < stars.length; i++) {
                stars[i].setImageResource(i < item.estrellas
                        ? R.drawable.ic_star_filled
                        : R.drawable.ic_star_outline);
            }

            if (!item.observacion.isEmpty()) {
                h.tvObs.setText(item.observacion);
                h.tvObs.setVisibility(View.VISIBLE);
            } else {
                h.tvObs.setVisibility(View.GONE);
            }
        }

        @Override public int getItemCount() { return lista.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvNombre, tvFecha, tvNum, tvObs;
            ImageView s1, s2, s3, s4, s5;

            VH(@NonNull View v) {
                super(v);
                tvNombre = v.findViewById(R.id.tvNombreProyecto);
                tvFecha  = v.findViewById(R.id.tvFechaResenia);
                tvNum    = v.findViewById(R.id.tvNumEstrellas);
                tvObs    = v.findViewById(R.id.tvObservacion);
                s1 = v.findViewById(R.id.rStar1);
                s2 = v.findViewById(R.id.rStar2);
                s3 = v.findViewById(R.id.rStar3);
                s4 = v.findViewById(R.id.rStar4);
                s5 = v.findViewById(R.id.rStar5);
            }
        }
    }
}
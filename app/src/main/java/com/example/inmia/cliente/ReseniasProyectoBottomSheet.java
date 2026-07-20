package com.example.inmia.cliente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReseniasProyectoBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_PROYECTO_ID    = "proyectoId";
    private static final String ARG_NOMBRE_PROYECTO = "nombreProyecto";

    public static ReseniasProyectoBottomSheet newInstance(String proyectoId, String nombreProyecto) {
        ReseniasProyectoBottomSheet sheet = new ReseniasProyectoBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_PROYECTO_ID, proyectoId);
        args.putString(ARG_NOMBRE_PROYECTO, nombreProyecto);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_resenias_proyecto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String proyectoId    = getArguments() != null ? getArguments().getString(ARG_PROYECTO_ID) : "";
        String nombreProyecto = getArguments() != null ? getArguments().getString(ARG_NOMBRE_PROYECTO) : "";

        TextView tvTitulo    = view.findViewById(R.id.tvTituloResenias);
        TextView tvPromedio  = view.findViewById(R.id.tvPromedioGeneral);
        TextView tvTotal     = view.findViewById(R.id.tvTotalResenias);
        ImageView[] estrellas = {
                view.findViewById(R.id.starG1),
                view.findViewById(R.id.starG2),
                view.findViewById(R.id.starG3),
                view.findViewById(R.id.starG4),
                view.findViewById(R.id.starG5)
        };
        LinearLayout layoutEmpty = view.findViewById(R.id.layoutEmptyResenias);
        RecyclerView rv          = view.findViewById(R.id.rvReseniasProyecto);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        tvTitulo.setText("Reseñas de " + nombreProyecto);

        if (proyectoId == null || proyectoId.isEmpty()) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("proyectos").document(proyectoId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    double promedio = doc.contains("promedioRating")
                            ? doc.getDouble("promedioRating") : 0.0;
                    long total = doc.contains("totalValoraciones")
                            ? doc.getLong("totalValoraciones") : 0L;

                    tvPromedio.setText(String.format(Locale.US, "%.1f", promedio));
                    tvTotal.setText(total + (total == 1 ? " reseña" : " reseñas"));

                    int estrellasLlenas = (int) Math.round(promedio);
                    for (int i = 0; i < estrellas.length; i++) {
                        estrellas[i].setImageResource(
                                i < estrellasLlenas
                                        ? R.drawable.ic_star_filled
                                        : R.drawable.ic_star_outline);
                    }
                });

        db.collection("proyectos").document(proyectoId)
                .collection("valoraciones")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ClienteMisReseniasActivity.ReseniaItem> lista = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        int stars = 0;
                        if (doc.contains("estrellas")) {
                            Object val = doc.get("estrellas");
                            if (val instanceof Long) stars = ((Long) val).intValue();
                        }
                        String obs = doc.getString("observacion");
                        if (obs == null) obs = "";

                        Timestamp ts = doc.getTimestamp("fecha");
                        String fecha = ts != null
                                ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(ts.toDate()) : "";

                        lista.add(new ClienteMisReseniasActivity.ReseniaItem(
                                "Usuario verificado", stars, obs, fecha, ts));
                    }

                    if (lista.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        rv.setAdapter(new ClienteMisReseniasActivity.ReseniasAdapter(lista));
                    }
                });
    }
}
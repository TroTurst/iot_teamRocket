package com.example.inmia.cliente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.inmia.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class ProyectoValoracionBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_SEP_ID         = "sepId";
    private static final String ARG_PROYECTO_ID    = "proyectoId";
    private static final String ARG_NOMBRE_PROYECTO = "nombreProyecto";
    private static final String ARG_UID            = "uid";

    private String sepId, proyectoId, nombreProyecto, uid;
    private int estrellaSeleccionada = 0;
    private ImageView[] estrellas;

    public static ProyectoValoracionBottomSheet newInstance(String sepId, String proyectoId,
                                                            String nombreProyecto, String uid) {
        ProyectoValoracionBottomSheet sheet = new ProyectoValoracionBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SEP_ID, sepId);
        args.putString(ARG_PROYECTO_ID, proyectoId);
        args.putString(ARG_NOMBRE_PROYECTO, nombreProyecto);
        args.putString(ARG_UID, uid);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sepId          = getArguments().getString(ARG_SEP_ID);
            proyectoId     = getArguments().getString(ARG_PROYECTO_ID);
            nombreProyecto = getArguments().getString(ARG_NOMBRE_PROYECTO);
            uid            = getArguments().getString(ARG_UID);
        }
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_valoracion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        TextView tvTitulo = view.findViewById(R.id.tvTituloValoracion);
        tvTitulo.setText("¿Cómo calificas\n" + nombreProyecto + "?");


        estrellas = new ImageView[]{
                view.findViewById(R.id.star1),
                view.findViewById(R.id.star2),
                view.findViewById(R.id.star3),
                view.findViewById(R.id.star4),
                view.findViewById(R.id.star5)
        };
        for (int i = 0; i < estrellas.length; i++) {
            final int index = i;
            estrellas[i].setOnClickListener(v -> {
                estrellaSeleccionada = index + 1;
                actualizarEstrellas();
            });
        }

        EditText etObservaciones = view.findViewById(R.id.etObservaciones);

        view.findViewById(R.id.btnEnviarValoracion).setOnClickListener(v -> {
            if (estrellaSeleccionada == 0) {
                Toast.makeText(getContext(),
                        "Selecciona una valoración", Toast.LENGTH_SHORT).show();
                return;
            }
            String obs = etObservaciones.getText() != null
                    ? etObservaciones.getText().toString().trim() : "";
            guardarValoracion(estrellaSeleccionada, obs);
        });

        view.findViewById(R.id.tvSaltarValoracion).setOnClickListener(v -> dismiss());
    }

    private void actualizarEstrellas() {
        for (int i = 0; i < estrellas.length; i++) {
            estrellas[i].setImageResource(
                    i < estrellaSeleccionada
                            ? R.drawable.ic_star_filled
                            : R.drawable.ic_star_outline);
        }
    }


    private void guardarValoracion(int stars, String observacion) {
        if (proyectoId == null) {
            Toast.makeText(getContext(),
                    "No se encontró el proyecto", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        com.google.firebase.firestore.DocumentReference proyectoRef = db.collection("proyectos").document(proyectoId);
        com.google.firebase.firestore.DocumentReference valoracionRef = proyectoRef.collection("valoraciones").document(uid);
        com.google.firebase.firestore.DocumentReference separacionRef = db.collection("separaciones").document(sepId);

        db.runTransaction((Transaction.Function<Void>) transaction -> {


            com.google.firebase.firestore.DocumentSnapshot proyectoSnap = transaction.get(proyectoRef);

            long totalActual   = proyectoSnap.contains("totalValoraciones")
                    ? proyectoSnap.getLong("totalValoraciones") : 0L;
            double promedioActual = proyectoSnap.contains("promedioRating")
                    ? proyectoSnap.getDouble("promedioRating") : 0.0;

            long nuevoTotal     = totalActual + 1;
            double nuevoPromedio = ((promedioActual * totalActual) + stars) / nuevoTotal;
            nuevoPromedio = Math.round(nuevoPromedio * 10.0) / 10.0;


            Map<String, Object> valoracionData = new HashMap<>();
            valoracionData.put("usuarioId", uid);
            valoracionData.put("estrellas", stars);
            valoracionData.put("observacion", observacion);
            valoracionData.put("fecha", Timestamp.now());
            transaction.set(valoracionRef, valoracionData);


            Map<String, Object> proyectoUpdate = new HashMap<>();
            proyectoUpdate.put("totalValoraciones", nuevoTotal);
            proyectoUpdate.put("promedioRating", nuevoPromedio);
            transaction.update(proyectoRef, proyectoUpdate);


            Map<String, Object> sepUpdate = new HashMap<>();
            sepUpdate.put("valoracionProyecto", stars);
            transaction.update(separacionRef, sepUpdate);

            return null;
        }).addOnSuccessListener(unused -> {
            Toast.makeText(getContext(),
                    "¡Gracias por valorar " + nombreProyecto + "!",
                    Toast.LENGTH_SHORT).show();
            dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(),
                    "Error al guardar. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
        });
    }
}
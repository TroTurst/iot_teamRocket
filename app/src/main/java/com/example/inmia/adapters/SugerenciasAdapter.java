package com.example.inmia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;

public class SugerenciasAdapter extends RecyclerView.Adapter<SugerenciasAdapter.SugerenciaVH> {

    public interface OnSugerenciaClick {
        void onClick(AutocompletePrediction prediction);
    }

    private final List<AutocompletePrediction> data = new ArrayList<>();
    private final OnSugerenciaClick listener;

    public SugerenciasAdapter(OnSugerenciaClick listener) {
        this.listener = listener;
    }

    public void setData(List<AutocompletePrediction> nuevas) {
        data.clear();
        data.addAll(nuevas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SugerenciaVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sugerencia, parent, false);
        return new SugerenciaVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SugerenciaVH h, int position) {
        AutocompletePrediction p = data.get(position);
        h.tvPrimario.setText(p.getPrimaryText(null).toString());
        h.tvSecundario.setText(p.getSecondaryText(null).toString());
        h.itemView.setOnClickListener(v -> listener.onClick(p));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SugerenciaVH extends RecyclerView.ViewHolder {
        TextView tvPrimario, tvSecundario;

        SugerenciaVH(View v) {
            super(v);
            tvPrimario = v.findViewById(R.id.tvSugerenciaPrimario);
            tvSecundario = v.findViewById(R.id.tvSugerenciaSecundario);
        }
    }
}

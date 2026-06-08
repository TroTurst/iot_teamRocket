package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Tipologia;

import java.util.List;

public class TipologiasAgregadasAdapter extends RecyclerView.Adapter<TipologiasAgregadasAdapter.TipologiaViewHolder> {

    private final List<Tipologia> tipologias;
    private final OnTipologiaRemoveListener removeListener;

    public interface OnTipologiaRemoveListener {
        void onRemove(Tipologia tipologia);
    }

    public TipologiasAgregadasAdapter(List<Tipologia> tipologias, OnTipologiaRemoveListener removeListener) {
        this.tipologias = tipologias;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public TipologiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tipologia_agregada, parent, false);
        return new TipologiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipologiaViewHolder holder, int position) {
        Tipologia tipologia = tipologias.get(position);
        holder.bind(tipologia);
    }

    @Override
    public int getItemCount() {
        return tipologias.size();
    }

    class TipologiaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombreTipologia;
        private final TextView tvDetallesTipologia;
        private final TextView tvPrecioTipologia;
        private final ImageButton btnEliminarTipologia;

        TipologiaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreTipologia = itemView.findViewById(R.id.tvNombreTipologiaAgregada);
            tvDetallesTipologia = itemView.findViewById(R.id.tvDetallesTipologiaAgregada);
            tvPrecioTipologia = itemView.findViewById(R.id.tvPrecioTipologiaAgregada);
            btnEliminarTipologia = itemView.findViewById(R.id.btnEliminarTipologia);
        }

        void bind(Tipologia tipologia) {
            tvNombreTipologia.setText(tipologia.getNombre());
            tvDetallesTipologia.setText(tipologia.getArea() + " · " + tipologia.getDormitorios() + "d · " + tipologia.getBanos() + "b");
            tvPrecioTipologia.setText(tipologia.getPrecio());
            btnEliminarTipologia.setOnClickListener(v -> {
                if (removeListener != null) {
                    removeListener.onRemove(tipologia);
                }
            });
        }
    }
}
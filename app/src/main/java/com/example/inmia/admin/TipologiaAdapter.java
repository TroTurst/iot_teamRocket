package com.example.inmia.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Tipologia;

import java.util.List;

/** Adapter simple para listar tipologías de un proyecto en el detalle. */
public class TipologiaAdapter extends RecyclerView.Adapter<TipologiaAdapter.TipologiaViewHolder> {

    public interface OnTipologiaClickListener {
        void onTipologiaClick(Tipologia tipologia);
    }

    private final Context context;
    private final List<Tipologia> tipologias;
    private final OnTipologiaClickListener listener;
    private String selectedTipologiaId;

    public TipologiaAdapter(Context context,
                            List<Tipologia> tipologias,
                            String selectedTipologiaId,
                            OnTipologiaClickListener listener) {
        this.context = context;
        this.tipologias = tipologias;
        this.selectedTipologiaId = selectedTipologiaId;
        this.listener = listener;
    }

    public void setSelectedTipologiaId(String selectedTipologiaId) {
        this.selectedTipologiaId = selectedTipologiaId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TipologiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tipologia, parent, false);
        return new TipologiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipologiaViewHolder holder, int position) {
        Tipologia tipologia = tipologias.get(position);

        holder.tvNombreTipologia.setText(tipologia.getNombre());
        holder.tvSpecsTipologia.setText(tipologia.getArea() + " • " + tipologia.getDormitorios() + "d • " + tipologia.getBanos() + "b");
        holder.tvPrecioTipologia.setText(tipologia.getPrecio());

        boolean selected = tipologia.getId() != null && tipologia.getId().equals(selectedTipologiaId);
        holder.itemView.setSelected(selected);
        holder.itemView.setAlpha(selected ? 1f : 0.9f);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTipologiaClick(tipologia);
        });
    }

    @Override
    public int getItemCount() {
        return tipologias != null ? tipologias.size() : 0;
    }

    static class TipologiaViewHolder extends RecyclerView.ViewHolder {

        final TextView tvNombreTipologia;
        final TextView tvSpecsTipologia;
        final TextView tvPrecioTipologia;

        TipologiaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreTipologia = itemView.findViewById(R.id.tvNombreTipologia);
            tvSpecsTipologia = itemView.findViewById(R.id.tvSpecsTipologia);
            tvPrecioTipologia = itemView.findViewById(R.id.tvPrecioTipologia);
        }
    }
}


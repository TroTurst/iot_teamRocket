package com.example.inmia.cliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Tipologia;

import java.util.List;

public class SimpleTipologiaAdapter extends RecyclerView.Adapter<SimpleTipologiaAdapter.ViewHolder> {
    private List<Tipologia> lista;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tipologia tp);
    }

    public SimpleTipologiaAdapter(List<Tipologia> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tipologia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tipologia tp = lista.get(position);

        holder.tvNombre.setText(tp.getNombre());

        String specs = tp.getArea() + " m² • " + tp.getDormitorios() + "d • " + tp.getBanos() + "b";
        holder.tvSpecs.setText(specs);

        holder.tvPrecio.setText(tp.getPrecio());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(tp));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvSpecs, tvPrecio;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreTipologia);
            tvSpecs = itemView.findViewById(R.id.tvSpecsTipologia);
            tvPrecio = itemView.findViewById(R.id.tvPrecioTipologia);
        }
    }
}

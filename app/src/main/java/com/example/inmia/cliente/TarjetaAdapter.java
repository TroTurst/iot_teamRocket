package com.example.inmia.cliente;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inmia.R;
import com.example.inmia.models.Tarjeta;
import java.util.List;

public class TarjetaAdapter extends RecyclerView.Adapter<TarjetaAdapter.ViewHolder> {

    private List<Tarjeta> listaTarjetas;
    private OnTarjetaClickListener listener;

    public interface OnTarjetaClickListener {
        void onSetPredeterminada(Tarjeta tarjeta);
    }

    public TarjetaAdapter(List<Tarjeta> listaTarjetas, OnTarjetaClickListener listener) {
        this.listaTarjetas = listaTarjetas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarjeta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tarjeta t = listaTarjetas.get(position);

        holder.tvBalanceTarjeta.setText(String.format("S/ %,.2f", t.getSaldo()));
        holder.tvNumTarjeta.setText("•••• •••• •••• " + t.getUltimos4Digitos());
        holder.tvExpiryTarjeta.setText(t.getFechaExpiracion());
        holder.tvMarcaTarjeta.setText(t.getMarca());

        if (position % 2 == 0) {
            holder.cardFondo.setBackgroundResource(R.drawable.bg_tarjeta_azul);
        } else {
            holder.cardFondo.setBackgroundResource(R.drawable.bg_tarjeta_oscura);
        }

        if (t.isPredeterminada()) {
            holder.imgCheckStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0A3D46")));
            holder.tvPredeterminado.setText("Medio de pago predeterminado");
            holder.tvPredeterminado.setTextColor(Color.parseColor("#0A3D46"));
            holder.tvPredeterminado.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            holder.imgCheckStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            holder.tvPredeterminado.setText("Usar como predeterminado");
            holder.tvPredeterminado.setTextColor(Color.parseColor("#999999"));
            holder.tvPredeterminado.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        holder.btnPredeterminado.setOnClickListener(v -> {
            if (!t.isPredeterminada()) {
                listener.onSetPredeterminada(t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTarjetas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cardFondo;
        TextView tvBalanceTarjeta, tvNumTarjeta, tvExpiryTarjeta, tvMarcaTarjeta, tvPredeterminado;
        ImageView imgCheckStatus;
        LinearLayout btnPredeterminado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFondo = itemView.findViewById(R.id.cardFondo);
            tvBalanceTarjeta = itemView.findViewById(R.id.tvBalanceTarjeta);
            tvNumTarjeta = itemView.findViewById(R.id.tvNumTarjeta);
            tvExpiryTarjeta = itemView.findViewById(R.id.tvExpiryTarjeta);
            tvMarcaTarjeta = itemView.findViewById(R.id.tvMarcaTarjeta);
            tvPredeterminado = itemView.findViewById(R.id.tvPredeterminado);
            imgCheckStatus = itemView.findViewById(R.id.imgCheckStatus);
            btnPredeterminado = itemView.findViewById(R.id.btnPredeterminado);
        }
    }
}
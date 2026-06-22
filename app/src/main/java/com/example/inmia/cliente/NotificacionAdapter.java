package com.example.inmia.cliente;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Notificacion;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.ViewHolder> {

    public interface OnNotifClick {
        void onClick(Notificacion notif);
    }

    private final List<Notificacion> lista;
    private final OnNotifClick listener;

    public NotificacionAdapter(List<Notificacion> lista, OnNotifClick listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notificacion notif = lista.get(position);

        if (notif.getTitulo() != null && !notif.getTitulo().isEmpty()) {
            holder.tvNotifText.setText(notif.getTitulo());
        } else {
            holder.tvNotifText.setText(notif.getTexto());
        }

        holder.tvNotifTime.setText(notif.getFechaFormateada());

        holder.itemView.setBackgroundColor(
                notif.isLeido() ? Color.TRANSPARENT : Color.parseColor("#F0FAF9"));

        switch (notif.getTipo() != null ? notif.getTipo() : "") {
            case "MENSAJE":
                holder.cardIconContainer.setCardBackgroundColor(Color.parseColor("#D0F0FF"));
                holder.imgNotifIcon.setImageResource(R.drawable.ic_chat);
                holder.imgNotifIcon.setColorFilter(Color.parseColor("#0277BD"));
                break;
            case "CITA":
                holder.cardIconContainer.setCardBackgroundColor(Color.parseColor("#FFF9C4"));
                holder.imgNotifIcon.setImageResource(R.drawable.ic_calendar);
                holder.imgNotifIcon.setColorFilter(Color.parseColor("#F57F17"));
                break;
            case "SEPARACION":
                boolean rechazada = notif.getTexto() != null &&
                        notif.getTexto().toLowerCase().contains("rechaz");
                if (rechazada) {
                    holder.cardIconContainer.setCardBackgroundColor(Color.parseColor("#FFCDD2"));
                    holder.imgNotifIcon.setImageResource(R.drawable.ic_close);
                    holder.imgNotifIcon.setColorFilter(Color.parseColor("#C62828"));
                } else {
                    holder.cardIconContainer.setCardBackgroundColor(Color.parseColor("#A8E6CF"));
                    holder.imgNotifIcon.setImageResource(R.drawable.ic_check);
                    holder.imgNotifIcon.setColorFilter(Color.parseColor("#2E7D32"));
                }
                break;
            default:
                holder.cardIconContainer.setCardBackgroundColor(Color.parseColor("#A8E6CF"));
                holder.imgNotifIcon.setImageResource(R.drawable.ic_check);
                holder.imgNotifIcon.setColorFilter(Color.parseColor("#2E7D32"));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(notif);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotifText, tvNotifTime;
        MaterialCardView cardIconContainer;
        ImageView imgNotifIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotifText       = itemView.findViewById(R.id.tvNotifText);
            tvNotifTime       = itemView.findViewById(R.id.tvNotifTime);
            cardIconContainer = itemView.findViewById(R.id.cardIconContainer);
            imgNotifIcon      = itemView.findViewById(R.id.imgNotifIcon);
        }
    }
}
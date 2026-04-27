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
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.ViewHolder> {

    private List<Notificacion> listaNotificaciones;

    public NotificacionAdapter(List<Notificacion> listaNotificaciones) {
        this.listaNotificaciones = listaNotificaciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notificacion notif = listaNotificaciones.get(position);

        holder.tvNotifText.setText(notif.getTexto());
        holder.tvNotifTime.setText(notif.getFechaHora());

        if (notif.getTipo().equals("ERROR") || notif.getTipo().equals("CANCELADO")) {
            holder.cardIconContainer.setCardBackgroundColor(Color.parseColor("#FFCDD2"));
            holder.imgNotifIcon.setImageResource(R.drawable.ic_close); // Icono de X
            holder.imgNotifIcon.setColorFilter(Color.parseColor("#C62828"));
        } else {
            holder.cardIconContainer.setCardBackgroundColor(Color.parseColor("#A8E6CF"));
            holder.imgNotifIcon.setImageResource(R.drawable.ic_check);
            holder.imgNotifIcon.setColorFilter(Color.parseColor("#2E7D32"));
        }
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotifText, tvNotifTime;
        MaterialCardView cardIconContainer;
        ImageView imgNotifIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotifText = itemView.findViewById(R.id.tvNotifText);
            tvNotifTime = itemView.findViewById(R.id.tvNotifTime);
            cardIconContainer = itemView.findViewById(R.id.cardIconContainer);
            imgNotifIcon = itemView.findViewById(R.id.imgNotifIcon);
        }
    }
}
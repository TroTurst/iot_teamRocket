package com.example.inmia.asesor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class NotificacionItemAdapter extends RecyclerView.Adapter<NotificacionItemAdapter.NotificacionViewHolder> {

    public interface Listener {
        void onNotificationSelected(NotificacionItem item);
    }

    private final List<NotificacionItem> items;
    private final Listener listener;

    public NotificacionItemAdapter(List<NotificacionItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_notificacion_asesor, parent, false);
        return new NotificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        NotificacionItem item = items.get(position);
        holder.message.setText(item.getMessage());
        holder.status.setText(item.getStatus());
        holder.time.setText(item.getTime());
        holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), item.getStatusColorRes()));
        holder.icon.setImageResource(item.getIconResId());
        holder.iconCard.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), item.getIconBackgroundColorRes()));
        holder.line.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), item.getLineColorRes()));
        holder.line.setAlpha(item.getLineAlpha());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        final View line;
        final MaterialCardView iconCard;
        final ImageView icon;
        final TextView message;
        final TextView status;
        final TextView time;

        NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            line = itemView.findViewById(R.id.viewLine);
            iconCard = itemView.findViewById(R.id.cardIcon);
            icon = itemView.findViewById(R.id.imgIcon);
            message = itemView.findViewById(R.id.tvMessage);
            status = itemView.findViewById(R.id.tvStatus);
            time = itemView.findViewById(R.id.tvTime);
        }
    }
}

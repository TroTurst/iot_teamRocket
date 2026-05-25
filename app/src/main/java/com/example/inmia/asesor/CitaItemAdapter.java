package com.example.inmia.asesor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

import java.util.List;

public class CitaItemAdapter extends RecyclerView.Adapter<CitaItemAdapter.CitaViewHolder> {

    public interface Listener {
        void onCitaSelected(CitaItem item);
    }

    private final List<CitaItem> items;
    private final Listener listener;

    public CitaItemAdapter(List<CitaItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_cita_asesor, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        CitaItem item = items.get(position);
        holder.status.setText(item.getStatus());
        holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), item.getStatusColorRes()));
        holder.status.setAlpha(item.getStatusAlpha());
        holder.project.setText(item.getProject());
        holder.client.setText(item.getClient());
        holder.location.setText(item.getLocation());
        holder.dateTime.setText(item.getDateTime());

        holder.btnDetalles.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCitaSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<CitaItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {
        final TextView status;
        final TextView project;
        final TextView client;
        final TextView location;
        final TextView dateTime;
        final TextView btnDetalles;

        CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.tvStatus);
            project = itemView.findViewById(R.id.tvProyecto);
            client = itemView.findViewById(R.id.tvCliente);
            location = itemView.findViewById(R.id.tvUbicacion);
            dateTime = itemView.findViewById(R.id.tvFecha);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
        }
    }
}

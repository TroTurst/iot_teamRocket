package com.example.inmia.asesor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

import java.util.List;

public class SeparacionItemAdapter extends RecyclerView.Adapter<SeparacionItemAdapter.SeparacionViewHolder> {

    public interface Listener {
        void onAction(SeparacionItem item, int position);
        void onDetails(SeparacionItem item);
    }

    private final List<SeparacionItem> items;
    private final Listener listener;

    public SeparacionItemAdapter(List<SeparacionItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SeparacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_separacion_asesor, parent, false);
        return new SeparacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeparacionViewHolder holder, int position) {
        SeparacionItem item = items.get(position);
        holder.status.setText(item.getStatus());
        AsesorEstadoBadgeStyle.apply(holder.itemView.getContext(), holder.status, item.getStatus());
        holder.project.setText(item.getProject());
        holder.location.setText(item.getLocation());
        holder.company.setText(item.getCompany());
        holder.action.setText(item.getActionLabel());

        holder.action.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAction(item, position);
            }
        });

        holder.btnDetalles.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetails(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetails(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<SeparacionItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    static class SeparacionViewHolder extends RecyclerView.ViewHolder {
        final TextView status;
        final TextView project;
        final TextView location;
        final TextView company;
        final TextView action;
        final TextView btnDetalles;

        SeparacionViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.tvStatus);
            project = itemView.findViewById(R.id.tvProyecto);
            location = itemView.findViewById(R.id.tvUbicacion);
            company = itemView.findViewById(R.id.tvEmpresa);
            action = itemView.findViewById(R.id.btnAction);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
        }
    }
}

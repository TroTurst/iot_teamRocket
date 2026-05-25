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

public class HomeCitaAdapter extends RecyclerView.Adapter<HomeCitaAdapter.HomeCitaViewHolder> {

    public interface Listener {
        void onCitaSelected(HomeCita item);
    }

    private final List<HomeCita> items;
    private final Listener listener;

    public HomeCitaAdapter(List<HomeCita> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeCitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_home_cita, parent, false);
        return new HomeCitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCitaViewHolder holder, int position) {
        HomeCita item = items.get(position);
        holder.time.setText(item.getTime());
        holder.meridian.setText(item.getMeridian());
        holder.client.setText(item.getClient());
        holder.project.setText(item.getProject());
        holder.status.setText(item.getStatus());

        if ("Cancelada".equalsIgnoreCase(item.getStatus())) {
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.inmia_white));
            holder.status.setBackgroundResource(R.drawable.bg_badge_red_circle);
        } else if (item.isConfirmed()) {
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.inmia_white));
            holder.status.setBackgroundResource(R.drawable.btn_rounded);
        } else {
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.inmia_teal_dark));
            holder.status.setBackgroundResource(R.drawable.badge_outline);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCitaSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<HomeCita> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    static class HomeCitaViewHolder extends RecyclerView.ViewHolder {
        final TextView time;
        final TextView meridian;
        final TextView client;
        final TextView project;
        final TextView status;

        HomeCitaViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tvTime);
            meridian = itemView.findViewById(R.id.tvMeridian);
            client = itemView.findViewById(R.id.tvClient);
            project = itemView.findViewById(R.id.tvProject);
            status = itemView.findViewById(R.id.tvStatus);
        }
    }
}

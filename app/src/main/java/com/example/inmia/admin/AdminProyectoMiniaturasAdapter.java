package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

import java.util.ArrayList;
import java.util.List;

public class AdminProyectoMiniaturasAdapter extends RecyclerView.Adapter<AdminProyectoMiniaturasAdapter.MiniaturaViewHolder> {

    public interface OnMiniaturaClickListener {
        void onMiniaturaClick(int position);
    }

    private final List<Integer> imagenes = new ArrayList<>();
    private final OnMiniaturaClickListener onMiniaturaClickListener;

    public AdminProyectoMiniaturasAdapter(OnMiniaturaClickListener onMiniaturaClickListener) {
        this.onMiniaturaClickListener = onMiniaturaClickListener;
    }

    public void submitList(List<Integer> nuevasImagenes) {
        imagenes.clear();
        if (nuevasImagenes != null) {
            imagenes.addAll(nuevasImagenes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MiniaturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_miniatura_tipologia, parent, false);
        return new MiniaturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniaturaViewHolder holder, int position) {
        holder.imageView.setImageResource(imagenes.get(position));
        View.OnClickListener clickListener = v -> {
            if (onMiniaturaClickListener != null) {
                onMiniaturaClickListener.onMiniaturaClick(position);
            }
        };
        holder.itemView.setOnClickListener(clickListener);
        holder.imageView.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return imagenes.size();
    }

    static class MiniaturaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        MiniaturaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgMiniaturaTipologia);
            imageView.setClickable(true);
            imageView.setFocusable(true);
        }
    }
}

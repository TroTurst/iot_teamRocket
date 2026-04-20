package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

public class AdminProyectoGaleriaAdapter extends RecyclerView.Adapter<AdminProyectoGaleriaAdapter.GaleriaViewHolder> {

    private final int[] images;

    public AdminProyectoGaleriaAdapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public GaleriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_proyecto_galeria, parent, false);
        return new GaleriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GaleriaViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class GaleriaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        GaleriaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgGaleriaProyecto);
        }
    }
}




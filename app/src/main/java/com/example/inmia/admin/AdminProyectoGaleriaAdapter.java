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
    private final OnImageClickListener onImageClickListener;

    public interface OnImageClickListener {
        void onImageClick(int imageRes, int position);
    }

    public AdminProyectoGaleriaAdapter(int[] images, OnImageClickListener onImageClickListener) {
        this.images = images;
        this.onImageClickListener = onImageClickListener;
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
        int imageRes = images[position];
        holder.imageView.setImageResource(imageRes);
        View.OnClickListener clickListener = v -> {
            if (onImageClickListener != null) {
                onImageClickListener.onImageClick(imageRes, position);
            }
        };
        holder.itemView.setOnClickListener(clickListener);
        holder.imageView.setOnClickListener(clickListener);
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
            imageView.setClickable(true);
            imageView.setFocusable(true);
        }
    }
}

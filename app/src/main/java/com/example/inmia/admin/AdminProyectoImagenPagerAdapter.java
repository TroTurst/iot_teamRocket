package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.github.chrisbanes.photoview.PhotoView;

public class AdminProyectoImagenPagerAdapter extends RecyclerView.Adapter<AdminProyectoImagenPagerAdapter.ImagenViewHolder> {

    private final int[] images;

    public AdminProyectoImagenPagerAdapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImagenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_proyecto_imagen_pager, parent, false);
        return new ImagenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    static class ImagenViewHolder extends RecyclerView.ViewHolder {
        private final PhotoView imageView;

        ImagenViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgProyectoPager);
        }
    }
}


package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class AdminProyectoImagenPagerAdapter extends RecyclerView.Adapter<AdminProyectoImagenPagerAdapter.ImagenViewHolder> {

    private final List<String> imageUrls;

    public AdminProyectoImagenPagerAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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
        String url = imageUrls.get(position);
        if (url != null && !url.isEmpty()) {
            Glide.with(holder.imageView.getContext()).load(url)
                    .placeholder(R.drawable.ic_add).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_add);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImagenViewHolder extends RecyclerView.ViewHolder {
        private final PhotoView imageView;

        ImagenViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgProyectoPager);
        }
    }
}

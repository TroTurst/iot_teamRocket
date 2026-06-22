package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmia.R;

import java.util.ArrayList;
import java.util.List;

public class AdminProyectoMiniaturasAdapter extends RecyclerView.Adapter<AdminProyectoMiniaturasAdapter.MiniaturaViewHolder> {

    public interface OnMiniaturaClickListener {
        void onMiniaturaClick(int position);
    }

    private final List<String> imageUrls = new ArrayList<>();
    private final OnMiniaturaClickListener onMiniaturaClickListener;

    public AdminProyectoMiniaturasAdapter(OnMiniaturaClickListener onMiniaturaClickListener) {
        this.onMiniaturaClickListener = onMiniaturaClickListener;
    }

    public void submitUrls(List<String> nuevasUrls) {
        imageUrls.clear();
        if (nuevasUrls != null) {
            imageUrls.addAll(nuevasUrls);
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
        String url = imageUrls.get(position);
        if (url != null && !url.isEmpty()) {
            Glide.with(holder.imageView.getContext()).load(url)
                    .centerCrop().placeholder(R.drawable.ic_add).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_add);
        }
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
        return imageUrls.size();
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

package com.example.inmia.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Asesor;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdminAsesorAdapter extends RecyclerView.Adapter<AdminAsesorAdapter.AsesorViewHolder> {

    public interface OnAsesorClickListener {
        void onDetalle(Asesor asesor);
    }

    private final Context context;
    private final List<Asesor> asesores;
    private final OnAsesorClickListener listener;

    public AdminAsesorAdapter(Context context, List<Asesor> asesores, OnAsesorClickListener listener) {
        this.context = context;
        this.asesores = asesores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AsesorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_asesor_admin, parent, false);
        return new AsesorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AsesorViewHolder holder, int position) {
        Asesor asesor = asesores.get(position);
        holder.tvNombre.setText(asesor.getNombre());
        holder.tvEspecialidad.setText(asesor.getEspecialidad());
        if (asesor.getFotoResId() != 0) {
            holder.imgAvatar.setImageResource(asesor.getFotoResId());
        }

        holder.btnDetalles.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetalle(asesor);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetalle(asesor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return asesores != null ? asesores.size() : 0;
    }

    static class AsesorViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgAvatar;
        private final TextView tvNombre;
        private final TextView tvEspecialidad;
        private final MaterialButton btnDetalles;

        AsesorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvNombre = itemView.findViewById(R.id.tvNombreAsesor);
            tvEspecialidad = itemView.findViewById(R.id.tvEspecialidadAsesor);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
        }
    }
}


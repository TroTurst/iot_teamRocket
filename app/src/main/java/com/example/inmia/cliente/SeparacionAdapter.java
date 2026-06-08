package com.example.inmia.cliente;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;
import com.example.inmia.models.Separacion;

public class SeparacionAdapter extends RecyclerView.Adapter<SeparacionAdapter.ViewHolder> {

    private List<Separacion> listaSeparaciones;

    public SeparacionAdapter(List<Separacion> listaSeparaciones) {
        this.listaSeparaciones = listaSeparaciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_separacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Separacion sep = listaSeparaciones.get(position);

        holder.tvEstadoSeparacion.setText(sep.getEstado().toUpperCase());
        holder.tvNombreProyecto.setText(sep.getNombre());
        holder.tvUbicacion.setText(sep.getUbicacion());
        holder.tvEmpresa.setText(sep.getEmpresa());

        if (sep.getImagenUrl() != null && !sep.getImagenUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(sep.getImagenUrl())
                    .placeholder(R.drawable.onboarding1)
                    .into(holder.imgSeparacion);
        } else {
            holder.imgSeparacion.setImageResource(R.drawable.onboarding1);
        }

        String estadoLimpio = sep.getEstado().trim().toLowerCase();
        if (estadoLimpio.equals("aprobada")) {
            holder.tvEstadoSeparacion.setTextColor(Color.parseColor("#2ECC71"));
        } else if (estadoLimpio.equals("no aprobada") || estadoLimpio.equals("rechazada")) {
            holder.tvEstadoSeparacion.setTextColor(Color.parseColor("#FF4C4C"));
        } else {
            holder.tvEstadoSeparacion.setTextColor(Color.parseColor("#FFA000"));
        }

        holder.btnDetallesSeparacion.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ClienteSeparacionAprobadaActivity.class);
            intent.putExtra("SEPARACION_ID", sep.getId());
            intent.putExtra("ESTADO_SEPARACION", estadoLimpio);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaSeparaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgSeparacion;
        TextView tvEstadoSeparacion, tvNombreProyecto, tvUbicacion, tvEmpresa;
        MaterialButton btnDetallesSeparacion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSeparacion = itemView.findViewById(R.id.imgSeparacion);
            tvEstadoSeparacion = itemView.findViewById(R.id.tvEstadoSeparacion);
            tvNombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvEmpresa = itemView.findViewById(R.id.tvEmpresa);
            btnDetallesSeparacion = itemView.findViewById(R.id.btnDetallesSeparacion);
        }
    }
}
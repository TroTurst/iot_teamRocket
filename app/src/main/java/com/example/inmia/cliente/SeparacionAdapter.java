package com.example.inmia.cliente;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

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

        holder.tvEstadoSeparacion.setText(sep.getEstado());
        holder.tvNombreProyecto.setText(sep.getNombre());
        holder.tvUbicacion.setText(sep.getUbicacion());
        holder.tvEmpresa.setText(sep.getEmpresa());
        holder.imgSeparacion.setImageResource(sep.getImagenResId());

        if (sep.getEstado().equalsIgnoreCase("Aprobada")) {
            holder.tvEstadoSeparacion.setTextColor(Color.parseColor("#4CAF50"));
        } else if (sep.getEstado().equalsIgnoreCase("No aprobada")) {
            holder.tvEstadoSeparacion.setTextColor(Color.parseColor("#FF4C4C"));
        } else {
            holder.tvEstadoSeparacion.setTextColor(Color.parseColor("#18C0C1"));
        }

        holder.btnDetallesSeparacion.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ClienteSeparacionAprobadaActivity.class);
            intent.putExtra("PROYECTO_NOMBRE", sep.getNombre());
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
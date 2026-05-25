package com.example.inmia.cliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

import java.util.List;

public class ProyectosAdapter extends RecyclerView.Adapter<ProyectosAdapter.ProyectoViewHolder> {

    private List<Proyecto> listaProyectos;

    public ProyectosAdapter(List<Proyecto> listaProyectos) {
        this.listaProyectos = listaProyectos;
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proyecto, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        Proyecto proyecto = listaProyectos.get(position);

        holder.tvNombre.setText(proyecto.getNombre());
        holder.tvUbicacion.setText(proyecto.getUbicacion());
        holder.tvPrecio.setText(proyecto.getPrecio());
        holder.tvEtiqueta.setText(proyecto.getEtiqueta());
        holder.imgProyecto.setImageResource(proyecto.getImagenResId());

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ClienteDetallePropiedadActivity.class);

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaProyectos.size();
    }

    public static class ProyectoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUbicacion, tvPrecio, tvEtiqueta;
        ImageView imgProyecto;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvProjectName);
            tvUbicacion = itemView.findViewById(R.id.tvProjectLocation);
            tvPrecio = itemView.findViewById(R.id.tvProjectPrice);
            tvEtiqueta = itemView.findViewById(R.id.tvBadge);
            imgProyecto = itemView.findViewById(R.id.imgProject);
        }
    }
}
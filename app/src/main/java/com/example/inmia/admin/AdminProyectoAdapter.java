package com.example.inmia.admin;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Proyecto;

import java.util.List;

/**
 * Adapter para mostrar una lista de Proyectos en RecyclerView
 *
 * Cada item mostrará:
 * - Imagen hero del proyecto
 * - Nombre del proyecto
 * - Ubicación
 * - Inmobiliaria
 * - Estado del proyecto
 * - Tipología principal (área, precio, dormitorios)
 */
public class AdminProyectoAdapter extends RecyclerView.Adapter<AdminProyectoAdapter.ProyectoViewHolder> {

    private List<Proyecto> proyectos;
    private Context context;

    public AdminProyectoAdapter(Context context, List<Proyecto> proyectos) {
        this.context = context;
        this.proyectos = proyectos;
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_proyecto_admin, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        Proyecto proyecto = proyectos.get(position);

        // Información básica del proyecto
        holder.tvNombreProyecto.setText(proyecto.getNombre());
        holder.tvUbicacion.setText(proyecto.getUbicacion());
        holder.tvInmobiliaria.setText(proyecto.getInmobiliaria());
        holder.tvEstado.setText(proyecto.getEstadoProyecto());

        int estadoColor = resolveEstadoColor(holder.itemView, proyecto.getEstadoProyecto());
        holder.tvEstado.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.inmia_white));
        holder.tvEstado.setBackgroundTintList(ColorStateList.valueOf(estadoColor));

        // Imagen hero del proyecto
        if (proyecto.getImagenHeroPrincipal() != 0) {
            holder.imgProyecto.setImageResource(proyecto.getImagenHeroPrincipal());
        }

        // Información de la tipología principal
        if (proyecto.getTipologiaPrincipal() != null) {
            holder.tvArea.setText(proyecto.getTipologiaPrincipal().getArea());
            holder.tvDormitorios.setText(proyecto.getTipologiaPrincipal().getDormitorios() + "d");
            holder.tvPrecio.setText(proyecto.getTipologiaPrincipal().getPrecio());
            holder.tvBanos.setText(proyecto.getTipologiaPrincipal().getBanos() + "b");
        }

        // Número de tipologías disponibles
        if (proyecto.getTipologias() != null && !proyecto.getTipologias().isEmpty()) {
            holder.tvNumTipologias.setText("+" + proyecto.getTipologias().size() + " tipologías");
        }

        // Mostrar si es pet-friendly (del proyecto)
        StringBuilder features = new StringBuilder();
        if (proyecto.isPetFriendly()) {
            features.append("🐾 Pet Friendly");
        }
        
        // Mostrar si tiene ascensor (del proyecto)
        if (proyecto.isConAscensor()) {
            if (features.length() > 0) features.append(" • ");
            features.append("🛗 Ascensor");
        }
        
        // Mostrar certificado energético (de la tipología principal)
        if (proyecto.getTipologiaPrincipal() != null && 
            proyecto.getTipologiaPrincipal().getCertificadoEnergetico() != null &&
            !proyecto.getTipologiaPrincipal().getCertificadoEnergetico().isEmpty()) {
            if (features.length() > 0) features.append(" • ");
            features.append("⚡ " + proyecto.getTipologiaPrincipal().getCertificadoEnergetico());
        }
        
        // Mostrar si tiene terraza o balcón (de la tipología principal)
        if (proyecto.getTipologiaPrincipal() != null) {
            StringBuilder amenidades = new StringBuilder();
            if (proyecto.getTipologiaPrincipal().isTerraza()) {
                amenidades.append("🏖️ Terraza");
            }
            if (proyecto.getTipologiaPrincipal().isBalcon()) {
                if (amenidades.length() > 0) amenidades.append(" • ");
                amenidades.append("🪟 Balcón");
            }
            if (amenidades.length() > 0) {
                if (features.length() > 0) features.append(" • ");
                features.append(amenidades.toString());
            }
        }
        
        // Asignar al TextView
        if (features.length() > 0) {
            holder.tvPetFriendly.setText(features.toString());
            holder.tvPetFriendly.setVisibility(View.VISIBLE);
        } else {
            holder.tvPetFriendly.setVisibility(View.GONE);
        }
        
        // Ocultar tvAscensor ya que ahora está en tvPetFriendly
        holder.tvAscensor.setVisibility(View.GONE);
    }

    private int resolveEstadoColor(View itemView, String estado) {
        String normalized = estado != null ? estado.toLowerCase() : "";
        if (normalized.contains("entreg")) {
            return ContextCompat.getColor(itemView.getContext(), R.color.inmia_text);
        }
        if (normalized.contains("venta")) {
            return ContextCompat.getColor(itemView.getContext(), R.color.inmia_success);
        }
        if (normalized.contains("preventa")) {
            return ContextCompat.getColor(itemView.getContext(), R.color.inmia_warning);
        }
        if (normalized.contains("planos")) {
            return ContextCompat.getColor(itemView.getContext(), R.color.inmia_info);
        }
        return ContextCompat.getColor(itemView.getContext(), R.color.inmia_teal_dark);
    }

    @Override
    public int getItemCount() {
        return proyectos != null ? proyectos.size() : 0;
    }

    public void setProyectos(List<Proyecto> proyectos) {
        this.proyectos = proyectos;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder que representa cada item de proyecto en la lista
     */
    public class ProyectoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProyecto;
        private TextView tvNombreProyecto;
        private TextView tvUbicacion;
        private TextView tvInmobiliaria;
        private TextView tvEstado;
        private TextView tvArea;
        private TextView tvDormitorios;
        private TextView tvBanos;
        private TextView tvPrecio;
        private TextView tvNumTipologias;
        private TextView tvPetFriendly;
        private TextView tvAscensor;
        private LinearLayout containerItem;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Vincular componentes del layout
            imgProyecto = itemView.findViewById(R.id.imgProyecto);
            tvNombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvInmobiliaria = itemView.findViewById(R.id.tvInmobiliaria);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvArea = itemView.findViewById(R.id.tvArea);
            tvDormitorios = itemView.findViewById(R.id.tvDormitorios);
            tvBanos = itemView.findViewById(R.id.tvBanos);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvNumTipologias = itemView.findViewById(R.id.tvNumTipologias);
            tvPetFriendly = itemView.findViewById(R.id.tvPetFriendly);
            tvAscensor = itemView.findViewById(R.id.tvAscensor);
            containerItem = itemView.findViewById(R.id.containerItem);

            // Click listener para el item completo
            containerItem.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Proyecto proyecto = proyectos.get(position);

                    // Navegar a AdminProyectoDetalleActivity
                    Intent intent = new Intent(context, AdminProyectoDetalleActivity.class);

                    // Pasar información básica (podría extenderse más adelante)
                    intent.putExtra("proyecto_id", proyecto.getId());
                    intent.putExtra("proyecto_nombre", proyecto.getNombre());
                    intent.putExtra("proyecto_ubicacion", proyecto.getUbicacion());

                    context.startActivity(intent);
                }
            });
        }
    }
}

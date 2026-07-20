package com.example.inmia.cliente;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.models.Proyecto;
import com.example.inmia.models.Tipologia;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProyectosAdapter extends RecyclerView.Adapter<ProyectosAdapter.ProyectoViewHolder> {

    private List<Proyecto> listaProyectos;

    public ProyectosAdapter(List<Proyecto> listaProyectos) {
        this.listaProyectos = listaProyectos;
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_proyecto_cliente, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        Proyecto proyecto = listaProyectos.get(position);

        holder.tvNombre.setText(
                proyecto.getNombre() != null ? proyecto.getNombre() : "Sin nombre");
        holder.tvUbicacion.setText(
                proyecto.getUbicacion() != null ? proyecto.getUbicacion() : "Lima");

        List<String> urls = proyecto.getImagenesUrls();
        if (urls != null && !urls.isEmpty() && !urls.get(0).isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(urls.get(0))
                    .placeholder(R.drawable.onboarding1)
                    .error(R.drawable.onboarding1)
                    .centerCrop()
                    .into(holder.imgProyecto);
        } else if (proyecto.getImagenHeroPrincipal() != 0) {
            holder.imgProyecto.setImageResource(proyecto.getImagenHeroPrincipal());
        } else {
            holder.imgProyecto.setImageResource(R.drawable.onboarding1);
        }

        String estado = proyecto.getEstadoProyecto();
        if (estado == null) estado = "Venta";
        holder.tvEstado.setText(estado);
        switch (estado) {
            case "Preventa":
                holder.tvEstado.setBackgroundResource(R.drawable.bg_badge_preventa); break;
            case "Planos":
                holder.tvEstado.setBackgroundResource(R.drawable.bg_badge_planos); break;
            default:
                holder.tvEstado.setBackgroundResource(R.drawable.bg_badge_estado); break;
        }

        List<Tipologia> tipologias = proyecto.getTipologias();
        if (tipologias != null && !tipologias.isEmpty()) {
            double minPrecio = Double.MAX_VALUE;
            for (Tipologia t : tipologias) {
                if (t.getPrecio() != null && !t.getPrecio().isEmpty()) {
                    try {
                        double p = Double.parseDouble(t.getPrecio());
                        if (p < minPrecio) minPrecio = p;
                    } catch (NumberFormatException ignored) {}
                }
            }
            if (minPrecio != Double.MAX_VALUE) {
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                holder.tvPrecio.setText("S/. " + nf.format((long) minPrecio));
            } else {
                holder.tvPrecio.setText("Consultar");
            }
            int total = tipologias.size();
            holder.tvTipologiasCount.setText(
                    total + (total == 1 ? " tipología" : " tipologías"));
        } else {
            holder.tvPrecio.setText("Consultar");
            holder.tvTipologiasCount.setText("Sin tipologías");
        }

        holder.tvRating.setText("--");
        holder.tvTotalValoraciones.setText("");

        if (proyecto.getId() != null && !proyecto.getId().isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("proyectos")
                    .document(proyecto.getId())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (!doc.exists()) return;
                        double promedio = doc.contains("promedioRating")
                                ? doc.getDouble("promedioRating") : 0.0;
                        long totalVal = doc.contains("totalValoraciones")
                                ? doc.getLong("totalValoraciones") : 0L;

                        if (totalVal > 0) {
                            holder.tvRating.setText(
                                    String.format(Locale.US, "%.1f", promedio));
                            holder.tvTotalValoraciones.setText(
                                    "(" + totalVal + (totalVal == 1
                                            ? " reseña)" : " reseñas)"));
                        } else {
                            holder.tvRating.setText("Nuevo");
                            holder.tvTotalValoraciones.setText("");
                        }
                    });
        }

        holder.itemView.setOnClickListener(v -> abrirDetalle(v, proyecto));
        holder.tvVerTipologias.setOnClickListener(v -> abrirDetalle(v, proyecto));
    }

    private void abrirDetalle(View v, Proyecto proyecto) {
        Intent intent = new Intent(v.getContext(), ClienteDetallePropiedadActivity.class);
        intent.putExtra("PROYECTO_ID", proyecto.getId());
        intent.putExtra("PROYECTO_NOMBRE", proyecto.getNombre());
        v.getContext().startActivity(intent);
    }

    @Override
    public int getItemCount() { return listaProyectos.size(); }

    public static class ProyectoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProyecto;
        TextView tvNombre, tvUbicacion, tvEstado, tvPrecio;
        TextView tvTipologiasCount, tvVerTipologias;
        TextView tvRating, tvTotalValoraciones;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProyecto         = itemView.findViewById(R.id.imgProyecto);
            tvNombre            = itemView.findViewById(R.id.tvNombre);
            tvUbicacion         = itemView.findViewById(R.id.tvUbicacion);
            tvEstado            = itemView.findViewById(R.id.tvEstado);
            tvPrecio            = itemView.findViewById(R.id.tvPrecio);
            tvTipologiasCount   = itemView.findViewById(R.id.tvTipologiasCount);
            tvVerTipologias     = itemView.findViewById(R.id.tvVerTipologias);
            tvRating            = itemView.findViewById(R.id.tvRating);
            tvTotalValoraciones = itemView.findViewById(R.id.tvTotalValoraciones);
        }
    }
}
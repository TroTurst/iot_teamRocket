package com.example.inmia.admin;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.CitaAsesor;

import java.util.List;

public class AdminCitasAdapter extends RecyclerView.Adapter<AdminCitasAdapter.CitaViewHolder> {

    private final List<CitaAsesor> citas;

    public AdminCitasAdapter(List<CitaAsesor> citas) {
        this.citas = citas;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita_admin, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        CitaAsesor cita = citas.get(position);
        holder.tvCliente.setText(cita.getCliente());
        holder.tvFecha.setText(cita.getFecha());
        holder.tvHora.setText(cita.getHora());
        holder.tvProyecto.setText(cita.getProyecto());
        holder.tvEstado.setText(cita.getEstado());

        int estadoColor = resolveEstadoColor(holder.itemView, cita.getEstado());
        holder.tvEstado.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.inmia_white));
        holder.tvEstado.setBackgroundTintList(ColorStateList.valueOf(estadoColor));
    }

    private int resolveEstadoColor(View itemView, String estado) {
        String normalized = estado != null ? estado.toLowerCase() : "";
        if (normalized.contains("confirmada")) {
            return ContextCompat.getColor(itemView.getContext(), R.color.inmia_success);
        }
        if (normalized.contains("en proceso")) {
            return ContextCompat.getColor(itemView.getContext(), R.color.inmia_warning);
        }
        if (normalized.contains("completada")) {
            return ContextCompat.getColor(itemView.getContext(), R.color.inmia_info);
        }
        if (normalized.contains("cancelada")) {
            return ContextCompat.getColor(itemView.getContext(), R.color.inmia_danger);
        }
        return ContextCompat.getColor(itemView.getContext(), R.color.inmia_teal_dark);
    }

    @Override
    public int getItemCount() {
        return citas != null ? citas.size() : 0;
    }

    public void setCitas(List<CitaAsesor> nuevasCitas) {
        citas.clear();
        if (nuevasCitas != null) {
            citas.addAll(nuevasCitas);
        }
        notifyItemRangeChanged(0, getItemCount());
    }

    public static class CitaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCliente;
        private final TextView tvFecha;
        private final TextView tvHora;
        private final TextView tvProyecto;
        private final TextView tvEstado;

        CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tvClienteCita);
            tvFecha = itemView.findViewById(R.id.tvFechaCita);
            tvHora = itemView.findViewById(R.id.tvHoraCita);
            tvProyecto = itemView.findViewById(R.id.tvProyectoCita);
            tvEstado = itemView.findViewById(R.id.tvEstadoCita);
        }
    }
}

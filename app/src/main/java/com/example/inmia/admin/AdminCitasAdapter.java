package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    }

    @Override
    public int getItemCount() {
        return citas != null ? citas.size() : 0;
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {
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


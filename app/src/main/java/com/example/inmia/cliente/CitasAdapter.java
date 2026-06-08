package com.example.inmia.cliente;
import android.content.Intent;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.inmia.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import com.example.inmia.models.Cita;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitaViewHolder> {

    private List<Cita> listaCitas;

    public CitasAdapter(List<Cita> listaCitas) {
        this.listaCitas = listaCitas;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = listaCitas.get(position);

        String estadoReal = cita.getEstado() != null ? cita.getEstado() : "PENDIENTE";
        holder.tvStatusCita.setText(estadoReal.toUpperCase());

        holder.tvNameCita.setText(cita.getNombre());
        holder.tvLocationCita.setText(cita.getUbicacion());
        holder.tvCompanyCita.setText(cita.getEmpresa());

        String estadoLimpio = estadoReal.trim().toLowerCase();

        if (estadoLimpio.equals("cancelada") || estadoLimpio.equals("cancelado")) {
            holder.tvStatusCita.setTextColor(Color.parseColor("#FF4C4C"));
        } else if (estadoLimpio.equals("pendiente")) {
            holder.tvStatusCita.setTextColor(Color.parseColor("#FFA000"));
        } else if (estadoLimpio.equals("confirmada") || estadoLimpio.equals("confirmado")) {
            holder.tvStatusCita.setTextColor(Color.parseColor("#2ECC71"));
        } else {
            holder.tvStatusCita.setTextColor(Color.parseColor("#18C0C1"));
        }

        // --- CLIC PARA VER DETALLES ---
        holder.btnDetallesCita.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ClienteDetallesCitaActivity2.class);

            intent.putExtra("CITA_ID", cita.getId());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusCita, tvNameCita, tvLocationCita, tvCompanyCita;
        MaterialButton btnDetallesCita;
        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatusCita = itemView.findViewById(R.id.tvStatusCita);
            tvNameCita = itemView.findViewById(R.id.tvNameCita);
            tvLocationCita = itemView.findViewById(R.id.tvLocationCita);
            tvCompanyCita = itemView.findViewById(R.id.tvCompanyCita);
            btnDetallesCita = itemView.findViewById(R.id.btnDetallesCita);
        }
    }
}
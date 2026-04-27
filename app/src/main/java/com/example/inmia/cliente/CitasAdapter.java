package com.example.inmia.cliente;
import android.content.Intent;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.inmia.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

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

        holder.tvStatusCita.setText(cita.getEstado());
        holder.tvNameCita.setText(cita.getNombre());
        holder.tvLocationCita.setText(cita.getUbicacion());
        holder.tvCompanyCita.setText(cita.getEmpresa());

        if (cita.getEstado().equals("Cancelado")) {
            holder.tvStatusCita.setTextColor(android.graphics.Color.parseColor("#FF4C4C"));
        } else {
            holder.tvStatusCita.setTextColor(android.graphics.Color.parseColor("#18C0C1"));
        }

        holder.btnDetallesCita.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ClienteDetallesCitaActivity2.class);

            intent.putExtra("PROYECTO_NOMBRE", cita.getNombre());

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
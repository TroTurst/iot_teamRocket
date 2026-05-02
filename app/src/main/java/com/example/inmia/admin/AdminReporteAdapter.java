package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

import java.util.List;

public class AdminReporteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int TYPE_MEDIA = 0;
    static final int TYPE_MEJOR = 1;
    static final int TYPE_ESTADO = 2;

    private final List<ReporteItem> items;

    public AdminReporteAdapter(List<ReporteItem> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getTipo();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_MEDIA) {
            View view = inflater.inflate(R.layout.item_reporte_media_asesores, parent, false);
            return new MediaViewHolder(view);
        }
        if (viewType == TYPE_MEJOR) {
            View view = inflater.inflate(R.layout.item_reporte_mejor_asesor, parent, false);
            return new MejorViewHolder(view);
        }
        View view = inflater.inflate(R.layout.item_reporte_estado_inmobiliaria, parent, false);
        return new EstadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReporteItem item = items.get(position);
        if (holder instanceof MediaViewHolder) {
            MediaViewHolder media = (MediaViewHolder) holder;
            media.tvVentas.setText(String.valueOf(item.getMediaVentas()));
            media.tvCitas.setText(String.valueOf(item.getMediaCitas()));
            media.tvGanancias.setText(item.getMediaGanancias());
            return;
        }
        if (holder instanceof MejorViewHolder) {
            MejorViewHolder mejor = (MejorViewHolder) holder;
            mejor.tvNombre.setText(item.getMejorNombre());
            mejor.tvVentas.setText(String.valueOf(item.getMejorVentas()));
            mejor.tvCitas.setText(String.valueOf(item.getMejorCitas()));
            mejor.tvGanancias.setText(item.getMejorGanancias());
            mejor.tvIngresos.setText(item.getMejorGanancias());
            return;
        }
        EstadoViewHolder estado = (EstadoViewHolder) holder;
        estado.tvCasasVendidas.setText(String.valueOf(item.getCasasVendidas()));
        estado.tvAsesoresActivos.setText(String.valueOf(item.getAsesoresActivos()));
        estado.tvPendientes.setText(String.valueOf(item.getPendientes()));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvVentas;
        private final TextView tvCitas;
        private final TextView tvGanancias;

        MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVentas = itemView.findViewById(R.id.tvMediaVentas);
            tvCitas = itemView.findViewById(R.id.tvMediaCitas);
            tvGanancias = itemView.findViewById(R.id.tvMediaGanancias);
        }
    }

    static class MejorViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvVentas;
        private final TextView tvCitas;
        private final TextView tvGanancias;
        private final TextView tvIngresos;

        MejorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvMejorAsesorNombre);
            tvVentas = itemView.findViewById(R.id.tvMejorAsesorVentas);
            tvCitas = itemView.findViewById(R.id.tvMejorAsesorCitas);
            tvGanancias = itemView.findViewById(R.id.tvMejorAsesorGanancias);
            tvIngresos = itemView.findViewById(R.id.tvMejorAsesorIngresos);
        }
    }

    static class EstadoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCasasVendidas;
        private final TextView tvAsesoresActivos;
        private final TextView tvPendientes;

        EstadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCasasVendidas = itemView.findViewById(R.id.tvCasasVendidas);
            tvAsesoresActivos = itemView.findViewById(R.id.tvAsesoresActivos);
            tvPendientes = itemView.findViewById(R.id.tvPendientesCierre);
        }
    }
}

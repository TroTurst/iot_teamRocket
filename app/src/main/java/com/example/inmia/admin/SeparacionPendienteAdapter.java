package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class SeparacionPendienteAdapter extends RecyclerView.Adapter<SeparacionPendienteAdapter.VH> {

    public interface OnAccionClick {
        void onAprobar(SeparacionPendiente item);
        void onRechazar(SeparacionPendiente item);
    }

    private final List<SeparacionPendiente> items = new ArrayList<>();
    private final OnAccionClick listener;

    public SeparacionPendienteAdapter(OnAccionClick listener) {
        this.listener = listener;
    }

    public void update(List<SeparacionPendiente> nuevos) {
        items.clear();
        if (nuevos != null) items.addAll(nuevos);
        notifyDataSetChanged();
    }

    public int size() {
        return items.size();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_separacion_pendiente, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        SeparacionPendiente it = items.get(position);
        h.tvProyecto.setText(it.getNombreProyecto());
        h.tvMonto.setText(it.getMonto());
        h.tvUbicacion.setText(it.getUbicacion());
        h.tvCliente.setText(it.getClienteNombre());
        h.tvFecha.setText(it.getFecha());
        h.tvTipologia.setText(it.getTipologia().isEmpty() ? "Tipología no especificada" : "Tipología: " + it.getTipologia());

        h.btnAprobar.setOnClickListener(v -> {
            if (listener != null) listener.onAprobar(it);
        });
        h.btnRechazar.setOnClickListener(v -> {
            if (listener != null) listener.onRechazar(it);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvProyecto;
        final TextView tvMonto;
        final TextView tvUbicacion;
        final TextView tvCliente;
        final TextView tvFecha;
        final TextView tvTipologia;
        final MaterialButton btnAprobar;
        final MaterialButton btnRechazar;

        VH(@NonNull View v) {
            super(v);
            tvProyecto   = v.findViewById(R.id.tvPendienteProyecto);
            tvMonto      = v.findViewById(R.id.tvPendienteMonto);
            tvUbicacion  = v.findViewById(R.id.tvPendienteUbicacion);
            tvCliente    = v.findViewById(R.id.tvPendienteCliente);
            tvFecha      = v.findViewById(R.id.tvPendienteFecha);
            tvTipologia  = v.findViewById(R.id.tvPendienteTipologia);
            btnAprobar   = v.findViewById(R.id.btnPendienteAprobar);
            btnRechazar  = v.findViewById(R.id.btnPendienteRechazar);
        }
    }
}

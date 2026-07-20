package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReporteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int TYPE_PROYECTO = 100;
    static final int TYPE_ASESOR   = 101;
    static final int TYPE_VACIO    = 102;

    private final List<Object> items = new ArrayList<>();
    private final NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());

    public void setProyectos(List<ReporteProyectoItem> data) {
        items.clear();
        if (data == null || data.isEmpty()) {
            items.add(new Vacio("Sin proyectos en el per\u00edodo seleccionado"));
        } else {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setAsesores(List<ReporteAsesorItem> data) {
        items.clear();
        if (data == null || data.isEmpty()) {
            items.add(new Vacio("Sin asesores con actividad en el per\u00edodo seleccionado"));
        } else {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object o = items.get(position);
        if (o instanceof ReporteProyectoItem) return TYPE_PROYECTO;
        if (o instanceof ReporteAsesorItem)   return TYPE_ASESOR;
        return TYPE_VACIO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROYECTO) {
            return new ProyectoVH(inflater.inflate(R.layout.item_reporte_proyecto, parent, false));
        }
        if (viewType == TYPE_ASESOR) {
            return new AsesorVH(inflater.inflate(R.layout.item_reporte_asesor, parent, false));
        }
        View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new VacioVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object o = items.get(position);
        if (holder instanceof ProyectoVH) {
            ReporteProyectoItem p = (ReporteProyectoItem) o;
            ProyectoVH h = (ProyectoVH) holder;
            h.tvNombre.setText(p.getNombreProyecto());
            h.tvMonto.setText("S/ " + nf.format((long) p.getMontoTotal()));
            String ubicacion = p.getDistrito().isEmpty() ? "Sin distrito" : p.getDistrito();
            h.tvUbicacion.setText(ubicacion);
            String asesores = p.getNumAsesores() == 0
                    ? "Sin asesores asignados"
                    : p.getNumAsesores() + " asesor" + (p.getNumAsesores() == 1 ? "" : "es") + ": " + p.getAsesoresNombres();
            h.tvAsesores.setText(asesores);
            h.tvEtiquetaAsesor.setText(
                    p.isTieneAsesoresAsignados()
                            ? "(los que realmente atendieron en el periodo)"
                            : "(asignados al proyecto, sin actividad en el periodo)");
            h.tvEtiquetaAsesor.setVisibility(View.VISIBLE);
            h.tvAprobadas.setText(String.valueOf(p.getNumAprobadas()));
            h.tvPagadas.setText(String.valueOf(p.getNumPagadas()));
            h.tvPendientes.setText(String.valueOf(p.getNumPendientes()));
            h.tvRechazadas.setText(String.valueOf(p.getNumRechazadas()));
        } else if (holder instanceof AsesorVH) {
            ReporteAsesorItem a = (ReporteAsesorItem) o;
            AsesorVH h = (AsesorVH) holder;
            h.tvNombre.setText(a.getNombreAsesor());
            h.tvMonto.setText("S/ " + nf.format((long) a.getMontoTotal()));
            h.tvZona.setText(a.getZonaTrabajo().isEmpty() ? "Sin zona" : a.getZonaTrabajo());
            String proyectos = a.getNumProyectos() == 0
                    ? "Sin proyectos asignados"
                    : a.getNumProyectos() + " proyecto" + (a.getNumProyectos() == 1 ? "" : "s") + ": " + a.getProyectosNombres();
            h.tvProyectos.setText(proyectos);
            h.tvAprobadas.setText(String.valueOf(a.getNumAprobadas()));
            h.tvPagadas.setText(String.valueOf(a.getNumPagadas()));
            h.tvPendientes.setText(String.valueOf(a.getNumPendientes()));
            h.tvRechazadas.setText(String.valueOf(a.getNumRechazadas()));
            h.tvCitas.setText(String.valueOf(a.getNumCitas()));
        } else if (holder instanceof VacioVH) {
            Vacio v = (Vacio) o;
            VacioVH h = (VacioVH) holder;
            TextView tv = h.itemView.findViewById(android.R.id.text1);
            if (tv != null) {
                tv.setText(v.message);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv.setPadding(0, 32, 0, 32);
                tv.setTextColor(0xFF7A9E9E);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProyectoVH extends RecyclerView.ViewHolder {
        final TextView tvNombre, tvMonto, tvUbicacion, tvAsesores, tvEtiquetaAsesor;
        final TextView tvAprobadas, tvPagadas, tvPendientes, tvRechazadas;

        ProyectoVH(@NonNull View v) {
            super(v);
            tvNombre         = v.findViewById(R.id.tvReporteProyectoNombre);
            tvMonto          = v.findViewById(R.id.tvReporteProyectoMonto);
            tvUbicacion      = v.findViewById(R.id.tvReporteProyectoUbicacion);
            tvAsesores       = v.findViewById(R.id.tvReporteProyectoAsesores);
            tvEtiquetaAsesor = v.findViewById(R.id.tvReporteProyectoEtiquetaAsesor);
            tvAprobadas      = v.findViewById(R.id.tvReporteProyectoAprobadas);
            tvPagadas        = v.findViewById(R.id.tvReporteProyectoPagadas);
            tvPendientes     = v.findViewById(R.id.tvReporteProyectoPendientes);
            tvRechazadas     = v.findViewById(R.id.tvReporteProyectoRechazadas);
        }
    }

    static class AsesorVH extends RecyclerView.ViewHolder {
        final TextView tvNombre, tvMonto, tvZona, tvProyectos;
        final TextView tvAprobadas, tvPagadas, tvPendientes, tvRechazadas, tvCitas;

        AsesorVH(@NonNull View v) {
            super(v);
            tvNombre      = v.findViewById(R.id.tvReporteAsesorNombre);
            tvMonto       = v.findViewById(R.id.tvReporteAsesorMonto);
            tvZona        = v.findViewById(R.id.tvReporteAsesorZona);
            tvProyectos   = v.findViewById(R.id.tvReporteAsesorProyectos);
            tvAprobadas   = v.findViewById(R.id.tvReporteAsesorAprobadas);
            tvPagadas     = v.findViewById(R.id.tvReporteAsesorPagadas);
            tvPendientes  = v.findViewById(R.id.tvReporteAsesorPendientes);
            tvRechazadas  = v.findViewById(R.id.tvReporteAsesorRechazadas);
            tvCitas       = v.findViewById(R.id.tvReporteAsesorCitas);
        }
    }

    static class VacioVH extends RecyclerView.ViewHolder {
        VacioVH(@NonNull View v) { super(v); }
    }

    private static class Vacio {
        final String message;
        Vacio(String m) { this.message = m; }
    }
}

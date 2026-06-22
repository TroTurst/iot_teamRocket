package com.example.inmia.superadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter de logs con dos tipos de fila:
 *   - Encabezado de fecha (un String, ej. "24 de mayo")
 *   - Evento de log (un objeto Log)
 * La lista mezcla ambos: cada día con logs lleva su encabezado seguido de sus eventos.
 */
public class LogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_HEADER = 0;
    private static final int TIPO_LOG    = 1;

    private final Context context;
    private List<Object> items;

    public LogAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items   = items != null ? items : new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof Log ? TIPO_LOG : TIPO_HEADER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TIPO_HEADER) {
            View v = inflater.inflate(R.layout.item_log_fecha, parent, false);
            return new HeaderViewHolder(v);
        }
        View v = inflater.inflate(R.layout.item_log_evento, parent, false);
        return new LogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvFechaHeader.setText((String) item);
            return;
        }

        Log log = (Log) item;
        LogViewHolder h = (LogViewHolder) holder;
        h.tvDescripcion.setText(log.getDescripcion());
        h.tvFecha.setText(log.getFecha());

        // Ícono y color según tipo
        String tipo = log.getTipo() != null ? log.getTipo() : "";
        switch (tipo) {
            case Log.TIPO_ESTADO_CUENTA:
                h.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_estado);
                h.imgIcono.setImageResource(R.drawable.sa_ic_log_estado);
                break;
            case Log.TIPO_PROYECTO:
                h.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_proyecto);
                h.imgIcono.setImageResource(R.drawable.sa_ic_log_proyecto);
                break;
            case Log.TIPO_SOLICITUD:
                h.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_solicitud);
                h.imgIcono.setImageResource(R.drawable.sa_ic_log_solicitud);
                break;
            case Log.TIPO_CITA:
                h.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_cita);
                h.imgIcono.setImageResource(R.drawable.sa_ic_log_cita);
                break;
            case Log.TIPO_SEPARACION:
                h.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_separacion);
                h.imgIcono.setImageResource(R.drawable.sa_ic_log_separacion);
                break;
            case Log.TIPO_CUENTA:
            default:
                h.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_cuenta);
                h.imgIcono.setImageResource(R.drawable.sa_ic_log_cuenta);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void actualizarLista(List<Object> nuevaLista) {
        this.items = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    // ── ViewHolders ──
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaHeader;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaHeader = itemView.findViewById(R.id.tvFechaHeader);
        }
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        FrameLayout frameIcono;
        ImageView imgIcono;
        TextView tvDescripcion, tvFecha;
        LogViewHolder(@NonNull View itemView) {
            super(itemView);
            frameIcono    = itemView.findViewById(R.id.frameIcono);
            imgIcono      = itemView.findViewById(R.id.imgIcono);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha       = itemView.findViewById(R.id.tvFecha);
        }
    }
}

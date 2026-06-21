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

import java.util.List;

public class LogAdapter extends
        RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private final Context context;
    private List<Log> listaLogs;

    public LogAdapter(Context context, List<Log> listaLogs) {
        this.context   = context;
        this.listaLogs = listaLogs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                            int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_log_evento, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder,
                                 int position) {
        Log log = listaLogs.get(position);

        holder.tvDescripcion.setText(log.getDescripcion());
        holder.tvFecha.setText(log.getFecha());

        // Ícono y color según tipo
        String tipo = log.getTipo() != null ? log.getTipo() : "";
        switch (tipo) {
            case Log.TIPO_ESTADO_CUENTA:
                holder.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_estado);
                holder.imgIcono.setImageResource(R.drawable.sa_ic_log_estado);
                break;

            case Log.TIPO_PROYECTO:
                holder.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_proyecto);
                holder.imgIcono.setImageResource(R.drawable.sa_ic_log_proyecto);
                break;

            case Log.TIPO_SOLICITUD:
                holder.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_solicitud);
                holder.imgIcono.setImageResource(R.drawable.sa_ic_log_solicitud);
                break;

            case Log.TIPO_CITA:
                holder.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_cita);
                holder.imgIcono.setImageResource(R.drawable.sa_ic_log_cita);
                break;

            case Log.TIPO_SEPARACION:
                holder.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_separacion);
                holder.imgIcono.setImageResource(R.drawable.sa_ic_log_separacion);
                break;

            case Log.TIPO_CUENTA:
            default:
                holder.frameIcono.setBackgroundResource(R.drawable.sa_bg_log_cuenta);
                holder.imgIcono.setImageResource(R.drawable.sa_ic_log_cuenta);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listaLogs.size();
    }

    // Actualizar lista al aplicar filtro de fecha
    public void actualizarLista(List<Log> nuevaLista) {
        this.listaLogs = nuevaLista;
        notifyDataSetChanged();
    }

    // ViewHolder
    public static class LogViewHolder extends RecyclerView.ViewHolder {

        FrameLayout frameIcono;
        ImageView imgIcono;
        TextView tvDescripcion, tvFecha;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            frameIcono    = itemView.findViewById(R.id.frameIcono);
            imgIcono      = itemView.findViewById(R.id.imgIcono);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha       = itemView.findViewById(R.id.tvFecha);
        }
    }
}
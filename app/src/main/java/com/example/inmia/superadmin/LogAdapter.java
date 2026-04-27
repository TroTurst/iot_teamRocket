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
        switch (log.getTipo()) {
            case Log.TIPO_USUARIO:
                holder.frameIcono.setBackgroundResource(
                        R.drawable.sa_bg_log_usuario);
                holder.imgIcono.setImageResource(
                        R.drawable.sa_ic_log_usuario);
                break;

            case Log.TIPO_ADMIN:
                holder.frameIcono.setBackgroundResource(
                        R.drawable.sa_bg_log_admin);
                holder.imgIcono.setImageResource(
                        R.drawable.sa_ic_log_admin);
                break;

            case Log.TIPO_RESERVA:
                holder.frameIcono.setBackgroundResource(
                        R.drawable.sa_bg_log_reserva);
                holder.imgIcono.setImageResource(
                        R.drawable.sa_ic_log_reserva);
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
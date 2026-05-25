package com.example.inmia.superadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.superadmin.db.NotificacionSAEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificacionSAAdapter extends
        RecyclerView.Adapter<NotificacionSAAdapter.ViewHolder> {

    private final Context context;
    private final List<NotificacionSAEntity> lista;
    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public NotificacionSAAdapter(Context context, List<NotificacionSAEntity> lista) {
        this.context = context;
        this.lista   = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.sa_item_notificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificacionSAEntity n = lista.get(position);

        holder.tvTitulo.setText(n.titulo);
        holder.tvDescripcion.setText(n.descripcion);
        holder.tvFecha.setText(SDF.format(new Date(n.timestamp)));

        // Icono según tipo
        int iconRes;
        switch (n.tipo) {
            case NotificacionHelper.TIPO_ADMIN_CREADO:
                iconRes = R.drawable.ic_person;
                break;
            case NotificacionHelper.TIPO_ASESOR_HABILITADO:
                iconRes = R.drawable.ic_check;
                break;
            case NotificacionHelper.TIPO_ASESOR_RECHAZADO:
                iconRes = R.drawable.ic_close;
                break;
            case NotificacionHelper.TIPO_USUARIO_ACTIVADO:
                iconRes = R.drawable.ic_check;
                break;
            case NotificacionHelper.TIPO_USUARIO_DESACTIVADO:
                iconRes = R.drawable.ic_close;
                break;
            default:
                iconRes = R.drawable.ic_notifications;
        }
        holder.ivIcono.setImageResource(iconRes);

        // Fondo: no leída → ligeramente resaltada
        holder.itemView.setAlpha(n.leida ? 0.65f : 1.0f);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcono;
        TextView  tvTitulo, tvDescripcion, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcono      = itemView.findViewById(R.id.ivIconoNotif);
            tvTitulo     = itemView.findViewById(R.id.tvTituloNotif);
            tvDescripcion = itemView.findViewById(R.id.tvDescNotif);
            tvFecha      = itemView.findViewById(R.id.tvFechaNotif);
        }
    }
}

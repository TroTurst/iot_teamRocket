package com.example.inmia.superadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Solicitud;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SolicitudAdapter extends
        RecyclerView.Adapter<SolicitudAdapter.SolicitudViewHolder> {

    private final Context context;
    private final List<Solicitud> listaSolicitudes;

    // Interfaz para acciones
    public interface OnSolicitudListener {
        void onHabilitar(Solicitud solicitud, int position);
        void onRechazar(Solicitud solicitud, int position);
        void onVerPerfil(Solicitud solicitud);
    }

    private final OnSolicitudListener listener;

    public SolicitudAdapter(Context context,
                            List<Solicitud> listaSolicitudes,
                            OnSolicitudListener listener) {
        this.context          = context;
        this.listaSolicitudes = listaSolicitudes;
        this.listener         = listener;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_solicitud_asesor, parent, false);
        return new SolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder,
                                 int position) {
        Solicitud solicitud = listaSolicitudes.get(position);

        // Datos
        holder.tvAvatar.setText(solicitud.getIniciales());
        holder.tvNombre.setText(solicitud.getNombre());
        holder.tvInmobiliaria.setText(solicitud.getInmobiliaria());
        holder.tvTiempoEspera.setText(solicitud.getTiempoEspera());
        holder.tvCorreo.setText(solicitud.getCorreo());
        holder.tvTelefono.setText(solicitud.getTelefono());

        // Botón Ver perfil
        holder.btnVerPerfil.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerPerfil(solicitud);
            }
        });

        // Botón Habilitar
        holder.btnHabilitar.setOnClickListener(v ->
            DialogHelper.mostrarDialogoAccion(
                context,
                "¿Habilitar asesor?",
                "¿Estás seguro de habilitar a " + solicitud.getNombre() + " como asesor de ventas?",
                "Habilitar",
                "Cancelar",
                R.color.inmia_success,
                R.drawable.bg_badge_teal,
                () -> {
                    if (listener != null) {
                        listener.onHabilitar(solicitud, holder.getAdapterPosition());
                    }
                }
            )
        );

        // Botón Rechazar
        holder.btnRechazar.setOnClickListener(v ->
            DialogHelper.mostrarDialogoAccion(
                context,
                "¿Rechazar solicitud?",
                "¿Estás seguro de rechazar la solicitud de " + solicitud.getNombre() + "? Esta acción no se puede deshacer.",
                "Rechazar",
                "Cancelar",
                R.color.inmia_danger,
                R.drawable.bg_badge_red_circle,
                () -> {
                    if (listener != null) {
                        listener.onRechazar(solicitud, holder.getAdapterPosition());
                    }
                }
            )
        );
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    // Eliminar ítem de la lista
    public void eliminarItem(int position) {
        listaSolicitudes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaSolicitudes.size());
    }

    // ViewHolder
    public static class SolicitudViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvAvatar, tvNombre, tvInmobiliaria;
        TextView tvTiempoEspera, tvCorreo, tvTelefono;
        MaterialButton btnVerPerfil, btnHabilitar, btnRechazar;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar       = itemView.findViewById(R.id.tvAvatar);
            tvNombre       = itemView.findViewById(R.id.tvNombre);
            tvInmobiliaria = itemView.findViewById(R.id.tvInmobiliaria);
            tvTiempoEspera = itemView.findViewById(R.id.tvTiempoEspera);
            tvCorreo       = itemView.findViewById(R.id.tvCorreo);
            tvTelefono     = itemView.findViewById(R.id.tvTelefono);
            btnVerPerfil   = itemView.findViewById(R.id.btnVerPerfil);
            btnHabilitar   = itemView.findViewById(R.id.btnHabilitar);
            btnRechazar    = itemView.findViewById(R.id.btnRechazar);
        }
    }
}
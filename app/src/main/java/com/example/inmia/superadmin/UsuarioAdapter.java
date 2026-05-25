package com.example.inmia.superadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Usuario;
import com.example.inmia.superadmin.NotificacionHelper;

import java.util.List;

public class UsuarioAdapter extends
        RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private final Context context;
    private List<Usuario> listaUsuarios;

    // Interfaz para el click en Ver perfil
    public interface OnVerPerfilListener {
        void onVerPerfil(Usuario usuario);
    }

    private final OnVerPerfilListener listener;

    public UsuarioAdapter(Context context,
                          List<Usuario> listaUsuarios,
                          OnVerPerfilListener listener) {
        this.context       = context;
        this.listaUsuarios = listaUsuarios;
        this.listener      = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_usuario_superadmin, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder,
                                 int position) {
        Usuario usuario = listaUsuarios.get(position);

        // Datos
        holder.tvAvatar.setText(usuario.getIniciales());
        holder.tvNombre.setText(usuario.getNombre());
        holder.tvEmpresa.setText(usuario.getEmpresa());

        // Badge estado
        if (usuario.isActivo()) {
            holder.tvEstado.setText("Activo");
            holder.tvEstado.setTextColor(
                    context.getColor(android.R.color.holo_green_dark));
            holder.tvEstado.setBackgroundResource(
                    R.drawable.badge_activo_superadmin);
        } else {
            holder.tvEstado.setText("Inactivo");
            holder.tvEstado.setTextColor(
                    context.getColor(android.R.color.holo_red_dark));
            holder.tvEstado.setBackgroundResource(
                    R.drawable.badge_inactivo_superadmin);
        }

        // Switch — sin listener primero para evitar disparos al hacer bind
        holder.switchEstado.setOnCheckedChangeListener(null);
        holder.switchEstado.setChecked(usuario.isActivo());

        // Switch listener con AlertDialog
        holder.switchEstado.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    String titulo  = isChecked ? "¿Habilitar usuario?"
                            : "¿Inhabilitar usuario?";
                    String mensaje = isChecked
                            ? "¿Estás seguro de habilitar a "
                            + usuario.getNombre() + "?"
                            : "¿Estás seguro de inhabilitar a "
                            + usuario.getNombre() + "?";
                    String btnOk   = isChecked ? "Habilitar" : "Inhabilitar";

                    new AlertDialog.Builder(context)
                            .setTitle(titulo)
                            .setMessage(mensaje)
                            .setPositiveButton(btnOk, (dialog, which) -> {
                                usuario.setActivo(isChecked);
                                notifyItemChanged(position);

                                // Notificación al activar / desactivar
                                String tipoNotif = isChecked
                                        ? NotificacionHelper.TIPO_USUARIO_ACTIVADO
                                        : NotificacionHelper.TIPO_USUARIO_DESACTIVADO;
                                String textoNotif = isChecked
                                        ? usuario.getNombre() + " ha sido activado."
                                        : usuario.getNombre() + " ha sido desactivado.";
                                NotificacionHelper.enviar(
                                        context,
                                        isChecked ? "Usuario activado" : "Usuario desactivado",
                                        textoNotif,
                                        tipoNotif
                                );
                            })
                            .setNegativeButton("Cancelar", (dialog, which) -> {
                                // Revertir switch sin disparar listener
                                holder.switchEstado
                                        .setOnCheckedChangeListener(null);
                                holder.switchEstado.setChecked(!isChecked);
                                // Re-asignar listener
                                onBindViewHolder(holder, position);
                            })
                            .setCancelable(false)
                            .show();
                });

        // Ver perfil
        holder.layoutVerPerfil.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerPerfil(usuario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    // ── Actualizar lista cuando cambia el tab ──
    public void actualizarLista(List<Usuario> nuevaLista) {
        this.listaUsuarios = nuevaLista;
        notifyDataSetChanged();
    }

    // ── ViewHolder ──
    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {

        TextView tvAvatar, tvNombre, tvEmpresa, tvEstado;
        Switch switchEstado;
        LinearLayout layoutVerPerfil;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar       = itemView.findViewById(R.id.tvAvatar);
            tvNombre       = itemView.findViewById(R.id.tvNombreUsuario);
            tvEmpresa      = itemView.findViewById(R.id.tvEmpresaUsuario);
            tvEstado       = itemView.findViewById(R.id.tvEstadoUsuario);
            switchEstado   = itemView.findViewById(R.id.switchEstado);
            layoutVerPerfil = itemView.findViewById(R.id.layoutVerPerfil);
        }
    }
}
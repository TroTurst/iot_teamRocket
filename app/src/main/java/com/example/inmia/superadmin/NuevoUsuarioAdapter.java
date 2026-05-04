package com.example.inmia.superadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Usuario;

import java.util.List;

public class NuevoUsuarioAdapter extends
        RecyclerView.Adapter<NuevoUsuarioAdapter.NuevoUsuarioViewHolder> {

    private final Context context;
    private final List<Usuario> listaUsuarios;

    public NuevoUsuarioAdapter(Context context,
                               List<Usuario> listaUsuarios) {
        this.context       = context;
        this.listaUsuarios = listaUsuarios;
    }

    @NonNull
    @Override
    public NuevoUsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.sa_item_nuevo_usuario, parent, false);
        return new NuevoUsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NuevoUsuarioViewHolder holder,
                                 int position) {
        Usuario usuario = listaUsuarios.get(position);

        // Avatar
        holder.tvAvatar.setText(usuario.getIniciales());

        // Nombre
        holder.tvNombre.setText(usuario.getNombre());

        // Empresa + rol concatenados
        String empresaRol = usuario.getRol() + " · " + usuario.getEmpresa();
        holder.tvEmpresaRol.setText(empresaRol);

        // Tiempo de registro
        holder.tvTiempoRegistro.setText(usuario.getTiempoRegistro());

        // Badge de rol — color según tipo
        holder.tvBadgeRol.setText(capitalizar(usuario.getRol()));
        switch (usuario.getRol()) {
            case "admin":
                holder.tvBadgeRol.setTextColor(
                        context.getColor(R.color.inmia_teal_dark));
                holder.tvBadgeRol.setBackgroundResource(
                        R.drawable.badge_activo_superadmin);
                break;
            case "asesor":
                holder.tvBadgeRol.setTextColor(
                        context.getColor(R.color.inmia_info));
                holder.tvBadgeRol.setBackgroundResource(
                        R.drawable.badge_activo_superadmin);
                break;
            case "cliente":
                holder.tvBadgeRol.setTextColor(
                        context.getColor(R.color.inmia_warning));
                holder.tvBadgeRol.setBackgroundResource(
                        R.drawable.badge_activo_superadmin);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase()
                + texto.substring(1);
    }

    // ViewHolder
    public static class NuevoUsuarioViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvAvatar, tvNombre, tvEmpresaRol;
        TextView tvTiempoRegistro, tvBadgeRol;

        public NuevoUsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar          = itemView.findViewById(R.id.tvAvatar);
            tvNombre          = itemView.findViewById(R.id.tvNombre);
            tvEmpresaRol      = itemView.findViewById(R.id.tvEmpresaRol);
            tvTiempoRegistro  = itemView.findViewById(R.id.tvTiempoRegistro);
            tvBadgeRol        = itemView.findViewById(R.id.tvBadgeRol);
        }
    }
}
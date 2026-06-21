package com.example.inmia.superadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Log;
import com.example.inmia.models.Usuario;
import com.example.inmia.superadmin.NotificacionHelper;
import com.example.inmia.util.LogHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UsuarioAdapter extends
        RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private final Context context;
    private List<Usuario> listaUsuarios;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        // Switch listener
        holder.switchEstado.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    String titulo   = isChecked ? "¿Habilitar usuario?" : "¿Inhabilitar usuario?";
                    String mensaje  = isChecked
                            ? "¿Estás seguro de habilitar a " + usuario.getNombre() + "?"
                            : "¿Estás seguro de inhabilitar a " + usuario.getNombre() + "?";
                    String btnOk    = isChecked ? "Habilitar" : "Inhabilitar";
                    int colorBtn    = isChecked ? R.color.inmia_success : R.color.inmia_danger;
                    int iconoFondo  = isChecked ? R.drawable.bg_badge_teal : R.drawable.bg_badge_red_circle;

                    DialogHelper.mostrarDialogoAccion(
                        context,
                        titulo,
                        mensaje,
                        btnOk,
                        "Cancelar",
                        colorBtn,
                        iconoFondo,
                        () -> {
                            usuario.setActivo(isChecked);
                            notifyItemChanged(position);
                            // Persistir cambio en Firestore
                            String uid = usuario.getUid();
                            if (uid != null && !uid.isEmpty()) {
                                db.collection("usuarios").document(uid)
                                    .update("activo", isChecked)
                                    .addOnFailureListener(e -> {
                                        // Revertir si falla la escritura en Firestore
                                        usuario.setActivo(!isChecked);
                                        notifyItemChanged(holder.getAdapterPosition());
                                    });
                            }
                            LogHelper.registrar(
                                    (isChecked ? "Se activó la cuenta de " : "Se desactivó la cuenta de ")
                                            + usuario.getNombre(),
                                    Log.TIPO_ESTADO_CUENTA,
                                    LogHelper.ROL_SUPERADMIN,
                                    usuario.getNombre(),
                                    usuario.getUid() != null ? usuario.getUid() : "");
                        },
                        () -> {
                            holder.switchEstado.setOnCheckedChangeListener(null);
                            holder.switchEstado.setChecked(!isChecked);
                            onBindViewHolder(holder, position);
                        }
                    );
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
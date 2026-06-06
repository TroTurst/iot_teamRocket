package com.example.inmia.cliente;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.asesor.ChatThread;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatThread> listaChats;

    public ChatAdapter(List<ChatThread> listaChats) {
        this.listaChats = listaChats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_thread, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatThread chat = listaChats.get(position);

        holder.tvNombre.setText(chat.getName());
        holder.tvUltimoMensaje.setText(chat.getLastMessage());
        holder.tvHora.setText(chat.getTime());

        if (chat.getFotoUrl() != null && !chat.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(chat.getFotoUrl())
                    .placeholder(chat.getAvatarResId())
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(chat.getAvatarResId());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ClienteChatActivity.class);
            intent.putExtra("CHAT_ID", chat.getId());
            intent.putExtra("ASESOR_NOMBRE", chat.getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaChats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView imgAvatar;
        TextView tvNombre, tvUltimoMensaje, tvHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvNombre = itemView.findViewById(R.id.tvName);
            tvUltimoMensaje = itemView.findViewById(R.id.tvLastMessage);
            tvHora = itemView.findViewById(R.id.tvTime);
        }
    }
}
package com.example.inmia.asesor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatThreadAdapter extends RecyclerView.Adapter<ChatThreadAdapter.ChatThreadViewHolder> {

    public interface Listener {
        void onChatSelected(ChatThread thread);
        void onChatDeleteRequested(ChatThread thread);
    }

    private final List<ChatThread> allThreads;
    private final List<ChatThread> visibleThreads;
    private final Listener listener;

    public ChatThreadAdapter(List<ChatThread> threads, Listener listener) {
        this.allThreads = new ArrayList<>(threads);
        this.visibleThreads = new ArrayList<>(threads);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_chat_thread, parent, false);
        return new ChatThreadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatThreadViewHolder holder, int position) {
        ChatThread thread = visibleThreads.get(position);
        holder.name.setText(thread.getName());
        holder.lastMessage.setText(thread.getLastMessage());
        holder.time.setText(thread.getTime());
        holder.avatar.setImageResource(thread.getAvatarResId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatSelected(thread);
            }
        });

        holder.more.setOnClickListener(v -> showMenu(v, thread));
    }

    @Override
    public int getItemCount() {
        return visibleThreads.size();
    }

    public void filterByName(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.US);
        visibleThreads.clear();
        if (normalized.isEmpty()) {
            visibleThreads.addAll(allThreads);
        } else {
            for (ChatThread thread : allThreads) {
                if (thread.getName().toLowerCase(Locale.US).contains(normalized)) {
                    visibleThreads.add(thread);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void deleteThreadById(String id) {
        removeThreadById(id);
    }

    private void showMenu(View anchor, ChatThread thread) {
        PopupMenu menu = new PopupMenu(anchor.getContext(), anchor);
        menu.inflate(R.menu.menu_chat_thread);
        menu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                if (listener != null) {
                    listener.onChatDeleteRequested(thread);
                }
                return true;
            }
            return false;
        });
        menu.show();
    }

    private void removeThreadById(String id) {
        int visibleIndex = indexOfById(visibleThreads, id);
        if (visibleIndex != -1) {
            visibleThreads.remove(visibleIndex);
        }
        int allIndex = indexOfById(allThreads, id);
        if (allIndex != -1) {
            allThreads.remove(allIndex);
        }
        notifyDataSetChanged();
    }

    private int indexOfById(List<ChatThread> threads, String id) {
        for (int i = 0; i < threads.size(); i++) {
            if (threads.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    static class ChatThreadViewHolder extends RecyclerView.ViewHolder {
        final ImageView avatar;
        final TextView name;
        final TextView lastMessage;
        final TextView time;
        final ImageView more;

        ChatThreadViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgAvatar);
            name = itemView.findViewById(R.id.tvName);
            lastMessage = itemView.findViewById(R.id.tvLastMessage);
            time = itemView.findViewById(R.id.tvTime);
            more = itemView.findViewById(R.id.btnMore);
        }
    }
}

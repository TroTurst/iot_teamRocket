package com.example.inmia.asesor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_INCOMING = 0;
    private static final int TYPE_OUTGOING = 1;

    private final List<ChatMessage> items;

    public ChatMessageAdapter(List<ChatMessage> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isOutgoing() ? TYPE_OUTGOING : TYPE_INCOMING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_OUTGOING) {
            View view = inflater.inflate(R.layout.item_chat_message_outgoing, parent, false);
            return new OutgoingViewHolder(view);
        }
        View view = inflater.inflate(R.layout.item_chat_message_incoming, parent, false);
        return new IncomingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = items.get(position);
        if (holder instanceof IncomingViewHolder) {
            ((IncomingViewHolder) holder).bind(message);
        } else if (holder instanceof OutgoingViewHolder) {
            ((OutgoingViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class IncomingViewHolder extends RecyclerView.ViewHolder {
        final TextView message;
        final TextView time;

        IncomingViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tvMessage);
            time = itemView.findViewById(R.id.tvTime);
        }

        void bind(ChatMessage item) {
            message.setText(item.getMessage());
            time.setText(item.getTime());
        }
    }

    static class OutgoingViewHolder extends RecyclerView.ViewHolder {
        final TextView message;
        final TextView time;

        OutgoingViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tvMessage);
            time = itemView.findViewById(R.id.tvTime);
        }

        void bind(ChatMessage item) {
            message.setText(item.getMessage());
            time.setText(item.getTime());
        }
    }
}

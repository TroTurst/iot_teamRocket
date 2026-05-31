package com.example.inmia.superadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inmia.R;
import com.example.inmia.models.Usuario;

import java.util.List;

public class UsuariosPagerAdapter extends RecyclerView.Adapter<UsuariosPagerAdapter.PageHolder> {

    private final Context context;
    private final UsuarioAdapter[] pageAdapters = new UsuarioAdapter[3];

    @SuppressWarnings("unchecked")
    public UsuariosPagerAdapter(Context context,
                                List<Usuario> admins,
                                List<Usuario> asesores,
                                List<Usuario> clientes,
                                UsuarioAdapter.OnVerPerfilListener listener) {
        this.context = context;
        pageAdapters[0] = new UsuarioAdapter(context, admins, listener);
        pageAdapters[1] = new UsuarioAdapter(context, asesores, listener);
        pageAdapters[2] = new UsuarioAdapter(context, clientes, listener);
    }

    @NonNull
    @Override
    public PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.page_recycler_view, parent, false);
        return new PageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageHolder holder, int position) {
        holder.recycler.setLayoutManager(new LinearLayoutManager(context));
        holder.recycler.setAdapter(pageAdapters[position]);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public UsuarioAdapter getPageAdapter(int position) {
        return pageAdapters[position];
    }

    static class PageHolder extends RecyclerView.ViewHolder {
        RecyclerView recycler;

        PageHolder(@NonNull View itemView) {
            super(itemView);
            recycler = itemView.findViewById(R.id.recyclerPage);
        }
    }
}

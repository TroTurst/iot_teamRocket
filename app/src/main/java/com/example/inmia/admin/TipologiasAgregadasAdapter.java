package com.example.inmia.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inmia.R;
import com.example.inmia.models.Tipologia;

import java.util.List;

public class TipologiasAgregadasAdapter extends RecyclerView.Adapter<TipologiasAgregadasAdapter.TipologiaViewHolder> {

    private final List<Tipologia> tipologias;
    private final OnTipologiaRemoveListener removeListener;
    private final OnTipologiaEditListener editListener;
    private final OnTipologiaAddFotoListener addFotoListener;
    private final OnTipologiaRemoveFotoListener removeFotoListener;

    public interface OnTipologiaRemoveListener {
        void onRemove(Tipologia tipologia);
    }

    public interface OnTipologiaEditListener {
        void onEdit(Tipologia tipologia, int position);
    }

    public interface OnTipologiaAddFotoListener {
        void onAddFoto(Tipologia tipologia, int position);
    }

    public interface OnTipologiaRemoveFotoListener {
        void onRemoveFoto(Tipologia tipologia, int fotoIndex);
    }

    public TipologiasAgregadasAdapter(List<Tipologia> tipologias,
                                      OnTipologiaRemoveListener removeListener,
                                      OnTipologiaEditListener editListener,
                                      OnTipologiaAddFotoListener addFotoListener,
                                      OnTipologiaRemoveFotoListener removeFotoListener) {
        this.tipologias = tipologias;
        this.removeListener = removeListener;
        this.editListener = editListener;
        this.addFotoListener = addFotoListener;
        this.removeFotoListener = removeFotoListener;
    }

    @NonNull
    @Override
    public TipologiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tipologia_agregada, parent, false);
        return new TipologiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipologiaViewHolder holder, int position) {
        Tipologia tipologia = tipologias.get(position);
        holder.bind(tipologia, position);
    }

    @Override
    public int getItemCount() {
        return tipologias.size();
    }

    class TipologiaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgThumb;
        private final TextView tvNombre, tvDetalles, tvPrecio;
        private final ImageButton btnEliminar;
        private final RecyclerView rvFotos;
        private final View btnAddFoto;

        TipologiaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgTipologiaThumb);
            tvNombre = itemView.findViewById(R.id.tvNombreTipologiaAgregada);
            tvDetalles = itemView.findViewById(R.id.tvDetallesTipologiaAgregada);
            tvPrecio = itemView.findViewById(R.id.tvPrecioTipologiaAgregada);
            btnEliminar = itemView.findViewById(R.id.btnEliminarTipologia);
            rvFotos = itemView.findViewById(R.id.rvTipologiaFotos);
            btnAddFoto = itemView.findViewById(R.id.btnAgregarFotoTipologia);
        }

        void bind(Tipologia tipologia, int position) {
            tvNombre.setText(tipologia.getNombre());
            tvDetalles.setText(tipologia.getArea() + " · " + tipologia.getDormitorios() + "d · " + tipologia.getBanos() + "b");
            tvPrecio.setText(tipologia.getPrecio());

            // Thumbnail: show first photo or placeholder
            List<String> urls = tipologia.getImagenesUrls();
            if (urls != null && !urls.isEmpty() && urls.get(0) != null && !urls.get(0).isEmpty()) {
                Glide.with(itemView.getContext()).load(urls.get(0))
                        .centerCrop().placeholder(R.drawable.ic_add).into(imgThumb);
            } else {
                imgThumb.setImageResource(R.drawable.ic_add);
            }

            // Tipología photos row
            if (urls != null && !urls.isEmpty()) {
                rvFotos.setVisibility(View.VISIBLE);
                rvFotos.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
                rvFotos.setAdapter(new FotosTipologiaAdapter(urls, (idx) -> {
                    if (removeFotoListener != null) {
                        removeFotoListener.onRemoveFoto(tipologia, idx);
                    }
                }));
            } else {
                rvFotos.setVisibility(View.GONE);
            }

            btnEliminar.setOnClickListener(v -> {
                if (removeListener != null) removeListener.onRemove(tipologia);
            });

            btnAddFoto.setOnClickListener(v -> {
                if (addFotoListener != null) addFotoListener.onAddFoto(tipologia, position);
            });

            itemView.setOnClickListener(v -> {
                if (editListener != null) editListener.onEdit(tipologia, position);
            });
        }
    }

    static class FotosTipologiaAdapter extends RecyclerView.Adapter<FotosTipologiaAdapter.FotoViewHolder> {
        private final List<String> urls;
        private final OnFotoRemoveListener listener;

        interface OnFotoRemoveListener { void onRemove(int index); }

        FotosTipologiaAdapter(List<String> urls, OnFotoRemoveListener listener) {
            this.urls = urls;
            this.listener = listener;
        }

        @NonNull @Override
        public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_galeria_foto, parent, false);
            return new FotoViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
            String url = urls.get(position);
            if (url != null && !url.isEmpty()) {
                Glide.with(holder.itemView.getContext()).load(url)
                        .centerCrop().placeholder(R.drawable.ic_add).into(holder.img);
            }
            holder.imgRemove.setVisibility(View.VISIBLE);
            holder.imgRemove.setOnClickListener(v -> listener.onRemove(holder.getAdapterPosition()));
        }

        @Override public int getItemCount() { return urls.size(); }

        static class FotoViewHolder extends RecyclerView.ViewHolder {
            ImageView img, imgRemove;
            FotoViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.imgGaleriaThumb);
                imgRemove = itemView.findViewById(R.id.imgGaleriaRemove);
            }
        }
    }
}

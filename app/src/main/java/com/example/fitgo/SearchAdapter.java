package com.example.fitgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Adapter para búsqueda de ejercicios.
 * Muestra nombre, grupo muscular y GIF animado.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final List<Exercise> originalList;
    private final List<Exercise> filteredList;
    private final Consumer<Exercise> onSelect;

    public SearchAdapter(List<Exercise> list, Consumer<Exercise> onSelect) {
        this.originalList = list;
        this.filteredList = new ArrayList<>(list);
        this.onSelect = onSelect;
    }

    public void filter(String query) {
        filteredList.clear();
        String q = query.toLowerCase();
        for (Exercise e : originalList) {
            String name = e.getNameEs() != null ? e.getNameEs() : e.getName();
            if (name.toLowerCase().contains(q)) {
                filteredList.add(e);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        Exercise e = filteredList.get(position);
        holder.tvName.setText(e.getNameEs() != null ? e.getNameEs() : e.getName());
        holder.tvTarget.setText(e.getTargetEs() != null ? e.getTargetEs() : e.getTarget());

        String gifUrl = e.getGifUrl();
        if (gifUrl != null && !gifUrl.isEmpty()) {
            Glide.with(holder.imgGif.getContext())
                    .asGif()
                    .load(gifUrl)
                    .into(holder.imgGif);
        } else {
            holder.imgGif.setImageResource(R.drawable.logo); // Usa uno tuyo si falta
        }

        holder.itemView.setOnClickListener(v -> onSelect.accept(e));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGif;
        TextView tvName, tvTarget;

        ViewHolder(View itemView) {
            super(itemView);
            imgGif = itemView.findViewById(R.id.imgGif);
            tvName = itemView.findViewById(R.id.tvNameEs);
            tvTarget = itemView.findViewById(R.id.tvTargetEs);
        }
    }
}

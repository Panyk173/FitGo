package com.example.fitgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

/**
 * Adapter para la lista de ejercicios, con soporte para "quitar" mediante un callback.
 */
public class ExerciseAdapter
        extends ListAdapter<Exercise, ExerciseAdapter.VH> {

    public interface RemoveListener {
        void onRemove(int position);
    }

    private final RemoveListener removeListener;

    public ExerciseAdapter(RemoveListener removeListener) {
        super(DIFF);
        this.removeListener = removeListener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new VH(v, removeListener);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Exercise e = getItem(position);
        Glide.with(holder.imgGif.getContext())
                .asGif()
                .load(e.getGifUrl())
                .into(holder.imgGif);
        holder.tvName.setText(e.getNameEs() != null ? e.getNameEs() : e.getName());
        holder.tvTarget.setText(e.getTargetEs() != null ? e.getTargetEs() : e.getTarget());
        // el botón ya dispara el callback en el ViewHolder
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgGif;
        TextView tvName, tvTarget;
        ImageButton btnRemove;

        VH(View itemView, RemoveListener rl) {
            super(itemView);
            imgGif    = itemView.findViewById(R.id.imgGif);
            tvName    = itemView.findViewById(R.id.tvNameEs);
            tvTarget  = itemView.findViewById(R.id.tvTargetEs);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnRemove.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    rl.onRemove(pos);
                }
            });
        }
    }

    private static final DiffUtil.ItemCallback<Exercise> DIFF =
            new DiffUtil.ItemCallback<Exercise>() {
                @Override
                public boolean areItemsTheSame(@NonNull Exercise oldItem, @NonNull Exercise newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull Exercise oldItem, @NonNull Exercise newItem) {
                    return oldItem.equals(newItem);
                }
            };
}

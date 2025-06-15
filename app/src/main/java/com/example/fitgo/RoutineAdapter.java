package com.example.fitgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoutineAdapter
        extends RecyclerView.Adapter<RoutineAdapter.GroupViewHolder> {

    public interface Listener {
        void onAddClicked(int groupPosition);
        void onRemoveClicked(int groupPosition, int exercisePosition);
    }

    private final Context context;
    private final List<ExerciseGroup> groupList;
    private final Listener listener;

    public RoutineAdapter(Context context, List<ExerciseGroup> groupList, Listener listener) {
        this.context = context;
        this.groupList = groupList;
        this.listener = listener;
    }

    @NonNull @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_exercise_group, parent, false);
        return new GroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        ExerciseGroup group = groupList.get(position);
        holder.tvGroupName.setText(group.getGroupName());

        // Interno de ejercicios
        holder.rvExercises.setLayoutManager(
                new LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        );
        ExerciseAdapter exAdapter = new ExerciseAdapter(pos ->
                // aquí usamos holder.getAdapterPosition()
                listener.onRemoveClicked(holder.getAdapterPosition(), pos)
        );
        exAdapter.submitList(group.getExercises());
        holder.rvExercises.setAdapter(exAdapter);

        // Botón Agregar
        holder.btnAdd.setOnClickListener(v ->
                listener.onAddClicked(holder.getAdapterPosition())
        );
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName;
        RecyclerView rvExercises;
        Button btnAdd;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            rvExercises = itemView.findViewById(R.id.rvExercises);
            btnAdd      = itemView.findViewById(R.id.btnAdd);
        }
    }
}

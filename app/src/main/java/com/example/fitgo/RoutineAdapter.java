package com.example.fitgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adapter principal: muestra una lista de ExerciseGroup.
 * En cada grupo inflamos item_exercise_group.xml, que contiene
 * un RecyclerView para los ejercicios de ese grupo.
 */
public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.GroupViewHolder> {

    private final List<ExerciseGroup> groupList;
    private final Context context;

    public RoutineAdapter(Context context, List<ExerciseGroup> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        ExerciseGroup group = groupList.get(position);
        holder.tvGroupName.setText(group.getGroupName());

        // Creamos un adaptador para los ejercicios de este grupo
        ExerciseAdapter exerciseAdapter = new ExerciseAdapter(group.getExercises());
        holder.rvExercises.setLayoutManager(
                new LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        );
        holder.rvExercises.setAdapter(exerciseAdapter);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName;
        RecyclerView rvExercises;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            rvExercises = itemView.findViewById(R.id.rvExercises);
        }
    }
}

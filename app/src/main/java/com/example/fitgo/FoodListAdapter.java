package com.example.fitgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {

    private final List<String> foodNames;
    private final WeightActivity.FoodSelectionListener listener;

    public FoodListAdapter(List<String> foodNames, WeightActivity.FoodSelectionListener listener) {
        this.foodNames = foodNames;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String foodName = foodNames.get(position);
        holder.tvFoodName.setText(foodName);
        holder.itemView.setOnClickListener(v -> listener.onSelect(foodName));
    }

    @Override
    public int getItemCount() {
        return foodNames.size();
    }
}

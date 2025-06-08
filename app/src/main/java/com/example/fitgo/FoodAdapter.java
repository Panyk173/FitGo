package com.example.fitgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private final List<WeightActivity.FoodItem> data;

    public FoodAdapter(List<WeightActivity.FoodItem> data) {
        this.data = data;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeightActivity.FoodItem item = data.get(position);
        holder.tvName.setText(item.name);
        holder.tvInfo.setText(
                String.format("Cal: %d  Prot: %d g", item.calories, item.protein)
        );
    }

    @Override public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo;
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvInfo = itemView.findViewById(R.id.tvFoodInfo);
        }
    }
}

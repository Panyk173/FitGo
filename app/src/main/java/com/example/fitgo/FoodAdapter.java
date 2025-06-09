package com.example.fitgo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final List<WeightActivity.FoodItem> foodList;

    public FoodAdapter(List<WeightActivity.FoodItem> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        WeightActivity.FoodItem item = foodList.get(position);
        holder.tvName.setText(item.name);
        holder.tvInfo.setText("Cal: " + item.calories + "  Prot: " + item.protein + " g");

        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.imageUrl)
                    .into(holder.ivFood);
        } else {
            holder.ivFood.setImageResource(R.drawable.ic_food_placeholder); // imagen por defecto
        }

        holder.ivDelete.setOnClickListener(v -> {
            foodList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, foodList.size());
            // Puedes también llamar a guardarComidasEnFirestore() desde la actividad.
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo;
        ImageView ivFood, ivDelete;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvInfo = itemView.findViewById(R.id.tvFoodInfo);
            ivFood = itemView.findViewById(R.id.ivFoodImage);
            ivDelete = itemView.findViewById(R.id.ivDeleteFood);
        }
    }
}

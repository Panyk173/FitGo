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

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName, tvFoodInfo;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodInfo = itemView.findViewById(R.id.tvFoodInfo);
        }
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

        holder.tvFoodName.setText(item.name);
        holder.tvFoodInfo.setText("Cal: " + item.calories + "  Prot: " + item.protein + " g");

        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_food_placeholder)
                    .into(holder.ivFoodImage);
        } else {
            holder.ivFoodImage.setImageResource(R.drawable.ic_food_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}

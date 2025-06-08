package com.example.fitgo;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NutritionixResponse {
    @SerializedName("foods")
    public List<Nutrient> foods;

    public static class Nutrient {
        @SerializedName("food_name")
        public String foodName;

        @SerializedName("nf_calories")
        public float calories;

        @SerializedName("nf_protein")
        public float protein;
    }
}

package com.example.fitgo;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NutritionixResponse {
    @SerializedName("foods")
    public List<com.example.fitgo.api.model.Nutrient> foods;
}

package com.example.fitgo.api.model;

import com.google.gson.annotations.SerializedName;

public class Nutrient {
    @SerializedName("food_name")
    public String foodName;

    @SerializedName("nf_calories")
    public float calories;

    @SerializedName("nf_protein")
    public float protein;
}

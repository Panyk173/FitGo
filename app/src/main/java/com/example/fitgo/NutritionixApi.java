package com.example.fitgo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NutritionixApi {
    String BASE_URL = "https://trackapi.nutritionix.com/";

    @Headers({
            "Content-Type: application/json",
            "x-app-id: ff5214d8",       // Tu Application ID (ff5214d8)
            "x-app-key: 16f8781bf16f227dca58994b14b4dab3"  // Tu Application Key
    })
    @POST("v2/natural/nutrients")
    Call<NutritionixResponse> getNutrients(@Body Map<String, String> body);
}

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
            "x-app-id: ff5214d8",
            "x-app-key: b26cfa1c61ca325ee3b96c4c28804d4d"
    })
    @POST("v2/natural/nutrients")
    Call<NutritionixResponse> getNutrients(@Body Map<String, String> body);

    // <<< ESTE es el que falta para Autocomplete >>>
    @Headers({
            "Content-Type: application/json",
            "x-app-id: ff5214d8",
            "x-app-key: b26cfa1c61ca325ee3b96c4c28804d4d"
    })
    @POST("v2/search/instant")
    Call<AutoCompleteResponse> autoComplete(@Body Map<String, String> body);
}
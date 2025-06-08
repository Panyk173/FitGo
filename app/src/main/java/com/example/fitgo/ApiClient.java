package com.example.fitgo;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    public static NutritionixApi getNutritionixApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(NutritionixApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(NutritionixApi.class);
    }
}

package com.example.fitgo;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static NutritionixApi nutritionixApi;

    public static NutritionixApi getNutritionixApi() {
        if (nutritionixApi == null) {
            // Interceptor para que Retrofit imprima en log las peticiones y respuestas (opcional)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(NutritionixApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            nutritionixApi = retrofit.create(NutritionixApi.class);
        }
        return nutritionixApi;
    }
}

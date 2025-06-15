package com.example.fitgo;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TranslatorService {

    @Headers({
            "Ocp-Apim-Subscription-Key: 8jV8XWEH0w8OICTQzkFmiQoJXN9mfNIXr8oi7i6RYXlUoqeYUvk6JQQJ99BFAC5RqLJXJ3w3AAAbACOGFfeq",
            "Ocp-Apim-Subscription-Region: westeurope",
            "Content-Type: application/json"
    })
    @POST("translate?api-version=3.0&to=es")
    Call<List<TranslationResult>> translate(@Body List<TextToTranslate> texts);
}

// Clase para enviar texto a traducir
class TextToTranslate {
    @SerializedName("Text")
    private String text;

    public TextToTranslate(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

// Clase para representar respuesta
class TranslationResult {
    private List<TranslatedText> translations;

    public List<TranslatedText> getTranslations() {
        return translations;
    }
}

// Clase para traducción individual
class TranslatedText {
    private String text;

    public String getText() {
        return text;
    }
}

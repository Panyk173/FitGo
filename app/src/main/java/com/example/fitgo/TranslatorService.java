package com.example.fitgo;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TranslatorService {
    @Headers({
            "Ocp-Apim-Subscription-Key: TU_SUBSCRIPTION_KEY",
            "Ocp-Apim-Subscription-Region: TU_REGION",
            "Content-Type: application/json"
    })
    @POST("translate?api-version=3.0&to=es")
    Call<List<TranslationResult>> translate(@Body List<TextToTranslate> texts);
}

class TextToTranslate {
    private String Text;
    public TextToTranslate(String text) { Text = text; }
}

class TranslationResult {
    private List<TranslatedText> translations;
    public List<TranslatedText> getTranslations() { return translations; }
}

class TranslatedText {
    private String text;
    public String getText() { return text; }
}

package com.example.fitgo;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseRepository {
    private final ExerciseDbService exerciseApi;
    private final TranslatorService translatorApi;
    private final Map<String, String> cache = new HashMap<>();

    public interface CallbackExercises {
        void onSuccess(List<Exercise> list);
        void onError(Throwable t);
    }

    public ExerciseRepository(ExerciseDbService exerciseApi, TranslatorService translatorApi) {
        this.exerciseApi = exerciseApi;
        this.translatorApi = translatorApi;
    }

    public void fetchByBodyPart(String part, CallbackExercises callback) {
        exerciseApi.getByBodyPart(part).enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleTranslation(response.body(), callback);
                } else if (response.code() == 404) {
                    // Fallback: si no existe el endpoint, trae todo y filtra en cliente
                    exerciseApi.getAllExercises().enqueue(new Callback<List<Exercise>>() {
                        @Override
                        public void onResponse(Call<List<Exercise>> call2, Response<List<Exercise>> resp2) {
                            if (resp2.isSuccessful() && resp2.body() != null) {
                                List<Exercise> todas = resp2.body();
                                List<Exercise> filtradas = new ArrayList<>();
                                for (Exercise e : todas) {
                                    if (e.getBodyPart().equalsIgnoreCase(part)) {
                                        filtradas.add(e);
                                    }
                                }
                                handleTranslation(filtradas, callback);
                            } else {
                                callback.onError(new Exception("Fallback failed: HTTP " + resp2.code()));
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Exercise>> call2, Throwable t) {
                            callback.onError(t);
                        }
                    });
                } else {
                    callback.onError(new Exception("Error fetching exercises: HTTP " + response.code()));
                }
            }
            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    private void handleTranslation(List<Exercise> list, CallbackExercises callback) {
        Set<String> uniques = new HashSet<>();
        for (Exercise e : list) {
            if (!cache.containsKey(e.getName())) uniques.add(e.getName());
            if (!cache.containsKey(e.getBodyPart())) uniques.add(e.getBodyPart());
            if (!cache.containsKey(e.getEquipment())) uniques.add(e.getEquipment());
            if (!cache.containsKey(e.getTarget())) uniques.add(e.getTarget());
        }
        if (uniques.isEmpty()) {
            assignTranslations(list);
            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(list));
            return;
        }
        List<TextToTranslate> toTranslate = new ArrayList<>();
        for (String s : uniques) {
            toTranslate.add(new TextToTranslate(s));
        }
        translatorApi.translate(toTranslate).enqueue(new Callback<List<TranslationResult>>() {
            @Override
            public void onResponse(Call<List<TranslationResult>> call2, Response<List<TranslationResult>> resp2) {
                if (resp2.isSuccessful() && resp2.body() != null) {
                    List<TranslationResult> results = resp2.body();
                    int i = 0;
                    for (String s : uniques) {
                        String tr = results.get(i++).getTranslations().get(0).getText();
                        cache.put(s, tr);
                    }
                    assignTranslations(list);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(list));
                } else {
                    callback.onError(new Exception("Error translating texts: HTTP " + resp2.code()));
                }
            }
            @Override
            public void onFailure(Call<List<TranslationResult>> call2, Throwable t) {
                callback.onError(t);
            }
        });
    }

    private void assignTranslations(List<Exercise> list) {
        for (Exercise e : list) {
            e.setNameEs(cache.get(e.getName()));
            e.setBodyPartEs(cache.get(e.getBodyPart()));
            e.setEquipmentEs(cache.get(e.getEquipment()));
            e.setTargetEs(cache.get(e.getTarget()));
        }
    }

    public void fetchAllExercises(CallbackExercises callback) {
        exerciseApi.getAllExercises().enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    handleTranslation(resp.body(), callback);
                } else {
                    callback.onError(new Exception("Error fetching all: HTTP " + resp.code()));
                }
            }
            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

}

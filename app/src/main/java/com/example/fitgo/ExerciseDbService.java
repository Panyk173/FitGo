package com.example.fitgo;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ExerciseDbService {
    // Obtiene todos los ejercicios sin filtrar por parte del cuerpo
    @GET("exercises")
    Call<List<Exercise>> getAllExercises();
}

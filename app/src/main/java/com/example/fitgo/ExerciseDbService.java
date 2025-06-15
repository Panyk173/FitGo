package com.example.fitgo;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ExerciseDbService {
    @GET("exercises")
    Call<List<Exercise>> getAllExercises();

    @GET("exercises/bodyPart/{part}")
    Call<List<Exercise>> getByBodyPart(@Path("part") String part);
}

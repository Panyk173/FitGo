package com.example.fitgo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RoutineActivity extends AppCompatActivity {

    private EditText etSearch;
    private ListView lvExercises;
    private ExerciseAdapter adapter;
    private List<Exercise> exerciseList = new ArrayList<>();
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        etSearch = findViewById(R.id.etSearch);
        lvExercises = findViewById(R.id.lvExercises);
        btnClear = findViewById(R.id.btnClear);

        adapter = new ExerciseAdapter(this, exerciseList, position -> {
            exerciseList.remove(position);
            adapter.updateList(exerciseList);
        });
        lvExercises.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExercises(s.toString());
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            filterExercises("");
        });

        // LOGO superior que lleva a Home
        ImageView logoIcon = findViewById(R.id.logo_icon);
        if (logoIcon != null) {
            logoIcon.setOnClickListener(v -> {
                Intent intent = new Intent(RoutineActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        // NAV inferior
        setupFooterNavigation();

        fetchExercises();
    }

    private void fetchExercises() {
        // Cliente ExerciseDB
        OkHttpClient rapidClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request req = chain.request().newBuilder()
                            .addHeader("x-rapidapi-key", "6e1d23d757msh1338b99d5db95e6p1ddc18jsnace979dc6902")
                            .addHeader("x-rapidapi-host", "exercisedb.p.rapidapi.com")
                            .build();
                    return chain.proceed(req);
                })
                .build();
        Retrofit retrofitEx = new Retrofit.Builder()
                .baseUrl("https://exercisedb.p.rapidapi.com/")
                .client(rapidClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExerciseDbService exApi = retrofitEx.create(ExerciseDbService.class);

        // Cliente Azure Translator
        OkHttpClient transClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request req = chain.request().newBuilder()
                            .addHeader("Ocp-Apim-Subscription-Key", "8jV8XWEH0w8OICTQzkFmiQoJXN9mfNIXr8oi7i6RYXlUoqeYUvk6JQQJ99BFAC5RqLJXJ3w3AAAbACOGFfeq")
                            .addHeader("Ocp-Apim-Subscription-Region", "westeurope")
                            .build();
                    return chain.proceed(req);
                })
                .build();
        Retrofit retrofitTr = new Retrofit.Builder()
                .baseUrl("https://api.cognitive.microsofttranslator.com/")
                .client(transClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TranslatorService trApi = retrofitTr.create(TranslatorService.class);

        // Repositorio de ejercicios con traducción
        ExerciseRepository repo = new ExerciseRepository(exApi, trApi);
        repo.fetchAllExercises(new ExerciseRepository.CallbackExercises() {
            @Override
            public void onSuccess(List<Exercise> list) {
                runOnUiThread(() -> {
                    exerciseList.clear();
                    exerciseList.addAll(list);
                    adapter.updateList(exerciseList);
                });
            }

            @Override
            public void onError(Throwable t) {
                runOnUiThread(() ->
                        Toast.makeText(RoutineActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    private void filterExercises(String query) {
        if (query == null || query.isEmpty()) {
            adapter.updateList(exerciseList);
            return;
        }
        List<Exercise> filtered = exerciseList.stream()
                .filter(e -> {
                    String name = e.getNameEs() != null ? e.getNameEs() : e.getName();
                    return name.toLowerCase().contains(query.toLowerCase());
                })
                .collect(Collectors.toList());
        adapter.updateList(filtered);
    }

    private void setupFooterNavigation() {
        ImageView ivRoutine  = findViewById(R.id.ivRoutine);
        ImageView ivSettings = findViewById(R.id.ivSettings);
        ImageView ivHealth   = findViewById(R.id.ivHealth);
        ImageView ivWeight   = findViewById(R.id.ivWeight);
        ImageView ivContact  = findViewById(R.id.ivContact);

        if (ivRoutine != null) {
            ivRoutine.setOnClickListener(v -> {});
        }

        if (ivSettings != null) {
            ivSettings.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
            });
        }

        if (ivHealth != null) {
            ivHealth.setOnClickListener(v -> {
                startActivity(new Intent(this, HealthActivity.class));
                finish();
            });
        }

        if (ivWeight != null) {
            ivWeight.setOnClickListener(v -> {
                startActivity(new Intent(this, WeightActivity.class));
                finish();
            });
        }

        if (ivContact != null) {
            ivContact.setOnClickListener(v -> {
                startActivity(new Intent(this, ContactActivity.class));
                finish();
            });
        }
    }
}

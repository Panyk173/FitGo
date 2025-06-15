package com.example.fitgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RoutineActivity extends AppCompatActivity
        implements RoutineAdapter.Listener {

    private static final String TAG = "RoutineActivity";

    private RecyclerView recyclerViewGroups;
    private RoutineAdapter routineAdapter;
    private Map<String, List<Exercise>> allByZone;
    private List<ExerciseGroup> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        findViewById(R.id.logo_icon).setOnClickListener(v ->
                startActivity(new Intent(this, HomeActivity.class))
        );
        findViewById(R.id.ivHealth).setOnClickListener(v ->
                startActivity(new Intent(this, HealthActivity.class))
        );
        findViewById(R.id.ivWeight).setOnClickListener(v ->
                startActivity(new Intent(this, WeightActivity.class))
        );
        findViewById(R.id.ivContact).setOnClickListener(v ->
                startActivity(new Intent(this, ContactActivity.class))
        );
        findViewById(R.id.ivRoutine).setOnClickListener(v ->
                startActivity(new Intent(this, RoutineActivity.class))
        );
        findViewById(R.id.ivSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class))
        );

        recyclerViewGroups = findViewById(R.id.recyclerViewGroups);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(this));

        // Retrofit + OkHttp for ExerciseDB (RapidAPI)
        OkHttpClient rapidClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request req = chain.request().newBuilder()
                            .addHeader("x-rapidapi-key",  "6e1d23d757msh1338b99d5db95e6p1ddc18jsnace979dc6902")
                            .addHeader("x-rapidapi-host", "exercisedb.p.rapidapi.com")
                            .build();
                    return chain.proceed(req);
                })
                .build();
        Retrofit retrofitExercises = new Retrofit.Builder()
                .baseUrl("https://exercisedb.p.rapidapi.com/")
                .client(rapidClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ExerciseDbService exApi = retrofitExercises.create(ExerciseDbService.class);

        // Retrofit + OkHttp for Azure Translator
        OkHttpClient transClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request req = original.newBuilder()
                            .header("Ocp-Apim-Subscription-Key",    "8jV8XWEH0w8OICTQzkFmiQoJXN9mfNIXr8oi7i6RYXlUoqeYUvk6JQQJ99BFAC5RqLJXJ3w3AAAbACOGFfeq")
                            .header("Ocp-Apim-Subscription-Region", "westeurope")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(req);
                })
                .build();
        Retrofit retrofitTrans = new Retrofit.Builder()
                .baseUrl("https://api.cognitive.microsofttranslator.com/")
                .client(transClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TranslatorService trApi = retrofitTrans.create(TranslatorService.class);

        // Fetch ALL exercises and then group/limit
        ExerciseRepository repo = new ExerciseRepository(exApi, trApi);
        repo.fetchAllExercises(new ExerciseRepository.CallbackExercises() {
            @Override
            public void onSuccess(List<Exercise> list) {
                runOnUiThread(() -> groupAndDisplay(list));
            }
            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Error fetching all exercises", t);
                runOnUiThread(() ->
                        Toast.makeText(RoutineActivity.this,
                                t.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    private void groupAndDisplay(List<Exercise> list) {
        // 1) Agrupa por zona (español si existe)
        allByZone = new LinkedHashMap<>();
        for (Exercise e : list) {
            String zona = e.getBodyPartEs() != null
                    ? e.getBodyPartEs()
                    : e.getBodyPart();
            allByZone
                    .computeIfAbsent(zona, k -> new ArrayList<>())
                    .add(e);
        }

        // 2) Construye grupos limitados a 3
        groups = new ArrayList<>();
        for (Map.Entry<String, List<Exercise>> entry : allByZone.entrySet()) {
            List<Exercise> todos = entry.getValue();
            List<Exercise> primeros = todos.subList(0, Math.min(1, todos.size()));
            groups.add(new ExerciseGroup(entry.getKey(), new ArrayList<>(primeros)));
        }

        // 3) Crea adapter con listener
        routineAdapter = new RoutineAdapter(this, groups, this);
        recyclerViewGroups.setAdapter(routineAdapter);
    }

    @Override
    public void onRemoveClicked(int groupPos, int exPos) {
        groups.get(groupPos).getExercises().remove(exPos);
        routineAdapter.notifyItemChanged(groupPos);
    }

    @Override
    public void onAddClicked(int groupPos) {
        showAddDialog(groupPos);
    }

    private void showAddDialog(int groupPos) {
        String zona = groups.get(groupPos).getGroupName();
        List<Exercise> disponibles = new ArrayList<>(allByZone.get(zona));
        disponibles.removeAll(groups.get(groupPos).getExercises());

        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_search_exercises, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        SearchView sv    = dialogView.findViewById(R.id.svSearch);
        RecyclerView rv  = dialogView.findViewById(R.id.rvSearchResults);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Asegurarnos de usar el widget de AppCompat y que no aparezca iconificado
        sv.setIconifiedByDefault(false);
        sv.clearFocus();

        SearchAdapter searchAdapter = new SearchAdapter(disponibles, ex -> {
            groups.get(groupPos).getExercises().add(ex);
            routineAdapter.notifyItemChanged(groupPos);
            dialog.dismiss();
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(searchAdapter);
        // Forzar pintado inicial con la lista completa
        searchAdapter.filter(""); // ahora verás todos los ejercicios disponibles al abrir

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String query) {
                searchAdapter.filter(query);
                return true;
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}

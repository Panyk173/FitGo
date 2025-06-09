package com.example.fitgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeightActivity extends AppCompatActivity {

    private RecyclerView rvBreakfast, rvLunch, rvSnack, rvDinner;
    private FoodAdapter adapterBreakfast, adapterLunch, adapterSnack, adapterDinner;
    private List<FoodItem> listBreakfast, listLunch, listSnack, listDinner;
    private TextView tvCalorieNeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        rvBreakfast = findViewById(R.id.rvBreakfast);
        rvLunch = findViewById(R.id.rvLunch);
        rvSnack = findViewById(R.id.rvSnack);
        rvDinner = findViewById(R.id.rvDinner);
        tvCalorieNeeds = findViewById(R.id.tvCalorieNeeds);

        listBreakfast = new ArrayList<>();
        listLunch = new ArrayList<>();
        listSnack = new ArrayList<>();
        listDinner = new ArrayList<>();

        fetchNutritionForFood("Manzana", item -> {
            for (int i = 0; i < 1; i++) {
                listBreakfast.add(item);
            }
            setupAdaptersAndFooter();
            calculateAndShowNeeds();
        });

        ImageView logoIcon = findViewById(R.id.logo_icon);
        if (logoIcon != null) {
            logoIcon.setOnClickListener(v -> {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupAdaptersAndFooter() {
        adapterBreakfast = new FoodAdapter(listBreakfast);
        adapterLunch = new FoodAdapter(listLunch);
        adapterSnack = new FoodAdapter(listSnack);
        adapterDinner = new FoodAdapter(listDinner);

        rvBreakfast.setLayoutManager(new LinearLayoutManager(this));
        rvBreakfast.setAdapter(adapterBreakfast);
        rvLunch.setLayoutManager(new LinearLayoutManager(this));
        rvLunch.setAdapter(adapterLunch);
        rvSnack.setLayoutManager(new LinearLayoutManager(this));
        rvSnack.setAdapter(adapterSnack);
        rvDinner.setLayoutManager(new LinearLayoutManager(this));
        rvDinner.setAdapter(adapterDinner);

        findViewById(R.id.btnAddBreakfast).setOnClickListener(v -> showFoodSearchDialog(item -> {
            listBreakfast.add(item);
            adapterBreakfast.notifyDataSetChanged();
        }));

        findViewById(R.id.btnAddLunch).setOnClickListener(v -> showFoodSearchDialog(item -> {
            listLunch.add(item);
            adapterLunch.notifyDataSetChanged();
        }));

        findViewById(R.id.btnAddSnack).setOnClickListener(v -> showFoodSearchDialog(item -> {
            listSnack.add(item);
            adapterSnack.notifyDataSetChanged();
        }));

        findViewById(R.id.btnAddDinner).setOnClickListener(v -> showFoodSearchDialog(item -> {
            listDinner.add(item);
            adapterDinner.notifyDataSetChanged();
        }));

        setupFooterNavigation();
    }

    private void showFoodSearchDialog(FoodSelectionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_food_search, null);
        builder.setView(view);

        EditText etSearch = view.findViewById(R.id.etSearchFood);
        RecyclerView recyclerView = view.findViewById(R.id.rvFoodResults);

        List<String> filteredList = new ArrayList<>();
        FoodListAdapter adapter = new FoodListAdapter(filteredList, new FoodSelectionListener() {
            @Override
            public void onSelectItem(FoodItem itemFromDialog) {}

            @Override
            public void onSelect(String foodName) {
                fetchNutritionForFood(foodName, listener::onSelectItem);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.length() < 2) return;

                Map<String, String> body = new HashMap<>();
                body.put("query", query);

                ApiClient.getNutritionixApi().autoComplete(body).enqueue(new Callback<AutoCompleteResponse>() {
                    @Override
                    public void onResponse(Call<AutoCompleteResponse> call, Response<AutoCompleteResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<String> results = new ArrayList<>();
                            for (AutoCompleteResponse.Item item : response.body().getItems()) {
                                results.add(item.food_name);
                            }
                            filteredList.clear();
                            filteredList.addAll(results);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<AutoCompleteResponse> call, Throwable t) {
                        // error
                    }
                });
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void fetchNutritionForFood(String foodName, NutritionCallback callback) {
        Map<String, String> body = new HashMap<>();
        body.put("query", foodName);

        ApiClient.getNutritionixApi().getNutrients(body).enqueue(new Callback<NutritionixResponse>() {
            @Override
            public void onResponse(Call<NutritionixResponse> call, Response<NutritionixResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().foods.isEmpty()) {
                    NutritionixResponse.Nutrient n = response.body().foods.get(0);
                    Log.d("DEBUG_API", "Alimento: " + n.food_name + " | Cal: " + n.calories + " | Prot: " + n.protein);
                    FoodItem item = new FoodItem(n.food_name, Math.round(n.calories), Math.round(n.protein),
                            n.photo != null ? n.photo.thumb : "");
                    callback.onResult(item);
                } else {
                    callback.onResult(new FoodItem(foodName, 0, 0, ""));
                }
            }

            @Override
            public void onFailure(Call<NutritionixResponse> call, Throwable t) {
                callback.onResult(new FoodItem(foodName, 0, 0, ""));
            }
        });
    }

    private void calculateAndShowNeeds() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String ws = prefs.getString("user_weight", "");
        String hs = prefs.getString("user_height", "");

        if (ws.isEmpty() || hs.isEmpty()) {
            tvCalorieNeeds.setText("Calorías diarias recomendadas: -- kcal\nProteína recomendada: -- g");
            return;
        }

        try {
            float w = Float.parseFloat(ws);
            float h = Float.parseFloat(hs);
            int age = 25, sex = 1;
            float bmr = 10 * w + 6.25f * h - 5 * age + (sex == 1 ? 5 : -161);
            float tdee = bmr * 1.375f;
            float prot = w * 2f;

            tvCalorieNeeds.setText(String.format("Calorías diarias recomendadas: %d kcal\nProteína recomendada: %d g",
                    Math.round(tdee), Math.round(prot)));
        } catch (NumberFormatException e) {
            tvCalorieNeeds.setText("Calorías diarias recomendadas: -- kcal\nProteína recomendada: -- g");
        }
    }

    private void setupFooterNavigation() {
        ImageView ivRoutine = findViewById(R.id.ivRoutine);
        ImageView ivSettings = findViewById(R.id.ivSettings);
        ImageView ivHealth = findViewById(R.id.ivHealth);
        ImageView ivWeight = findViewById(R.id.ivWeight);
        ImageView ivContact = findViewById(R.id.ivContact);

        if (ivRoutine != null) ivRoutine.setOnClickListener(v -> startActivity(new Intent(this, RoutineActivity.class)));
        if (ivSettings != null) ivSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        if (ivHealth != null) ivHealth.setOnClickListener(v -> startActivity(new Intent(this, HealthActivity.class)));
        if (ivWeight != null) ivWeight.setOnClickListener(v -> {});
        if (ivContact != null) ivContact.setOnClickListener(v -> startActivity(new Intent(this, ContactActivity.class)));
    }

    // callback de nutrición
    private interface NutritionCallback {
        void onResult(FoodItem item);
    }

    // listener para seleccionar alimento
    public interface FoodSelectionListener {
        void onSelectItem(FoodItem item);
        default void onSelect(String foodName) {}
    }

    // modelo del alimento
    static class FoodItem {
        String name;
        int calories, protein;
        String imageUrl;

        FoodItem(String name, int calories, int protein, String imageUrl) {
            this.name = name;
            this.calories = calories;
            this.protein = protein;
            this.imageUrl = imageUrl;
        }
    }
}

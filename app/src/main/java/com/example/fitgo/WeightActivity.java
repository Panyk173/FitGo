package com.example.fitgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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



/**
 * Permite ver y añadir alimentos consumidos en cada comida.
 * Además muestra calorías y proteína recomendadas (cálculo basado en peso/altura).
 */
public class WeightActivity extends AppCompatActivity {

    // RecyclerViews para cada comida
    private RecyclerView rvBreakfast, rvLunch, rvSnack, rvDinner;

    // Adaptadores y listas
    private FoodAdapter adapterBreakfast, adapterLunch, adapterSnack, adapterDinner;
    private List<FoodItem> listBreakfast, listLunch, listSnack, listDinner;

    // TextView para calorías y proteínas recomendadas
    private TextView tvCalorieNeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        // 1) Enlazar vistas
        rvBreakfast    = findViewById(R.id.rvBreakfast);
        rvLunch        = findViewById(R.id.rvLunch);
        rvSnack        = findViewById(R.id.rvSnack);
        rvDinner       = findViewById(R.id.rvDinner);
        tvCalorieNeeds = findViewById(R.id.tvCalorieNeeds);

        // 2) Crear listas vacías
        listBreakfast = new ArrayList<>();
        listLunch     = new ArrayList<>();
        listSnack     = new ArrayList<>();
        listDinner    = new ArrayList<>();

        // 3) Pre-poblar con 3 ejemplos en cada sección:
        fetchNutritionForFood("Manzana", (cal, prot) -> {
            if (cal >= 0) {
                // API respondió correctamente: duplicamos 3 veces
                for (int i = 0; i < 3; i++) {
                    listBreakfast.add(new FoodItem("Manzana (1 mediana)", cal, prot));
                }
            } else {
                // Fallback manual
                listBreakfast.add(new FoodItem("Manzana (1 mediana)", 95, 0));
                listBreakfast.add(new FoodItem("Plátano (1 mediano)", 105, 1));
                listBreakfast.add(new FoodItem("Avena (50g)", 190, 6));
            }
            setupAdaptersAndFooter();
            calculateAndShowNeeds();
        });

        // 4) Logo → Home
        ImageView logoIcon = findViewById(R.id.logo_icon);
        if (logoIcon != null) {
            logoIcon.setOnClickListener(v -> {
                Intent intent = new Intent(WeightActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    /**
     * Configura los RecyclerViews, botones y footer una vez cargados los ejemplos.
     */
    private void setupAdaptersAndFooter() {
        adapterBreakfast = new FoodAdapter(listBreakfast);
        adapterLunch     = new FoodAdapter(listLunch);
        adapterSnack     = new FoodAdapter(listSnack);
        adapterDinner    = new FoodAdapter(listDinner);

        rvBreakfast.setLayoutManager(new LinearLayoutManager(this));
        rvBreakfast.setAdapter(adapterBreakfast);
        rvLunch.setLayoutManager(new LinearLayoutManager(this));
        rvLunch.setAdapter(adapterLunch);
        rvSnack.setLayoutManager(new LinearLayoutManager(this));
        rvSnack.setAdapter(adapterSnack);
        rvDinner.setLayoutManager(new LinearLayoutManager(this));
        rvDinner.setAdapter(adapterDinner);

        findViewById(R.id.btnAddBreakfast).setOnClickListener(v ->
                showAddFoodDialog(listBreakfast, adapterBreakfast)
        );
        findViewById(R.id.btnAddLunch).setOnClickListener(v ->
                showAddFoodDialog(listLunch, adapterLunch)
        );
        findViewById(R.id.btnAddSnack).setOnClickListener(v ->
                showAddFoodDialog(listSnack, adapterSnack)
        );
        findViewById(R.id.btnAddDinner).setOnClickListener(v ->
                showAddFoodDialog(listDinner, adapterDinner)
        );

        setupFooterNavigation();
    }

    /**
     * Muestra un diálogo con AutoCompleteTextView para buscar alimentos.
     */
    private void showAddFoodDialog(List<FoodItem> targetList, FoodAdapter targetAdapter) {
        // 1) Inflar la vista
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search_food, null);
        AutoCompleteTextView actv = dialogView.findViewById(R.id.actvFoodSearch);

        // 2) Configurar el adaptador de sugerencias vacío
        List<String> suggestions = new ArrayList<>();
        ArrayAdapter<String> autoAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                suggestions
        );
        actv.setAdapter(autoAdapter);
        actv.setThreshold(2);  // empieza a sugerir tras 2 caracteres

        // 3) Construir el diálogo sin mostrar todavía
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Añadir alimento")
                .setView(dialogView)
                .setPositiveButton("Añadir", (d, which) -> {
                    String selected = actv.getText().toString().trim();
                    if (selected.isEmpty()) {
                        Toast.makeText(this, "Selecciona un alimento", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fetchNutritionForFood(selected, (cal, prot) -> {
                        if (cal >= 0) {
                            targetList.add(new FoodItem(selected, cal, prot));
                            targetAdapter.notifyItemInserted(targetList.size() - 1);
                        } else {
                            Toast.makeText(this, "No info nutricional", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .create();

        // 4) Mostrar el diálogo _antes_ de enlazar el listener de texto
        dialog.show();

        // 5) Ahora sí, enlazar el TextWatcher y la llamada a la API
        actv.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                if (query.length() < 2) return;

                // 5a) Preparar cuerpo y llamar a autocomplete
                Map<String, String> body = new HashMap<>();
                body.put("query", query.toString());

                ApiClient.getNutritionixApi()
                        .autoComplete(body)
                        .enqueue(new Callback<AutoCompleteResponse>() {
                            @Override
                            public void onResponse(Call<AutoCompleteResponse> call, Response<AutoCompleteResponse> resp) {
                                if (resp.isSuccessful() && resp.body() != null) {
                                    suggestions.clear();
                                    for (AutoCompleteResponse.Item it : resp.body().getItems()) {
                                        suggestions.add(it.food_name);
                                    }
                                    autoAdapter.notifyDataSetChanged();
                                    // 5b) y sólo aquí, tras actualizar, forzar el dropdown
                                    actv.showDropDown();
                                }
                            }
                            @Override public void onFailure(Call<AutoCompleteResponse> call, Throwable t) {
                                // opcional: log
                            }
                        });
            }
        });
    }



    /**
     * Llama a Nutritionix API para obtener calorías y proteínas.
     */
    private void fetchNutritionForFood(String foodName, NutritionCallback callback) {
        Map<String,String> body = new HashMap<>();
        body.put("query", foodName);

        ApiClient.getNutritionixApi()
                .getNutrients(body)
                .enqueue(new Callback<NutritionixResponse>() {
                    @Override
                    public void onResponse(Call<NutritionixResponse> call, Response<NutritionixResponse> res) {
                        if (res.isSuccessful() && res.body()!=null && !res.body().foods.isEmpty()) {
                            // Aquí eliminamos 'var' y usamos el tipo explícito:
                            NutritionixResponse.Nutrient n = res.body().foods.get(0);
                            int calories = Math.round(n.calories);
                            int protein  = Math.round(n.protein);
                            callback.onResult(calories, protein);
                        } else {
                            callback.onResult(-1, -1);
                        }
                    }
                    @Override public void onFailure(Call<NutritionixResponse> call, Throwable t) {
                        callback.onResult(-1, -1);
                    }
                });
    }

    /**
     * Calcula y muestra necesidades diarias según peso/altura en SharedPreferences.
     */
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
            float bmr = 10*w + 6.25f*h - 5*age + (sex==1?5:-161);
            float tdee = bmr * 1.375f;
            float prot = w * 2f;
            tvCalorieNeeds.setText(
                    String.format("Calorías diarias recomendadas: %d kcal\nProteína recomendada: %d g",
                            Math.round(tdee), Math.round(prot))
            );
        } catch (NumberFormatException e) {
            tvCalorieNeeds.setText("Calorías diarias recomendadas: -- kcal\nProteína recomendada: -- g");
        }
    }

    private void setupFooterNavigation() {
        ImageView ivRoutine  = findViewById(R.id.ivRoutine);
        ImageView ivSettings = findViewById(R.id.ivSettings);
        ImageView ivHealth   = findViewById(R.id.ivHealth);
        ImageView ivWeight   = findViewById(R.id.ivWeight);
        ImageView ivContact  = findViewById(R.id.ivContact);

        if (ivRoutine  != null) ivRoutine.setOnClickListener(v -> { startActivity(new Intent(this, RoutineActivity.class)); finish(); });
        if (ivSettings != null) ivSettings.setOnClickListener(v -> { startActivity(new Intent(this, SettingsActivity.class)); finish(); });
        if (ivHealth   != null) ivHealth.setOnClickListener(v -> { startActivity(new Intent(this, HealthActivity.class)); finish(); });
        if (ivWeight   != null) ivWeight.setOnClickListener(v -> {});  // ya en esta
        if (ivContact  != null) ivContact.setOnClickListener(v -> { startActivity(new Intent(this, ContactActivity.class)); finish(); });
    }

    private interface NutritionCallback { void onResult(int calories, int protein); }

    static class FoodItem {
        String name;
        int calories, protein;
        FoodItem(String name, int calories, int protein) {
            this.name = name;
            this.calories = calories;
            this.protein = protein;
        }
    }
}

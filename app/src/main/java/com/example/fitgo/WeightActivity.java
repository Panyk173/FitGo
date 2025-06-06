package com.example.fitgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
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
        rvBreakfast = findViewById(R.id.rvBreakfast);
        rvLunch     = findViewById(R.id.rvLunch);
        rvSnack     = findViewById(R.id.rvSnack);
        rvDinner    = findViewById(R.id.rvDinner);
        tvCalorieNeeds = findViewById(R.id.tvCalorieNeeds);

        // 2) Crear listas vacías
        listBreakfast = new ArrayList<>();
        listLunch     = new ArrayList<>();
        listSnack     = new ArrayList<>();
        listDinner    = new ArrayList<>();

        // 3) Pre-poblar con 3 ejemplos en cada sección:
        //    A) “Desayuno”: buscamos un alimento real en la API y lo duplicamos 3 veces...
        //    B) Si la API falla, añadimos un fallback manual.
        fetchNutritionForFood("Manzana", (cal, prot) -> {
            if (cal >= 0) {
                // Si la API devolvió algo válido, creamos 3 veces el mismo ítem de ejemplo
                for (int i = 0; i < 3; i++) {
                    listBreakfast.add(new FoodItem("Manzana (1 mediana)", cal, prot));
                }
            } else {
                // Fallback: datos simulados si la API no devolvió nada
                listBreakfast.add(new FoodItem("Manzana (1 mediana)", 95, 0));
                listBreakfast.add(new FoodItem("Plátano (1 mediano)", 105, 1));
                listBreakfast.add(new FoodItem("Avena (50g)", 190, 6));
            }
            // NOTA: como la lista cambió, luego notificaremos al adaptador. Sin embargo,
            // adaptadores aún no existen; los creamos **después** de esta llamada.
            // Para simplificar, podemos inicializar adaptadores aquí mismo:
            setupAdaptersAndFooter();
            calculateAndShowNeeds();
        });

        // Si quieres que la app muestre algo mientras llega la respuesta de la API,
        // podrías inicializar adaptadores con listas vacías o simuladas. Por claridad,
        // este ejemplo espera a la respuesta antes de setear adaptadores.

        // 4) Configuración del logo en la toolbar para retroceder al Home
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
     * Configura los RecyclerView y el Footer después de poblar las 4 listas.
     * Se le llama desde el callback de fetchNutritionForFood(...) arriba.
     */
    private void setupAdaptersAndFooter() {
        // A) Crear adaptadores una sola vez, con las listas ya pre-pobladas
        adapterBreakfast = new FoodAdapter(listBreakfast);
        adapterLunch     = new FoodAdapter(listLunch);
        adapterSnack     = new FoodAdapter(listSnack);
        adapterDinner    = new FoodAdapter(listDinner);

        // B) Asignar LayoutManagers y adaptadores
        rvBreakfast.setLayoutManager(new LinearLayoutManager(this));
        rvBreakfast.setAdapter(adapterBreakfast);

        rvLunch.setLayoutManager(new LinearLayoutManager(this));
        rvLunch.setAdapter(adapterLunch);

        rvSnack.setLayoutManager(new LinearLayoutManager(this));
        rvSnack.setAdapter(adapterSnack);

        rvDinner.setLayoutManager(new LinearLayoutManager(this));
        rvDinner.setAdapter(adapterDinner);

        // C) Botones para añadir nuevos alimentos (puedes dejarlos, o añadir ejemplos)
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

        // D) Configurar la navegación inferior
        setupFooterNavigation();
    }

    /**
     * Muestra un diálogo que pide al usuario el nombre del alimento.
     * Luego obtiene sus calorías/proteínas de Nutritionix y añade el FoodItem resultante.
     */
    private void showAddFoodDialog(List<FoodItem> targetList, FoodAdapter targetAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añadir nuevo alimento");

        // Campo de texto para introducir el nombre del alimento
        final EditText input = new EditText(this);
        input.setHint("Nombre del alimento");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        builder.setView(input);

        builder.setPositiveButton("Buscar y añadir", (dialog, which) -> {
            String foodName = input.getText().toString().trim();
            if (foodName.isEmpty()) {
                Toast.makeText(this, "Debes insertar un nombre", Toast.LENGTH_SHORT).show();
                return;
            }
            // Llamamos a Nutritionix
            fetchNutritionForFood(foodName, (calories, protein) -> {
                if (calories >= 0) {
                    FoodItem newItem = new FoodItem(foodName, calories, protein);
                    targetList.add(newItem);
                    targetAdapter.notifyItemInserted(targetList.size() - 1);
                } else {
                    Toast.makeText(this, "No se encontró información", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    /**
     * Llama a Nutritionix API para obtener calorías y proteínas de un alimento.
     * @param foodName Nombre del alimento a buscar.
     * @param callback Callback que recibe calorías y proteínas, o (-1, -1) si no se encontró.
     */
    private void fetchNutritionForFood(String foodName, NutritionCallback callback) {
        // Construimos el body: {"query": "foodName"}
        Map<String, String> body = new HashMap<>();
        body.put("query", foodName);

        NutritionixApi api = ApiClient.getNutritionixApi();
        Call<NutritionixResponse> call = api.getNutrients(body);

        call.enqueue(new Callback<NutritionixResponse>() {
            @Override
            public void onResponse(Call<NutritionixResponse> call, Response<NutritionixResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().foods.isEmpty()) {
                    com.example.fitgo.api.model.Nutrient n = response.body().foods.get(0);
                    int calories = Math.round(n.calories);
                    int protein  = Math.round(n.protein);
                    callback.onResult(calories, protein);
                } else {
                    callback.onResult(-1, -1);
                }
            }

            @Override
            public void onFailure(Call<NutritionixResponse> call, Throwable t) {
                callback.onResult(-1, -1);
            }
        });
    }

    /**
     * Calcula las calorías diarias recomendadas y proteína diaria recomendada.
     * Lee peso (kg) y altura (cm) de SharedPreferences "settings".
     */
    private void calculateAndShowNeeds() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String weightStr = prefs.getString("user_weight", "");
        String heightStr = prefs.getString("user_height", "");

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            tvCalorieNeeds.setText(
                    "Calorías diarias recomendadas: -- kcal\n" +
                            "Proteína recomendada: -- g"
            );
            return;
        }

        try {
            float weightKg = Float.parseFloat(weightStr);
            float heightCm = Float.parseFloat(heightStr);

            int age = 25;
            int sex = 1; // 1 = hombre, 0 = mujer
            float bmr = (10f * weightKg) + (6.25f * heightCm) - (5f * age) + (sex == 1 ? 5f : -161f);
            float tdee = bmr * 1.375f; // factor 1.375 (actividad ligera)
            float proteinNeed = weightKg * 2f;

            String text = String.format(
                    "Calorías diarias recomendadas: %d kcal\nProteína recomendada: %d g",
                    Math.round(tdee),
                    Math.round(proteinNeed)
            );
            tvCalorieNeeds.setText(text);
        } catch (NumberFormatException e) {
            tvCalorieNeeds.setText(
                    "Calorías diarias recomendadas: -- kcal\n" +
                            "Proteína recomendada: -- g"
            );
        }
    }

    private void setupFooterNavigation() {
        ImageView ivRoutine  = findViewById(R.id.ivRoutine);
        ImageView ivSettings = findViewById(R.id.ivSettings);
        ImageView ivHealth   = findViewById(R.id.ivHealth);
        ImageView ivWeight   = findViewById(R.id.ivWeight);
        ImageView ivContact  = findViewById(R.id.ivContact);

        if (ivRoutine != null) {
            ivRoutine.setOnClickListener(v -> {
                startActivity(new Intent(this, RoutineActivity.class));
                finish();
            });
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
            // Ya estás aquí
            ivWeight.setOnClickListener(v -> {});
        }

        if (ivContact != null) {
            ivContact.setOnClickListener(v -> {
                startActivity(new Intent(this, ContactActivity.class));
                finish();
            });
        }
    }

    /** Callback para devolver calorías y proteínas */
    private interface NutritionCallback {
        void onResult(int calories, int protein);
    }

    /** Modelo interno para los alimentos */
    static class FoodItem {
        String name;
        int calories;
        int protein;
        FoodItem(String name, int calories, int protein) {
            this.name = name;
            this.calories = calories;
            this.protein = protein;
        }
    }
}

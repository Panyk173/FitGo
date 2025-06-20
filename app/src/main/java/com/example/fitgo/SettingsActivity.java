package com.example.fitgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private Switch themeSwitch;
    private Button logoutButton;
    private EditText etWeight, etHeight, etWater, etSleep;
    private Button btnSaveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        themeSwitch = findViewById(R.id.switch_theme);
        logoutButton = findViewById(R.id.button_logout);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etWater = findViewById(R.id.etWater);
        etSleep = findViewById(R.id.etSleep);
        btnSaveData = findViewById(R.id.btnSaveData);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("dark_mode", false);
        themeSwitch.setChecked(darkMode);

        AppCompatDelegate.setDefaultNightMode(darkMode ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Leer los datos del usuario desde Firestore al abrir la vista
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String weight = document.getString("weight");
                        String height = document.getString("height");
                        String water = document.getString("water");
                        String sleep = document.getString("sleep");

                        etWeight.setText(weight != null ? weight : "");
                        etHeight.setText(height != null ? height : "");
                        etWater.setText(water != null ? water : "");
                        etSleep.setText(sleep != null ? sleep : "");

                        prefs.edit()
                                .putString("user_weight", weight != null ? weight : "")
                                .putString("user_height", height != null ? height : "")
                                .putString("user_water", water != null ? water : "")
                                .putString("user_sleep", sleep != null ? sleep : "")
                                .apply();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
                );

        btnSaveData.setOnClickListener(v -> {
            String weight = etWeight.getText().toString().trim();
            String height = etHeight.getText().toString().trim();
            String water = etWater.getText().toString().trim();
            String sleep = etSleep.getText().toString().trim();

            if (weight.isEmpty() || height.isEmpty() || water.isEmpty() || sleep.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            prefs.edit()
                    .putString("user_weight", weight)
                    .putString("user_height", height)
                    .putString("user_water", water)
                    .putString("user_sleep", sleep)
                    .apply();

            // Guardado principal del usuario (AHORA ACTUALIZAMOS en lugar de SET)
            DocumentReference userRef = db.collection("users").document(userId);
            Map<String, Object> updates = new HashMap<>();
            updates.put("weight", weight);
            updates.put("height", height);
            updates.put("water", water);
            updates.put("sleep", sleep);

            userRef.update(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar en Firestore", Toast.LENGTH_SHORT).show());

            // Guardar sueño con fecha (YYYY-MM-DD) en subcolección
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Map<String, Object> sleepEntry = new HashMap<>();
            sleepEntry.put("value", sleep);

            db.collection("users")
                    .document(userId)
                    .collection("sleep_logs")
                    .document(currentDate)
                    .set(sleepEntry);

            // Guardar agua con fecha (YYYY-MM-DD) en subcolección
            Map<String, Object> waterEntry = new HashMap<>();
            waterEntry.put("value", water);

            db.collection("users")
                    .document(userId)
                    .collection("water_logs")
                    .document(currentDate)
                    .set(waterEntry);

            // Guardar peso con fecha (YYYY-MM-DD) en subcolección
            Map<String, Object> weightEntry = new HashMap<>();
            weightEntry.put("value", weight);

            db.collection("users")
                    .document(userId)
                    .collection("weight_logs")
                    .document(currentDate)
                    .set(weightEntry);
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            finish();
        });

        ImageView logoIcon = findViewById(R.id.logo_icon);
        if (logoIcon != null) {
            logoIcon.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        setupFooterNavigation();
    }

    private void setupFooterNavigation() {
        ImageView ivRoutine  = findViewById(R.id.ivRoutine);
        ImageView ivSettings = findViewById(R.id.ivSettings);
        ImageView ivHealth   = findViewById(R.id.ivHealth);
        ImageView ivWeight   = findViewById(R.id.ivWeight);
        ImageView ivContact  = findViewById(R.id.ivContact);
        ImageView logoIcon   = findViewById(R.id.logo_icon);

        if (logoIcon != null) {
            logoIcon.setOnClickListener(v -> {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            });
        }

        if (ivRoutine != null) {
            ivRoutine.setOnClickListener(v -> {
                startActivity(new Intent(this, RoutineActivity.class));
                finish();
            });
        }

        if (ivSettings != null) {
            ivSettings.setOnClickListener(v -> {});
        }

        if (ivHealth != null) {
            ivHealth.setOnClickListener(v ->
                    startActivity(new Intent(this, HealthActivity.class))
            );
        }

        if (ivWeight != null) {
            ivWeight.setOnClickListener(v ->
                    startActivity(new Intent(this, WeightActivity.class))
            );
        }

        if (ivContact != null) {
            ivContact.setOnClickListener(v ->
                    startActivity(new Intent(this, ContactActivity.class))
            );
        }
    }
}

// Clase auxiliar
class UserData {
    private String weight;
    private String height;
    private String water;
    private String sleep;

    public UserData() {}
    public UserData(String weight, String height, String water, String sleep) {
        this.weight = weight;
        this.height = height;
        this.water = water;
        this.sleep = sleep;
    }

    public String getWeight() { return weight; }
    public String getHeight() { return height; }
    public String getWater()  { return water; }
    public String getSleep()  { return sleep; }
}

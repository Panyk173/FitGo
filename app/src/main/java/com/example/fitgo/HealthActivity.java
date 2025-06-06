package com.example.fitgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HealthActivity extends AppCompatActivity {

    private TextView tvStepsToday;
    private TextView tvWeightBody;
    private TextView tvBodyFat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        // Enlazamos las vistas
        tvStepsToday  = findViewById(R.id.tvStepsToday);
        tvWeightBody  = findViewById(R.id.tvWeightBody);
        tvBodyFat     = findViewById(R.id.tvBodyFat);

        // Leemos peso y altura desde SharedPreferences ("settings")
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String weightStr = prefs.getString("user_weight", "");
        String heightStr = prefs.getString("user_height", "");

        // Mostramos peso (si existe) o "--" si no hay valor
        if (!weightStr.isEmpty()) {
            tvWeightBody.setText("Peso corporal: " + weightStr + " kg");
        } else {
            tvWeightBody.setText("Peso corporal: -- kg");
        }

        // Calculamos porcentaje de grasa corporal solo si tenemos ambos valores
        if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
            try {
                float weightKg = Float.parseFloat(weightStr);
                // La altura se guardó en cm; la convertimos a metros
                float heightMeters = Float.parseFloat(heightStr) / 100f;

                // Fórmula IMC = peso / (altura^2)
                float imc = weightKg / (heightMeters * heightMeters);

                // Fórmula simplificada para % de grasa (Deurenberg), con edad=25 y sexo=1 (hombre)
                float fatPercent = (1.2f * imc) + (0.23f * 25f) - (10.8f * 1f) - 5.4f;
                if (fatPercent < 0) fatPercent = 0f;

                tvBodyFat.setText(String.format("Grasa corporal: %.1f %%", fatPercent));
            } catch (NumberFormatException e) {
                tvBodyFat.setText("Grasa corporal: -- %");
            }
        } else {
            tvBodyFat.setText("Grasa corporal: -- %");
        }

        // Logo en toolbar → redirige a HomeActivity
        ImageView logoIcon = findViewById(R.id.logo_icon);
        if (logoIcon != null) {
            logoIcon.setOnClickListener(v -> {
                Intent intent = new Intent(HealthActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        // Footer navigation
        setupFooterNavigation();
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
            // Ya estás aquí
            ivHealth.setOnClickListener(v -> {});
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

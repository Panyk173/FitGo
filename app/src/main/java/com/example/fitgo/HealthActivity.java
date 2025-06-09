package com.example.fitgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HealthActivity extends AppCompatActivity {

    private TextView tvStepsToday;
    private TextView tvWeightBody;
    private TextView tvBodyFat;
    private TextView tvWater;
    private TextView tvSleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        tvWeightBody  = findViewById(R.id.tvWeightBody);
        tvBodyFat     = findViewById(R.id.tvBodyFat);
        tvWater       = findViewById(R.id.tvWater);
        tvSleep       = findViewById(R.id.tvSleep);

        // Botón para gráfico del sueño
        Button btnSleepChart = findViewById(R.id.btnSleepChart);
        btnSleepChart.setOnClickListener(v -> {
            startActivity(new Intent(this, SleepChartActivity.class));
        });

        //Botón para gráfico del agua
        Button btnWaterChart = findViewById(R.id.btnWaterChart);
        btnWaterChart.setOnClickListener(v -> {
            startActivity(new Intent(this, WaterChartActivity.class));
        });

        //Botón para gráfico del peso
        Button btnWeightChart = findViewById(R.id.btnWeightChart);
        btnWeightChart.setOnClickListener(v -> {
            startActivity(new Intent(this, WeightChartActivity.class));
        });


        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String weightStr = prefs.getString("user_weight", "");
        String heightStr = prefs.getString("user_height", "");
        String waterStr  = prefs.getString("user_water", "");
        String sleepStr  = prefs.getString("user_sleep", "");

        if (!weightStr.isEmpty()) {
            tvWeightBody.setText("Peso corporal: " + weightStr + " kg");
        } else {
            tvWeightBody.setText("Peso corporal: -- kg");
        }

        if (!waterStr.isEmpty()) {
            tvWater.setText("Hidratación: " + waterStr + " l");
        } else {
            tvWater.setText("Hidratación: -- ml");
        }

        if (!sleepStr.isEmpty()) {
            tvSleep.setText("Sueño: " + sleepStr + " h");
        } else {
            tvSleep.setText("Sueño: -- h");
        }

        if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
            try {
                float weightKg = Float.parseFloat(weightStr);
                float heightMeters = Float.parseFloat(heightStr) / 100f;
                float imc = weightKg / (heightMeters * heightMeters);
                float fatPercent = (1.2f * imc) + (0.23f * 25f) - (10.8f * 1f) - 5.4f;
                if (fatPercent < 0) fatPercent = 0f;
                tvBodyFat.setText(String.format("Grasa corporal: %.1f %%", fatPercent));
            } catch (NumberFormatException e) {
                tvBodyFat.setText("Grasa corporal: -- %");
            }
        } else {
            tvBodyFat.setText("Grasa corporal: -- %");
        }

        ImageView logoIcon = findViewById(R.id.logo_icon);
        if (logoIcon != null) {
            logoIcon.setOnClickListener(v -> {
                Intent intent = new Intent(HealthActivity.this, HomeActivity.class);
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

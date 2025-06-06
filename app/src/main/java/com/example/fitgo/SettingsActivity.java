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

public class SettingsActivity extends AppCompatActivity {

    private Switch themeSwitch;
    private Button logoutButton;
    private EditText etWeight, etHeight;
    private Button btnSaveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        themeSwitch = findViewById(R.id.switch_theme);
        logoutButton = findViewById(R.id.button_logout);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        btnSaveData = findViewById(R.id.btnSaveData);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("dark_mode", false);
        themeSwitch.setChecked(darkMode);

        // Cargar datos guardados
        etWeight.setText(prefs.getString("user_weight", ""));
        etHeight.setText(prefs.getString("user_height", ""));

        AppCompatDelegate.setDefaultNightMode(darkMode ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        btnSaveData.setOnClickListener(v -> {
            String weight = etWeight.getText().toString().trim();
            String height = etHeight.getText().toString().trim();
            prefs.edit().putString("user_weight", weight).putString("user_height", height).apply();
            Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            finish();
        });

        // Toolbar: logo redirige a HomeActivity
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

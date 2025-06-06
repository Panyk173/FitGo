package com.example.fitgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Referencias a los iconos del footer
        ImageView ivHealth   = findViewById(R.id.ivHealth);
        ImageView ivWeight   = findViewById(R.id.ivWeight);
        ImageView ivContact  = findViewById(R.id.ivContact);
        ImageView ivRoutine  = findViewById(R.id.ivRoutine);
        ImageView ivSettings = findViewById(R.id.ivSettings);

        ivHealth.setOnClickListener(v ->
                startActivity(new Intent(this, HealthActivity.class))
        );
        ivWeight.setOnClickListener(v ->
                startActivity(new Intent(this, WeightActivity.class))
        );
        ivContact.setOnClickListener(v ->
                startActivity(new Intent(this, ContactActivity.class))
        );
        ivRoutine.setOnClickListener(v ->
                startActivity(new Intent(this, RoutineActivity.class))
        );
        ivSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class))
        );
    }
}

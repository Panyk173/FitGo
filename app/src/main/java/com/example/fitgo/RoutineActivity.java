package com.example.fitgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoutineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoutineAdapter adapter;
    private List<ExerciseGroup> groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        // 1) Logo en toolbar → MainActivity
        ImageView logoIcon = findViewById(R.id.logo_icon);
        if (logoIcon != null) {
            logoIcon.setOnClickListener(v -> {
                Intent intent = new Intent(RoutineActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        // 2) RecyclerView principal (grupos)
        recyclerView = findViewById(R.id.recyclerViewGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupList = getSampleGroups();
        adapter = new RoutineAdapter(this, groupList);
        recyclerView.setAdapter(adapter);

        // 3) Íconos navegación inferior
        ImageView ivRoutine  = findViewById(R.id.ivRoutine);
        ImageView ivSettings = findViewById(R.id.ivSettings);
        ImageView ivHealth   = findViewById(R.id.ivHealth);
        ImageView ivWeight   = findViewById(R.id.ivWeight);
        ImageView ivContact  = findViewById(R.id.ivContact);

        if (ivRoutine != null) {
            ivRoutine.setOnClickListener(v -> {
                // Ya estás en esta pantalla
            });
        }
        if (ivSettings != null) {
            ivSettings.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
            });
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

    private List<ExerciseGroup> getSampleGroups() {
        List<ExerciseGroup> groups = new ArrayList<>();

        // Grupo Pecho
        groups.add(new ExerciseGroup("Pecho", Arrays.asList(
                new Exercise("Press banca", R.drawable.ic_placeholder),
                new Exercise("Aperturas con mancuernas", R.drawable.ic_placeholder),
                new Exercise("Fondos en paralelas", R.drawable.ic_placeholder),
                new Exercise("Press inclinado con barra", R.drawable.ic_placeholder),
                new Exercise("Press declinado con mancuernas", R.drawable.ic_placeholder)
        )));

        // Grupo Espalda
        groups.add(new ExerciseGroup("Espalda", Arrays.asList(
                new Exercise("Peso muerto", R.drawable.ic_placeholder),
                new Exercise("Remo con barra", R.drawable.ic_placeholder),
                new Exercise("Jalón al pecho", R.drawable.ic_placeholder),
                new Exercise("Remo con mancuerna a un brazo", R.drawable.ic_placeholder),
                new Exercise("Pull-over con mancuerna", R.drawable.ic_placeholder)
        )));

        // Grupo Piernas
        groups.add(new ExerciseGroup("Piernas", Arrays.asList(
                new Exercise("Sentadillas", R.drawable.ic_placeholder),
                new Exercise("Prensa", R.drawable.ic_placeholder),
                new Exercise("Zancadas", R.drawable.ic_placeholder),
                new Exercise("Peso muerto rumano", R.drawable.ic_placeholder),
                new Exercise("Extensión de piernas", R.drawable.ic_placeholder)
        )));

        // Grupo Hombros
        groups.add(new ExerciseGroup("Hombros", Arrays.asList(
                new Exercise("Press militar", R.drawable.ic_placeholder),
                new Exercise("Elevaciones laterales", R.drawable.ic_placeholder),
                new Exercise("Pájaros", R.drawable.ic_placeholder),
                new Exercise("Press Arnold", R.drawable.ic_placeholder),
                new Exercise("Encogimientos de hombros", R.drawable.ic_placeholder)
        )));

        // Grupo Bíceps
        groups.add(new ExerciseGroup("Bíceps", Arrays.asList(
                new Exercise("Curl con barra", R.drawable.ic_placeholder),
                new Exercise("Curl alterno con mancuernas", R.drawable.ic_placeholder),
                new Exercise("Curl concentrado", R.drawable.ic_placeholder),
                new Exercise("Curl predicador", R.drawable.ic_placeholder),
                new Exercise("Curl martillo", R.drawable.ic_placeholder)
        )));

        // Grupo Tríceps
        groups.add(new ExerciseGroup("Tríceps", Arrays.asList(
                new Exercise("Extensiones en polea", R.drawable.ic_placeholder),
                new Exercise("Press cerrado", R.drawable.ic_placeholder),
                new Exercise("Fondos entre bancos", R.drawable.ic_placeholder),
                new Exercise("Extensión con mancuerna por encima de la cabeza", R.drawable.ic_placeholder),
                new Exercise("Patada de tríceps", R.drawable.ic_placeholder)
        )));

        // Grupo Core / Abdominales
        groups.add(new ExerciseGroup("Core / Abdominales", Arrays.asList(
                new Exercise("Plancha", R.drawable.ic_placeholder),
                new Exercise("Crunch abdominal", R.drawable.ic_placeholder),
                new Exercise("Elevaciones de piernas", R.drawable.ic_placeholder),
                new Exercise("Bicicleta en el aire", R.drawable.ic_placeholder),
                new Exercise("Crunch en máquina", R.drawable.ic_placeholder)
        )));

        // Grupo Glúteos / Abductores
        groups.add(new ExerciseGroup("Glúteos / Abductores", Arrays.asList(
                new Exercise("Hip thrust", R.drawable.ic_placeholder),
                new Exercise("Puente de glúteos", R.drawable.ic_placeholder),
                new Exercise("Patada de glúteo en polea", R.drawable.ic_placeholder),
                new Exercise("Sentadilla sumo", R.drawable.ic_placeholder),
                new Exercise("Elevación lateral de cadera", R.drawable.ic_placeholder)
        )));

        // Grupo Gemelos
        groups.add(new ExerciseGroup("Gemelos", Arrays.asList(
                new Exercise("Elevación de talones de pie", R.drawable.ic_placeholder),
                new Exercise("Elevación de talones sentado", R.drawable.ic_placeholder),
                new Exercise("Elevación unilateral de talón", R.drawable.ic_placeholder)
        )));

        return groups;
    }
}

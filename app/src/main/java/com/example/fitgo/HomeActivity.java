package com.example.fitgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private EditText etNote;
    private TextView tvCuriosity, tvMotivation;
    private Button btnSaveNote;
    private LinearLayout notesContainer;

    private final String[] motivaciones = {
            "¡Eres más fuerte de lo que crees!",
            "Empújate a ti mismo, nadie lo hará por ti.",
            "Cada paso cuenta. ¡Sigue adelante!",
            "Sin esfuerzo no hay recompensa.",
            "¡Hoy es un buen día para mejorar!",
            "No se trata de ser el mejor, se trata de ser mejor que ayer.",
            "Cree en ti y todo será posible.",
            "La constancia vence al talento."
    };

    private final String[] datosCuriosos = {
            "¿Sabías que beber agua antes de las comidas ayuda a quemar grasa?",
            "Tus músculos queman más calorías que la grasa incluso en reposo.",
            "Caminar 30 minutos al día mejora tu salud cardiovascular.",
            "La proteína es clave para reparar y fortalecer músculos.",
            "Dormir bien mejora el rendimiento físico y mental.",
            "El ejercicio reduce el estrés y mejora el estado de ánimo.",
            "La hidratación adecuada mejora tu metabolismo.",
            "Escuchar música durante el ejercicio mejora tu resistencia."
    };

    private SharedPreferences prefs;
    private Set<String> notas;

    private FirebaseFirestore db;
    private String userId;
    private Map<String, String> notasFirestoreIds = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Referencias visuales
        etNote = findViewById(R.id.etNote);
        tvCuriosity = findViewById(R.id.tvCuriosity);
        tvMotivation = findViewById(R.id.tvMotivation);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        notesContainer = findViewById(R.id.notesContainer);

        Random random = new Random();

        // Mostrar mensaje aleatorio
        tvCuriosity.setText("💡 " + datosCuriosos[random.nextInt(datosCuriosos.length)]);
        tvMotivation.setText(motivaciones[random.nextInt(motivaciones.length)]);

        // Cargar notas desde SharedPreferences
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        notas = new HashSet<>();

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Cargar notas desde Firestore
        db.collection("users").document(userId).collection("notes")
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String texto = doc.getString("texto");
                        if (texto != null && !notasFirestoreIds.containsKey(texto)) {
                            notas.add(texto);
                            notasFirestoreIds.put(texto, doc.getId());
                            agregarNotaCard(texto);
                        }
                    }
                    prefs.edit().putStringSet("notas_motivacionales", notas).apply();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar notas", Toast.LENGTH_SHORT).show());

        // Guardar nueva nota
        btnSaveNote.setOnClickListener(v -> {
            String nuevaNota = etNote.getText().toString().trim();
            if (!nuevaNota.isEmpty() && !notas.contains(nuevaNota)) {
                notas.add(nuevaNota);
                prefs.edit().putStringSet("notas_motivacionales", notas).apply();
                etNote.setText("");

                // Guardar en Firestore
                Map<String, Object> notaMap = new HashMap<>();
                notaMap.put("texto", nuevaNota);

                db.collection("users").document(userId).collection("notes")
                        .add(notaMap)
                        .addOnSuccessListener(docRef -> {
                            notasFirestoreIds.put(nuevaNota, docRef.getId());
                            agregarNotaCard(nuevaNota);
                        });
            }
        });

        // Footer navegación
        findViewById(R.id.ivHealth).setOnClickListener(v -> startActivity(new Intent(this, HealthActivity.class)));
        findViewById(R.id.ivWeight).setOnClickListener(v -> startActivity(new Intent(this, WeightActivity.class)));
        findViewById(R.id.ivContact).setOnClickListener(v -> startActivity(new Intent(this, ContactActivity.class)));
        findViewById(R.id.ivRoutine).setOnClickListener(v -> startActivity(new Intent(this, RoutineActivity.class)));
        findViewById(R.id.ivSettings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    // Metodo para crear una tarjeta visual con diseño mejorado
    private void agregarNotaCard(String texto) {
        CardView card = new CardView(this);
        card.setRadius(12f);
        card.setCardElevation(2f);
        card.setUseCompatPadding(true);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 8, 0, 8);
        card.setLayoutParams(cardParams);

        // Layout horizontal interno
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.setPadding(20, 16, 20, 16);
        innerLayout.setGravity(Gravity.CENTER_VERTICAL);

        // Texto
        TextView tv = new TextView(this);
        tv.setText("📌 " + texto);
        tv.setTextSize(18f);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        ));

        // Botón eliminar
        ImageView btnX = new ImageView(this);
        btnX.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        btnX.setColorFilter(0xFFDD2C00);
        btnX.setPadding(18, 18, 18, 18);

        GradientDrawable bgCircle = new GradientDrawable();
        bgCircle.setColor(0x11DD2C00);
        bgCircle.setCornerRadius(15f);
        btnX.setBackground(bgCircle);

        btnX.setOnClickListener(v -> {
            notesContainer.removeView(card);
            notas.remove(texto);
            prefs.edit().putStringSet("notas_motivacionales", notas).apply();

            // Eliminar de Firestore
            String firestoreId = notasFirestoreIds.get(texto);
            if (firestoreId != null) {
                db.collection("users").document(userId)
                        .collection("notes").document(firestoreId)
                        .delete();
            }
        });

        // Añadir todo
        innerLayout.addView(tv);
        innerLayout.addView(btnX);
        card.addView(innerLayout);
        notesContainer.addView(card);

        // Animación
        card.setAlpha(0f);
        card.animate().alpha(1f).setDuration(250).start();
    }
}

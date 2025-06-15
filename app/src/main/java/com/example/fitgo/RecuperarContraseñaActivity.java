package com.example.fitgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitgo.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RecuperarContraseñaActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnEnviarCodigo;
    private FirebaseFirestore db;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        etEmail = findViewById(R.id.etEmail);
        btnEnviarCodigo = findViewById(R.id.btnEnviarCodigo);
        db = FirebaseFirestore.getInstance();
        random = new Random();

        btnEnviarCodigo.setOnClickListener(v -> enviarCodigo());
    }

    private void enviarCodigo() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Introduce un correo");
            return;
        }

        String codigo = String.format("%06d", random.nextInt(999999));

        Map<String, Object> data = new HashMap<>();
        data.put("codigo", codigo);
        data.put("timestamp", System.currentTimeMillis());

        db.collection("codigos_recuperacion").document(email)
                .set(data)
                .addOnSuccessListener(unused -> {
                    enviarCorreoConCodigo(email, codigo);
                    Toast.makeText(this, "Código enviado a tu correo", Toast.LENGTH_LONG).show();
                    // Opcional: pasar a siguiente actividad
                    Intent intent = new Intent(this, VerificarCodigoActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar el código", Toast.LENGTH_SHORT).show();
                });
    }

    // ⚠️ Esta función es un marcador de lugar.
    // Necesitas usar EmailJS o Cloud Function para el envío real.
    private void enviarCorreoConCodigo(String email, String codigo) {
        Log.d("EMAIL_SIMULADO", "Se enviaría a " + email + " el código: " + codigo);
    }
}

package com.example.fitgo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerificarCodigoActivity extends AppCompatActivity {

    private EditText etCodigo, etNuevaContrasena;
    private Button btnCambiarContrasena;
    private FirebaseFirestore db;
    private String email;
    private static final long EXPIRACION_MILLIS = 10 * 60 * 1000; // 10 minutos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_codigo);

        etCodigo = findViewById(R.id.etCodigo);
        etNuevaContrasena = findViewById(R.id.etNuevaContrasena);
        btnCambiarContrasena = findViewById(R.id.btnCambiarContrasena);
        db = FirebaseFirestore.getInstance();

        email = getIntent().getStringExtra("email");

        btnCambiarContrasena.setOnClickListener(v -> verificarCodigoYActualizar());
    }

    private void verificarCodigoYActualizar() {
        String codigoIngresado = etCodigo.getText().toString().trim();
        String nuevaContrasena = etNuevaContrasena.getText().toString().trim();

        if (codigoIngresado.isEmpty() || nuevaContrasena.length() < 6) {
            Toast.makeText(this, "Verifica los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("codigos_recuperacion").document(email).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "No hay código enviado a este correo", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String codigoGuardado = doc.getString("codigo");
                    long timestamp = doc.getLong("timestamp");

                    if (codigoGuardado == null || timestamp == 0) {
                        Toast.makeText(this, "Datos de código inválidos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!codigoIngresado.equals(codigoGuardado)) {
                        Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long ahora = System.currentTimeMillis();
                    if (ahora - timestamp > EXPIRACION_MILLIS) {
                        Toast.makeText(this, "Código expirado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ✅ Código válido: continuar con cambio de contraseña
                    cambiarContrasena(nuevaContrasena);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al verificar el código", Toast.LENGTH_SHORT).show();
                });
    }

    private void cambiarContrasena(String nuevaContrasena) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // En este flujo deberías iniciar sesión temporalmente para permitir cambiarla:
        // Aquí se asume que el usuario ya ha iniciado sesión tras verificar el código,
        // si no, necesitarás un flujo alternativo con enlaces de restablecimiento

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Correo para restablecer contraseña enviado", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "No se pudo enviar el correo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


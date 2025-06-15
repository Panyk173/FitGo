package com.example.fitgo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// IMPORTS FIREBASE
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmailReg, edtPasswordReg, edtConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;                   // Enlace “¿Ya tienes cuenta? Iniciar sesión”

    private FirebaseAuth mAuth;                     // Firebase Auth
    private FirebaseFirestore db;                   // Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializamos FirebaseAuth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        // Referencias UI
        edtName            = findViewById(R.id.edtName);
        edtEmailReg        = findViewById(R.id.edtEmailReg);
        edtPasswordReg     = findViewById(R.id.edtPasswordReg);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister        = findViewById(R.id.btnRegister);
        tvGoToLogin        = findViewById(R.id.tvGoToLogin);

        // Listener registro
        btnRegister.setOnClickListener(v -> validateAndRegister());

        // Volver a login al pulsar “Iniciar sesión”
        tvGoToLogin.setOnClickListener(v -> {
            // Abre MainActivity (login)
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void validateAndRegister() {
        String name    = edtName.getText().toString().trim();
        String email   = edtEmailReg.getText().toString().trim();
        String pass    = edtPasswordReg.getText().toString();
        String confirm = edtConfirmPassword.getText().toString();

        // Validaciones básicas
        if (TextUtils.isEmpty(name)) {
            edtName.setError(getString(R.string.error_empty_name));
            edtName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            edtEmailReg.setError(getString(R.string.error_empty_email));
            edtEmailReg.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtPasswordReg.setError(getString(R.string.error_empty_password));
            edtPasswordReg.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirm)) {
            edtConfirmPassword.setError(getString(R.string.error_empty_confirm_password));
            edtConfirmPassword.requestFocus();
            return;
        }
        if (!pass.equals(confirm)) {
            edtConfirmPassword.setError(getString(R.string.error_password_mismatch));
            edtConfirmPassword.requestFocus();
            return;
        }

        // 1) Crear usuario en FirebaseAuth
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        // 2) Guardar perfil en Firestore
                        saveUserToFirestore(user.getUid(), name, email);
                    }
                })
                .addOnFailureListener(e -> {
                    // Mostrar error de registro
                    Toast.makeText(this, "Error registro: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void saveUserToFirestore(String uid, String name, String email) {
        // Documento users/{uid}
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        // Si no existe, lo creamos
                        Map<String,Object> profile = new HashMap<>();
                        profile.put("fullName", name);
                        profile.put("email", email);
                        profile.put("createdAt", FieldValue.serverTimestamp());
                        // **Asignamos aquí el monitor por defecto**
                        profile.put("monitorId", "6gMJXUWwhZVGwTnlHeiHmLkVPlw1");

                        db.collection("users")
                                .document(uid)
                                .set(profile)
                                .addOnSuccessListener(v -> {
                                    Toast.makeText(this, "Registrado correctamente",
                                            Toast.LENGTH_SHORT).show();
                                    // 3) Volver al login
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error guardando perfil: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    } else {
                        // Ya existía: volvemos al login
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error leyendo Firestore: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}

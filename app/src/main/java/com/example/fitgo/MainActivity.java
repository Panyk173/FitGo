package com.example.fitgo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtEmail, edtPassword;
    private Button btnContinue, btnGoogleSignIn;
    private TextView tvRegister, tvForgotPassword;
    private GoogleSignInClient mGoogleSignInClient;

    // Nuevo launcher moderno
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                // Aquí podrías autenticar en Firebase con account.getIdToken()
                                goToHome();
                            } catch (ApiException e) {
                                Toast.makeText(this, "Error login Google: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        edtEmail         = findViewById(R.id.edtEmail);
        edtPassword      = findViewById(R.id.edtPassword);
        btnContinue      = findViewById(R.id.btnContinue);
        tvRegister       = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnGoogleSignIn  = findViewById(R.id.btnGoogleSignIn);

        btnContinue.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String pass  = edtPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                edtEmail.setError(getString(R.string.error_empty_email));
                edtEmail.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(pass)) {
                edtPassword.setError(getString(R.string.error_empty_password));
                edtPassword.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> goToHome())
                    .addOnFailureListener(e -> Toast.makeText(
                            MainActivity.this,
                            "Error al iniciar sesión",
                            Toast.LENGTH_LONG).show());
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Recuperar contraseña…", Toast.LENGTH_SHORT).show();
        });

        // Google Sign-In setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Usar el launcher moderno
        btnGoogleSignIn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void goToHome() {
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }
}

package com.example.fitgo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private EditText    edtEmail, edtPassword;
    private Button      btnContinue, btnGoogleSignIn;
    private TextView    tvRegister, tvForgotPassword;
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * 1) Declaramos aquí el lanzador moderno para Google Sign-In.
     *    Este objeto sustituye onActivityResult(...) al usar el API de ActivityResultContracts.
     */
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // Este callback se ejecuta cuando vuelve el Intent de Google Sign-In
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Task<GoogleSignInAccount> task =
                                    GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
                                // 2) Obtenemos la cuenta de Google seleccionada por el usuario
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                if (account == null) {
                                    Log.e(TAG, "GoogleSignInAccount es null");
                                    Toast.makeText(
                                            this,
                                            "No se obtuvo cuenta de Google.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                    return;
                                }

                                // 3) Pedimos el ID token de esa cuenta
                                String idToken = account.getIdToken();
                                Log.d(TAG, "Google ID Token: " + idToken);

                                if (idToken != null) {
                                    // 4) Creamos una credencial de Firebase usando ese ID token
                                    AuthCredential credential =
                                            GoogleAuthProvider.getCredential(idToken, null);

                                    // 5) Iniciamos sesión en Firebase con la credencial
                                    mAuth.signInWithCredential(credential)
                                            .addOnSuccessListener(authResult -> {
                                                // Login correcto → vamos a HomeActivity
                                                goToHome();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Error al autenticar con Firebase: ", e);
                                                Toast.makeText(
                                                        MainActivity.this,
                                                        "Error al autenticar con Firebase:\n" + e.getLocalizedMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show();
                                            });
                                } else {
                                    Log.e(TAG, "ID Token de Google es null");
                                    Toast.makeText(
                                            this,
                                            "No se pudo obtener el ID Token de Google.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }

                            } catch (ApiException e) {
                                Log.e(TAG, "Error en Google Sign-In: " + e.getStatusCode(), e);
                                Toast.makeText(
                                        this,
                                        "Error login Google (ApiException): " + e.getStatusCode(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Configura Firebase App Check en modo debug (opcional)
        FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());

        // Referencias a las vistas
        edtEmail         = findViewById(R.id.edtEmail);
        edtPassword      = findViewById(R.id.edtPassword);
        btnContinue      = findViewById(R.id.btnContinue);
        tvRegister       = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnGoogleSignIn  = findViewById(R.id.btnGoogleSignIn);

        // 6) Listener para iniciar sesión con email/contraseña
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
                    .addOnSuccessListener(authResult -> {
                        goToHome();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al iniciar sesión con email/password: ", e);
                        Toast.makeText(
                                MainActivity.this,
                                "Error al iniciar sesión: " + e.getLocalizedMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });

        // 7) Ir a RegisterActivity al pulsar “¿No tienes cuenta? Regístrate”
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });

        // 8) Recuperar contraseña (solo muestra un Toast por ahora)
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad de recuperación de contraseña pendiente", Toast.LENGTH_SHORT).show();
        });

        // ==== Configuración de Google Sign-In para Firebase ====
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        )
                // Esto le indica a Google que queremos un ID Token válido para Firebase.
                // Asegúrate de que default_web_client_id coincide con el “Client ID” que te dio Firebase.
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 9) Cuando se pulsa “Iniciar sesión con Google”, lanzamos el Intent de Google Sign-In
        btnGoogleSignIn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            // En lugar de startActivityForResult, llamamos ahora a:
            googleSignInLauncher.launch(signInIntent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Si el usuario ya está autenticado en Firebase, vamos directamente a HomeActivity
        if (mAuth.getCurrentUser() != null) {
            goToHome();
        }
    }

    /** Abre HomeActivity y cierra esta pantalla */
    private void goToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

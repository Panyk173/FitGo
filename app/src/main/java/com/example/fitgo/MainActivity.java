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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private EditText    edtEmail, edtPassword;
    private Button      btnContinue, btnGoogleSignIn;
    private TextView    tvRegister, tvForgotPassword;
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * 1) Lanzador moderno para Google Sign-In.
     */
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Task<GoogleSignInAccount> task =
                                    GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
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
                                String idToken = account.getIdToken();
                                if (idToken != null) {
                                    AuthCredential credential =
                                            GoogleAuthProvider.getCredential(idToken, null);
                                    mAuth.signInWithCredential(credential)
                                            .addOnSuccessListener(authResult -> {
                                                // Login correcto → ahora sincronizamos el nombre
                                                syncDisplayNameAndGoHome();
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

        // 6) Email/Password
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
                        // Sincronizamos displayName antes de ir a Home
                        syncDisplayNameAndGoHome();
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

        // 7) Ir a RegisterActivity
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });

        // 8) Recuperar contraseña
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecuperarContraseñaActivity.class);
            startActivity(intent);
        });

        // Configuración Google Sign-In para Firebase
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        )
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogleSignIn.setOnClickListener(v ->
                googleSignInLauncher.launch(mGoogleSignInClient.getSignInIntent())
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            // Ya autenticado, sincronizamos el nombre y vamos
            syncDisplayNameAndGoHome();
        }
    }

    /**
     * 9) Recupera de Firestore el fullName y lo asigna como displayName en Auth,
     *    luego lanza HomeActivity.
     */
    private void syncDisplayNameAndGoHome() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // debería ser raro, pero volvemos al login
            finish();
            return;
        }
        String uid = user.getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener((DocumentSnapshot doc) -> {
                    if (doc.exists() && doc.getString("fullName") != null) {
                        String fullName = doc.getString("fullName");
                        // Actualizamos el perfil de FirebaseAuth
                        UserProfileChangeRequest profileUpdates =
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)
                                        .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task -> {
                                    // Ya con displayName, seguimos a Home
                                    goToHome();
                                });
                    } else {
                        // No hay fullName, vamos a Home igualmente
                        goToHome();
                    }
                })
                .addOnFailureListener(e -> {
                    // En error, vamos a Home igualmente
                    goToHome();
                });
    }

    /** Abre HomeActivity y cierra esta pantalla */
    private void goToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

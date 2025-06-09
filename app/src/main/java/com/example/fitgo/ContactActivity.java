package com.example.fitgo;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {

    private WebView webViewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Inicializar WebView y cargar tu página de contacto
        webViewContact = findViewById(R.id.webViewContact);
        webViewContact.getSettings().setJavaScriptEnabled(true);
        webViewContact.setWebViewClient(new WebViewClient());

        // Cambia esta URL por la de tu página de contacto en WordPress
        webViewContact.loadUrl("https://fitgo9.wordpress.com/");

        // Footer - Navegación
        ImageView ivHealth = findViewById(R.id.ivHealth);
        ImageView ivWeight = findViewById(R.id.ivWeight);
        ImageView ivContact = findViewById(R.id.ivContact); // permanece aquí
        ImageView ivRoutine = findViewById(R.id.ivRoutine);
        ImageView ivSettings = findViewById(R.id.ivSettings);

        ivHealth.setOnClickListener(v -> startActivity(new Intent(this, HealthActivity.class)));
        ivWeight.setOnClickListener(v -> startActivity(new Intent(this, WeightActivity.class)));
        ivRoutine.setOnClickListener(v -> startActivity(new Intent(this, RoutineActivity.class)));
        ivSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        // El botón Contacto ya está activo, así que no se redirige a sí mismo
    }
}

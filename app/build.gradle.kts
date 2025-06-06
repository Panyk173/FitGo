plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fitgo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fitgo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildToolsVersion = "35.0.0"
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // 1) Importa el BoM con la versión correcta
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

    // 2) Ahora las dependencias Firebase SIN número de versión
    implementation("com.google.firebase:firebase-auth")                // Auth
    implementation("com.google.firebase:firebase-firestore")           // Firestore
    implementation("com.google.firebase:firebase-appcheck")            // App Check
    implementation("com.google.firebase:firebase-appcheck-debug")      // Provider DEBUG
    // Retrofit para llamadas HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Conversor de JSON a objetos Java (Gson)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Interceptor de logging (opcional, pero muy útil para depurar)
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")


    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}

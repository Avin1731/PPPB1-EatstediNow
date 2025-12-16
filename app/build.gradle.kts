plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // TAMBAHAN: Aktifkan Plugin Google Services
    id("com.google.gms.google-services")
}

android {
    // PENTING: Gunakan Namespace Proyek BARU (Team)
    namespace = "com.example.eatstedinow"
    compileSdk = 36

    defaultConfig {
        // PENTING: Application ID harus sesuai dengan yang didaftarkan di Firebase Console
        applicationId = "com.example.eatstedinow"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        // Kita fokus pakai Compose, ViewBinding dimatikan saja biar ringan
        compose = true
        // viewBinding = true // Hapus ini, karena kita move on ke Compose
    }
}

dependencies {
    // --- BAWAAN PROYEK BARU (COMPOSE) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation & Gambar (Penting untuk Compose)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.3")

    // --- TAMBAHAN DARI PROYEK LAMA (FIREBASE) ---
    // Saya tulis manual versinya agar tidak error jika libs.versions.toml belum diupdate

    // 1. Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))

    // 2. Firebase Auth & Google Sign In
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.4.0") // Versi stabil

    // 3. Firestore
    implementation("com.google.firebase:firebase-firestore")

    // 4. Analytics (Opsional)
    implementation("com.google.firebase:firebase-analytics")

    // Library untuk Splash Screen kustom
    implementation("androidx.core:core-splashscreen:1.2.0")

    // 1. Buat ngilangin error merah di themes.xml (Wajib ada)
    implementation("com.google.android.material:material:1.12.0")

    // --- TESTING ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
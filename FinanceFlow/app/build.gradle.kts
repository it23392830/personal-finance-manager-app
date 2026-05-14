plugins {

    id("com.android.application")

    id("org.jetbrains.kotlin.android")

    id("org.jetbrains.kotlin.plugin.compose")

    id("com.google.gms.google-services")

    id("com.google.dagger.hilt.android")

    kotlin("kapt")
}

android {

    namespace = "com.example.financeflow"

    compileSdk = 35

    defaultConfig {

        applicationId = "com.example.financeflow"

        minSdk = 26

        targetSdk = 35

        versionCode = 1

        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {

        sourceCompatibility =
            JavaVersion.VERSION_11

        targetCompatibility =
            JavaVersion.VERSION_11
    }

    buildFeatures {

        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {

    // Core Android
    implementation(
        "androidx.core:core-ktx:1.13.1"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.8.3"
    )

    implementation(
        "androidx.activity:activity-compose:1.9.1"
    )

    // Compose BOM
    implementation(
        platform(
            "androidx.compose:compose-bom:2024.06.00"
        )
    )

    implementation(
        "androidx.compose.ui:ui"
    )

    implementation(
        "androidx.compose.ui:ui-graphics"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    implementation(
        "androidx.compose.material3:material3"
    )

    // Navigation
    implementation(
        "androidx.navigation:navigation-compose:2.7.7"
    )

    // Lifecycle + ViewModel
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.8.3"
    )

    // Firebase BOM
    implementation(
        platform(
            "com.google.firebase:firebase-bom:33.1.0"
        )
    )

    // Firebase Auth
    implementation(
        "com.google.firebase:firebase-auth-ktx"
    )

    // Firestore
    implementation(
        "com.google.firebase:firebase-firestore-ktx"
    )

    // Hilt
    implementation(
        "com.google.dagger:hilt-android:2.51.1"
    )

    kapt(
        "com.google.dagger:hilt-compiler:2.51.1"
    )

    implementation(
        "androidx.hilt:hilt-navigation-compose:1.2.0"
    )

    // Coroutines
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"
    )

    // Testing
    testImplementation(
        "junit:junit:4.13.2"
    )

    androidTestImplementation(
        "androidx.test.ext:junit:1.2.1"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.6.1"
    )

    androidTestImplementation(
        platform(
            "androidx.compose:compose-bom:2024.06.00"
        )
    )

    androidTestImplementation(
        "androidx.compose.ui:ui-test-junit4"
    )

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )
}
import java.util.Properties
import java.io.FileInputStream

plugins {

    id("com.android.application")

    id("org.jetbrains.kotlin.android")

    id("org.jetbrains.kotlin.plugin.compose")

    id("com.google.gms.google-services")

    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
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

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY") ?: ""}\"")
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
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(17)
}

// Workaround: force Kotlin language/api version to 1.9 to avoid KAPT issues with Kotlin 2.0
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
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

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-android-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // WorkManager + Hilt integration
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Coroutines
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"
    )

    // Room (local persistence)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

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

    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.airbnb.android:lottie-compose:6.4.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("io.coil-kt:coil-compose:2.6.0")
}
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.student3"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.student3"
        minSdk = 24
        targetSdk = 35
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core)

    // AppCompat and Material Design
    implementation(libs.appcompat)
    implementation(libs.material)

    // Activity and Fragment
    implementation(libs.activity)
    implementation(libs.fragment)

    // ConstraintLayout
    implementation(libs.constraintlayout)

    // Room Database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.common)

    // Lifecycle (for LiveData and ViewModel)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Navigation Component
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Glide for image loading
    implementation(libs.glide)

    // WorkManager for background tasks
    implementation("androidx.work:work-runtime:2.9.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
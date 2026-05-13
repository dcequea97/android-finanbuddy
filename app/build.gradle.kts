plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.serialization)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.finanbuddy"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.finanbuddy"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        // Replace this value with your real API URL (must end with '/').
        buildConfigField("String", "API_BASE_URL", "\"https://script.google.com/macros/s/AKfycbwbahlNqxvL6hcdTeGQDszuySw2EnGBBg8hWmJO9ywV_eReTaX2sxAgYwBVxxe5BDSUHw/\"")

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
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    //Nav3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    implementation(libs.compose.material.icons)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.gms.play.services.auth)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.retrofitCore)
    implementation(libs.okhttpLoggingInterceptor)
    implementation(libs.gsonConverterFactory)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinxSerializationJson)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    implementation (libs.compose.charts)
}
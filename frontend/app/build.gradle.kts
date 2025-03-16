import java.util.Properties

val envPropertiesFile = rootProject.file("env.properties")
val envProperties = Properties().apply {
    if (envPropertiesFile.exists()) {
        envPropertiesFile.inputStream().use { load(it) }
    }
}

fun getEnvProperty(key: String, defaultValue: String): String =
    envProperties.getProperty(key, defaultValue)

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
    kotlin("plugin.serialization") version "2.0.21"

    // Kotlin annotation processor compiler plugin (i.e., the @Something metadata tags)
    id("kotlin-kapt")

    // Hilt plugin for Dagger
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.bmexcs.pickpic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bmexcs.pickpic"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "WEB_CLIENT_ID", getEnvProperty("WEB_CLIENT_ID", "\"\""))
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
        compose = true
        buildConfig = true
    }

    tasks.register<Wrapper>("wrapper") {
        gradleVersion = "5.6.4"
    }

    tasks.register("prepareKotlinBuildScriptModel"){}
}

dependencies {
    // Core AndroidX libraries
    implementation(libs.androidx.core.ktx) // Kotlin extensions for Android core components
    implementation(libs.androidx.appcompat) // Support for older Android versions

    // Lifecycle and Activity
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle-aware components
    implementation(libs.androidx.activity.compose) // Jetpack Compose support for activities

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom)) // Compose BOM (Bill of Materials) for version management
    implementation(libs.androidx.ui) // Core UI components
    implementation(libs.androidx.ui.graphics) // Graphics utilities
    implementation(libs.androidx.ui.tooling.preview) // Preview support
    implementation(libs.androidx.material3) // Material 3 UI components
    implementation(libs.androidx.runtime.livedata.v154) // LiveData integration for Compose
    implementation(libs.androidx.material.icons.extended) // Material icons

    // Firebase
    implementation(platform(libs.firebase.bom)) // Firebase BOM for version management
    implementation(libs.firebase.common.ktx) // Firebase common library for Kotlin
    implementation(libs.firebase.auth.ktx) // Firebase Authentication
    implementation(libs.play.services.auth) // Google Sign-In with Play Services

    // Credential Manager
    implementation(libs.androidx.credentials) // Credential Manager API
    implementation(libs.androidx.credentials.play.services.auth) // Play Services integration for credentials
    implementation(libs.googleid) // Google Sign-In integration with AuthorizationClient API

    // Testing dependencies
    testImplementation(libs.junit) // Unit testing framework

    // Networking
    implementation(libs.okhttp) // OkHttp for HTTP requests

    // Image Loading
    implementation(libs.coil.compose) // Coil image loading for Jetpack Compose

    // JSON Serialization
    implementation(libs.kotlinx.serialization.json) // Kotlinx serialization for JSON
    implementation(libs.gson) // Parse requests

    // Navigation
    implementation(libs.androidx.navigation.compose) // Jetpack Compose navigation
    implementation(libs.androidx.navigation.fragment) // Navigation for Fragments
    implementation(libs.androidx.navigation.ui) // Navigation UI helpers
    implementation(libs.androidx.navigation.dynamic.features.fragment) // Feature module support

    // DI management
    implementation(libs.hilt.android) // Hilt library for reducing DI boilerplate
    kapt(libs.hilt.android.compiler) // Hilt compiler
    implementation(libs.androidx.hilt.navigation.compose) // Hilt compose navigation

    // Testing dependencies
    testImplementation(libs.junit) // Unit testing framework

    // Android instrumentation tests
    androidTestImplementation(libs.androidx.junit) // JUnit support for Android tests
    androidTestImplementation(libs.androidx.espresso.core) // UI testing with Espresso
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose BOM for test dependencies
    androidTestImplementation(libs.androidx.ui.test.junit4) // JUnit4 support for Compose tests
    androidTestImplementation(libs.androidx.navigation.testing) // Navigation testing

    // QR code
    implementation(libs.zxing.android.embedded)

    // Debug-only dependencies
    debugImplementation(libs.androidx.ui.tooling) // Compose UI tooling for debugging
    debugImplementation(libs.androidx.ui.test.manifest) // Manifest file support for UI tests
}

kapt {
    correctErrorTypes = true
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.google.hilt.android)
    require(
        // Ensure KSP version is compatible with Kotlin version
        libs.versions.ksp
            .get()
            .startsWith(libs.versions.kotlin.get()),
    )
}

android {
    namespace = "day.vitayuzu.neodb"
    compileSdk = 35

    defaultConfig {
        applicationId = "day.vitayuzu.neodb"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.slf4j.android)
    // Jetpack Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    // androidTestImplementation(composeBom)
    debugImplementation(composeBom)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icon.core)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    // UI Lib
    implementation(libs.shimmer)
    // Kotlin
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    // Hilt
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    // Http client
    implementation(libs.ktorfit.lib)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    // Oauth2
    implementation(libs.datastore.preferences)
    implementation(libs.ktor.client.auth)
    implementation(libs.androidx.browser)
    // Test
    testImplementation(libs.junit4)
}

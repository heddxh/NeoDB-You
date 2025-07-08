import com.android.build.api.dsl.SigningConfig
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.io.FileInputStream
import java.util.Properties
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

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

kotlin {
    compilerOptions {
        jvmToolchain(21)
        languageVersion = KotlinVersion.KOTLIN_2_2
        apiVersion = KotlinVersion.KOTLIN_2_2
        progressiveMode = true
    }
}

// Sign config
val isGithubActions = System.getenv("GITHUB_ACTIONS").toBoolean()

private fun getEnv(key: String): String? = System.getenv(key)?.takeIf { it.isNotBlank() }

@OptIn(ExperimentalEncodingApi::class)
private fun configureSigning(signingConfigs: NamedDomainObjectContainer<out SigningConfig>) {
    if (isGithubActions) {
        // Read from env
        val keyBase64 = getEnv("SIGN_KEY_BASE64")
        val keyPassword = getEnv("SIGN_KEY_PWD")
        val keyAlias = getEnv("SIGN_KEY_ALIAS")

        requireNotNull(keyBase64) { "Missing SIGN_KEY_BASE64" }
        requireNotNull(keyPassword) { "Missing SIGN_KEY_PWD" }
        requireNotNull(keyAlias) { "Missing SIGN_KEY_ALIAS" }

        val buildDir = layout.buildDirectory.asFile
            .get()
            .apply { mkdirs() }
        val tmpFile = File.createTempFile("sign-", ".ks", buildDir)
        try {
            tmpFile.writeBytes(Base64.decode(keyBase64))
            tmpFile.deleteOnExit()
            signingConfigs.create("release") {
                storeFile = tmpFile
                storePassword = keyPassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        } catch (e: Exception) {
            tmpFile.delete()
            throw e
        }
    } else {
        // Read from file
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        require(keystorePropertiesFile.exists()) { println("Missing keystore.properties") }
        val keystoreProperties =
            Properties().apply { load(FileInputStream(keystorePropertiesFile)) }
        val keyAlias = keystoreProperties.getProperty("keyAlias")
        val keyPassword = keystoreProperties.getProperty("keyPassword")
        val storeFile = keystoreProperties.getProperty("storeFile")
        val storePassword = keystoreProperties.getProperty("storePassword")

        requireNotNull(keyAlias) { "Missing keyAlias in keystore.properties" }
        requireNotNull(keyPassword) { "Missing keyPassword in keystore.properties" }
        requireNotNull(storeFile) { "Missing storeFile in keystore.properties" }
        requireNotNull(storePassword) { "Missing storePassword in keystore.properties" }

        signingConfigs.create("release") {
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
            this.storeFile = file(storeFile)
            this.storePassword = storePassword
        }
    }
}

android {
    signingConfigs { configureSigning(this) }
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
            signingConfigs.findByName("release")?.let {
                signingConfig = it
            }
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
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

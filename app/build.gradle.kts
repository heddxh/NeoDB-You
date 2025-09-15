import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.io.FileInputStream
import java.util.Properties
import kotlin.io.encoding.Base64

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.google.hilt.android)
    alias(libs.plugins.aboutlibraries.android)
    // Ensure KSP version is compatible with Kotlin version
    require(
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
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

android {
    namespace = "day.vitayuzu.neodb"
    compileSdk = 36

    defaultConfig {
        applicationId = android.namespace
        targetSdk = android.compileSdk
        minSdk = 24
        versionCode = 5 // TODO: Verify increment before release
        versionName = "1.0.3"

        manifestPlaceholders["auth"] = namespace.toString()
        manifestPlaceholders["app_name"] = "NeoDB You"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            // Return null to get unsigned apk to make RB happy.
            configureSigning()
            signingConfig = signingConfigs.findByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["app_name"] = "NeoDB You Debug"
        }
        // Release build without R8 and lint for faster build time.
        register("fastRelease") {
            initWith(getByName("release"))
            isMinifyEnabled = false
            isShrinkResources = false
            lint.checkReleaseBuilds = false
        }
    }

    packaging {
        // https://issuetracker.google.com/issues/356109544
        jniLibs.keepDebugSymbols.addAll(
            setOf("**/libandroidx.graphics.path.so", "**/libdatastore_shared_counter.so"),
        )
        // https://github.com/Kotlin/kotlinx.coroutines?tab=readme-ov-file#avoiding-including-the-debug-infrastructure-in-the-resulting-apk
        // https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-debug/README.md#debug-agent-and-android
        // https://youtrack.jetbrains.com/issue/IDEA-335195
        resources.excludes += "DebugProbesKt.bin"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    @Suppress("UnstableApiUsage")
    androidResources.generateLocaleConfig = true
}

dependencies {
    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.slf4j.android)
    implementation(libs.kotlinx.coroutines.android)
    // Jetpack Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    // androidTestImplementation(composeBom)
    // debugImplementation(composeBom)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icon.core)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // UI Lib
    implementation(libs.shimmer)
    implementation(libs.aboutlibraries.m3)
    // Navigation 3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    // Kotlin
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    // Hilt
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)
    implementation(libs.androidx.hilt.viewmodel)
    // Http client
    implementation(libs.ktorfit.lib.light)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)
    // Oauth2
    implementation(libs.datastore.preferences)
    implementation(libs.ktor.client.auth)
    implementation(libs.androidx.browser)
    // Other
    implementation(libs.versionCompare)
}

// Signing config
private fun configureSigning() {
    if (System.getenv("GITHUB_ACTIONS").toBoolean()) {
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
            android.signingConfigs.register("release") {
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
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        // Unsigned for IzzyOnDroid
        if (!keystorePropertiesFile.exists()) {
            println("Missing keystore.properties, create unsigned APK")
            return
        }
        // Read from file
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

        android.signingConfigs.register("release") {
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
            this.storeFile = file(storeFile)
            this.storePassword = storePassword
        }
    }
}

private fun getEnv(key: String): String? = System.getenv(key)?.takeIf { it.isNotBlank() }

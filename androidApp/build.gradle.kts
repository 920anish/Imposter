import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.application)
}

val signingPropsFile =
    listOf(
        file("/run/secrets/imposter-signing-properties"),
        rootProject.file("keystore.properties"),
    ).firstOrNull { it.canRead() }

val signingProps =
    Properties().apply {
        signingPropsFile?.inputStream()?.use { load(it) }
    }

android {
    namespace = "com.imposter.play.androidApp"
    compileSdk = 37

    defaultConfig {
        minSdk = 23
        targetSdk = 37

        applicationId = "com.imposter.play"
        versionCode = 5
        versionName = "1.0.4"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    signingConfigs {
        if (signingPropsFile != null) {
            create("release") {
                storeFile = file(signingProps.getProperty("storeFile"))
                storePassword = signingProps.getProperty("storePassword")
                keyAlias = signingProps.getProperty("keyAlias")
                keyPassword = signingProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            if (signingPropsFile != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
}

dependencies {
    implementation(project(":sharedUI"))
    implementation(libs.androidx.activityCompose)
}

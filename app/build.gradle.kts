import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    // `org.jetbrains.kotlin.android` is intentionally not applied: AGP 9 has
    // built-in Kotlin support and rejects the Kotlin Android plugin.
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

/**
 * Release signing config is loaded from `keystore.properties`, which is
 * deliberately git-ignored. When the file is absent the release build is left
 * unsigned on purpose: CI signs the artifact with the `SIGNING_KEY` secret via
 * `r0adkll/sign-android-release`.
 *
 * The previous setup hard-coded `storePassword 'finance'` in this file and
 * committed `finance.jks` to the repository. That secret must be treated as
 * compromised and rotated.
 */
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        FileInputStream(keystorePropertiesFile).use { load(it) }
    }
}
val hasReleaseSigning = keystoreProperties.getProperty("storeFile") != null

android {
    namespace = "com.financialcalculator"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.horizonlabs.financialcalculator"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        versionCode = 15
        versionName = "1.1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField(
                "int",
                "SPLASH_WAIT_TIME_IN_SECONDS",
                providers.gradleProperty("DEBUG_SPLASH_WAIT_TIME_IN_SECONDS").get(),
            )
            buildConfigField(
                "boolean",
                "IS_IN_DEBUG_MODE",
                providers.gradleProperty("DEBUG_IS_IN_DEBUG_MODE").get(),
            )
            buildConfigField(
                "String",
                "HOST",
                "\"${providers.gradleProperty("DEBUG_HOST").get()}\"",
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }

            buildConfigField(
                "int",
                "SPLASH_WAIT_TIME_IN_SECONDS",
                providers.gradleProperty("RELEASE_SPLASH_WAIT_TIME_IN_SECONDS").get(),
            )
            buildConfigField(
                "boolean",
                "IS_IN_DEBUG_MODE",
                providers.gradleProperty("RELEASE_IS_IN_DEBUG_MODE").get(),
            )
            buildConfigField(
                "String",
                "HOST",
                "\"${providers.gradleProperty("RELEASE_HOST").get()}\"",
            )
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
        warningsAsErrors = false
        // The legacy code base still has a large number of View/XML based
        // suppressions that are resolved as the Compose migration progresses.
        disable += setOf("GradleDependency", "AndroidGradlePluginVersion", "OldTargetApi")
    }

    packaging {
        resources.excludes += setOf(
            "/META-INF/{AL2.0,LGPL2.1}",
            "/META-INF/DEPENDENCIES",
        )
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.play.app.update)
    implementation(libs.play.app.update.legacy)

    // Legacy networking stack. Retained verbatim so the existing Java sources
    // keep compiling; replaced by Coroutines + kotlinx.serialization in the
    // networking phase of MIGRATION_PLAN.md.
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.adapter.rxjava2)
    implementation(libs.retrofit.adapter.rxjava)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.gson)

    // Legacy image loading, replaced by Coil in the images phase.
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Room still runs on annotationProcessor until Phase 3 moves it to KSP.
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.junit)
    debugImplementation(libs.compose.ui.test.manifest)
}
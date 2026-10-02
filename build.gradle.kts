// AGP 9 provides built-in Kotlin support, but it ships with a pinned runtime
// dependency on the Kotlin Gradle Plugin (2.2.10 for AGP 9.0). Declaring a
// newer KGP here upgrades built-in Kotlin to the version we actually target.
// See https://developer.android.com/build/migrate-to-built-in-kotlin
buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
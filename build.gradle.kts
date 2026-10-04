plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
}

allprojects {
    group = "com.horizonlabs.financialcalculator"
    version = "1.0.0"
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
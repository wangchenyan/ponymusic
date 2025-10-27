plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    if (File("app/google-services.json").exists()) {
        println("Enable gms in root plugins")
        alias(libs.plugins.gms) apply false
        alias(libs.plugins.crashlytics) apply false
    }
}

buildscript {
    dependencies {
        classpath(libs.crouter.plugin)
    }
}

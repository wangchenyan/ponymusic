import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    //alias(libs.plugins.gms)
    //alias(libs.plugins.crashlytics)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("auto-register")
    id("org.greenrobot.greendao")
}

android {
    namespace = "me.wcy.music"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "me.wcy.music"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 131
        versionName = "1.3.1"

        multiDexEnabled = true

        ndk {
            abiFilters.apply {
                add("armeabi-v7a")
                add("arm64-v8a")
            }
        }
    }

    buildFeatures {
        viewBinding = true
    }

    signingConfigs {
        register("release") {
            enableV1Signing = true
            enableV2Signing = true
            storeFile = file("wangchenyan.keystore")
            storePassword = getLocalValue("STORE_PASSWORD")
            keyAlias = getLocalValue("KEY_ALIAS")
            keyPassword = getLocalValue("KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.valueOf(libs.versions.java.get())
        targetCompatibility = JavaVersion.valueOf(libs.versions.java.get())
    }
}

fun getLocalValue(key: String): String {
    return getLocalValue(key, false)
}

fun getLocalValue(key: String, quot: Boolean): String {
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    var value = if (properties.containsKey(key)) properties[key].toString() else ""
    if (quot) {
        value = "\"" + value + "\""
    }
    return value
}

kapt {
    // For hilt: Allow references to generated code
    correctErrorTypes = true
}

//greendao {
//    schemaVersion = 1
//    targetGenDir = File("src/main/java")
//    daoPackage = "${android.defaultConfig.applicationId}.storage.db.greendao"
//}

autoregister {
    registerInfo = listOf(
        // crouter 注解收集
        mapOf(
            "scanInterface" to "me.wcy.router.annotation.RouteLoader",
            "codeInsertToClassName" to "me.wcy.router.RouteSet",
            "codeInsertToMethodName" to "init",
            "registerMethodName" to "register",
            "include" to listOf("me/wcy/router/annotation/loader/.*")
        )
    )
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.media)
    implementation(libs.common)
    implementation("com.zhy:okhttputils:2.6.2")
    implementation("com.github.wangchenyan:lrcview:2.2")
    implementation("com.hwangjr.rxbus:rxbus:2.0.0")
    implementation("org.greenrobot:greendao:3.3.0")

    kapt(libs.hilt.compiler)
    implementation(libs.hilt)
}

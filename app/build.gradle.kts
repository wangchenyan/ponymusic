import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("auto-register")
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
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    var value = if (properties.containsKey(key)) {
        properties[key].toString()
    } else {
        ""
    }
    if (quot) {
        value = "\"" + value + "\""
    }
    return value
}

kapt {
    // For hilt: Allow references to generated code
    correctErrorTypes = true
}

ksp {
    arg("moduleName", project.name)
    // crouter 默认 scheme
    arg("defaultScheme", "app")
    // crouter 默认 host
    arg("defaultHost", "music")
    arg("room.schemaLocation", "$projectDir/schemas")
}

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
    implementation(libs.preference)
    implementation(libs.flexbox)

    ksp(libs.room.compiler)
    implementation(libs.room)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt)

    implementation(libs.common)
    ksp(libs.crouter.compiler)
    implementation(libs.crouter.api)
    implementation(libs.lrcview)

    implementation(libs.loggingInterceptor)
    implementation(libs.persistentCookieJar)
    implementation(libs.zbar)
    implementation(libs.blurry)
}

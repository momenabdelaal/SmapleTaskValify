plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.kotlin.compose)
//    alias(libs.plugins.room)
}

android {
    namespace = "com.valify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.valify"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Valify SDK
    implementation(libs.vidvocrsdk.v319)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.hilt.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
//    annotationProcessor(libs.room.compiler)



    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.hilt.navigation.compose)

    // Accompanist
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)

    // Network
    implementation(libs.retrofit)
    implementation(libs.com.squareup.retrofit2.converter.gson3)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // ML Kit
    implementation(libs.mlkit.face.detection)

    // Coil
    implementation(libs.coil.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
}
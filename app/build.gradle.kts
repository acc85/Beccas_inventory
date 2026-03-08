plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.raymond.beccasinventory"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.raymond.beccasinventory"
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        disable += setOf(
            "IconLauncherShape",       // Requires resizing raw image pixels
            "GradleDependency",        // AGP update breaks local gradle wrapper
            "ObsoleteLintCustomCheck", // Bug in compose BOM's lint module
            "OldTargetApi"             // AGP 8.2.2 cannot compile against API 36+
        )
    }
}

dependencies {
    implementation(project(":models"))
    implementation(project(":contracts"))
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.room.runtime)
    implementation(libs.coil.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.cbor)
    implementation(libs.kotlinx.collections.immutable)
    
    debugImplementation(libs.androidx.ui.tooling)
}


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "tv.vizbee.movidletv"
    compileSdk = 34

    defaultConfig {
        applicationId = "tv.vizbee.movidletv"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add the vizbee_app_id string resource
        resValue("string", "vizbee_app_id", "vzb7058786937")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    flavorDimensions += "platform"
    productFlavors {
        create("firetv") {
            dimension = "platform"
            applicationIdSuffix = ".firetv"
            versionNameSuffix = "-firetv"
        }
        create("androidtv") {
            dimension = "platform"
            applicationIdSuffix = ".androidtv"
            versionNameSuffix = "-androidtv"
        }
    }

    sourceSets {
        getByName("firetv") {
            res.srcDirs("src/firetv/res")
        }
        getByName("androidtv") {
            res.srcDirs("src/androidtv/res")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true  // Enable BuildConfig generation
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.leanback)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Vizbee
    "firetvImplementation"("tv.vizbee:firetv-receiver-sdk:4.2.4")
    "androidtvImplementation"("tv.vizbee:androidtv-receiver-sdk:4.1.7")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Unit Tests
    testImplementation(libs.junit)

    // UI Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
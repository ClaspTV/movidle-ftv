plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "tv.vizbee.movidletv"
    compileSdk = 34

    defaultConfig {
        applicationId = "tv.vizbee.movidletv"
        minSdk = 24
        targetSdk = 34
        versionCode = 5
        versionName = "0.1.4"

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
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.interactivemedia)
    implementation(libs.androidx.recyclerview)
    implementation("com.google.code.gson:gson:2.9.0")

    // Vizbee
//    "firetvImplementation"("tv.vizbee:firetv-receiver-sdk:4.2.4")
    "firetvImplementation"("tv.vizbee:firetv-receiver-sdk:4.2.5-rc9-x-messages")
    "androidtvImplementation"("tv.vizbee:androidtv-receiver-sdk:4.1.7")
    // X SDK
    implementation("tv.vizbee:android-receiver-x-sdk:1.0.0-rc13")

    // ExoPlayer
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.google.android.exoplayer:extension-mediasession:2.19.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Unit Tests
    testImplementation(libs.junit)

    // UI Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
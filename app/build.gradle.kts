 plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") version "4.4.0"
}

android {
    namespace = "com.example.moviesapp_latiris"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.moviesapp_latiris"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.volley)
    implementation(libs.glide)
    implementation(libs.play.services.location)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    
    // Google Auth
    implementation(libs.google.play.services.auth)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.material:material:1.11.0")
}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.ksp)


}

android {
    namespace = "com.example.geofencetracker"
    compileSdk {
        version = release(36)
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
    defaultConfig {
        applicationId = "com.example.geofencetracker"
        minSdk = 29
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
        debug {
            resValue("string", "app_name", "GeoFence Tracker")
        }
        release {
            resValue ("string", "app_name", "GeoFence Tracker")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Bottolm Nav Bar Dependency
    implementation ("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")
    // Room
    implementation(libs.room.ktx)
    // For Kotlin Coroutines support (recommended)
    implementation(libs.room.runtime)
    // WorkManager for background tasks
   ksp(libs.room.compiler)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.play.services)
    implementation(libs.androidx.work.runtime.ktx)

}

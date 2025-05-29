plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "hcmute.edu.projectfinal"
    compileSdk = 35

    defaultConfig {
        applicationId = "hcmute.edu.projectfinal"
        minSdk = 24
        targetSdk = 35
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("io.appwrite:sdk-for-android:7.0.1")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.12.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
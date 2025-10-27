plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
}

// Load local.properties
val localProperties = java.util.Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

// Get API keys from local.properties with fallback to test IDs
fun getLocalProperty(key: String, defaultValue: String = ""): String {
    return localProperties.getProperty(key) ?: defaultValue
}

android {
    namespace = "com.example.androidmycoffee"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.androidmycoffee"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add API keys to BuildConfig
        buildConfigField("String", "FIREBASE_WEB_CLIENT_ID", "\"${getLocalProperty("FIREBASE_WEB_CLIENT_ID", "")}\"")
        buildConfigField("String", "ADMOB_APP_ID", "\"${getLocalProperty("ADMOB_APP_ID", "ca-app-pub-3940256099942544~3347511713")}\"")
        buildConfigField("String", "ADMOB_APP_OPEN_AD_UNIT_ID", "\"${getLocalProperty("ADMOB_APP_OPEN_AD_UNIT_ID", "ca-app-pub-3940256099942544/9257395921")}\"")
        buildConfigField("String", "ADMOB_INTERSTITIAL_AD_UNIT_ID", "\"${getLocalProperty("ADMOB_INTERSTITIAL_AD_UNIT_ID", "ca-app-pub-3940256099942544/1033173712")}\"")
        buildConfigField("String", "ADMOB_REWARDED_AD_UNIT_ID", "\"${getLocalProperty("ADMOB_REWARDED_AD_UNIT_ID", "ca-app-pub-3940256099942544/5224354917")}\"")
        buildConfigField("String", "ADMOB_BANNER_AD_UNIT_ID", "\"${getLocalProperty("ADMOB_BANNER_AD_UNIT_ID", "ca-app-pub-3940256099942544/9214589741")}\"")

        // Add manifest placeholders for AdMob App ID
        manifestPlaceholders["ADMOB_APP_ID"] = getLocalProperty("ADMOB_APP_ID", "ca-app-pub-3940256099942544~3347511713")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // admob sdk
    implementation(libs.play.services.ads)

    // room
    implementation(libs.androidx.room.ktx.v261)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // nav
    implementation(libs.androidx.navigation.compose.v282)

    // lottie
    implementation(libs.lottie.compose)

    // in app purchase
    implementation(libs.billing.ktx)
    implementation(libs.billing)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)
}

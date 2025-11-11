plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // ✅ Plugin untuk Kotlin 2.0 dan Compose sudah benar
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

android {
    namespace = "com.example.lab_week_09"
    // ⬇️ UPDATE: Naikkan compileSdk ke 36 atau lebih tinggi (disarankan 36 sesuai kebutuhan BOM)
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.lab_week_09"
        minSdk = 24
        // ⬇️ UPDATE: Sebaiknya targetSdk disamakan dengan compileSdk
        targetSdk = 36
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

    buildFeatures {
        compose = true
    }

    // ⬇️ UPDATE: Hapus blok composeOptions ini
    // composeOptions {
    //     kotlinCompilerExtensionVersion = "1.5.14" // TIDAK DIPERLUKAN LAGI untuk Kotlin 2.0+
    // }

    compileOptions {
        // ⬇️ UPDATE: Gunakan JavaVersion.VERSION_1_8 atau yang lebih tinggi, 11 sudah bagus
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ✅ Menggunakan BOM adalah praktik terbaik
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // ✅ Dependensi Inti AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // ✅ Dependensi Jetpack Compose (versi akan diatur oleh BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // ⬇️ UPDATE: Gunakan versi activity-compose yang lebih baru jika diperlukan, 1.9.0 atau yang kompatibel
    implementation("androidx.activity:activity-compose:1.9.0")
    // ⬇️ UPDATE: Gunakan versi navigation-compose yang kompatibel, seperti 2.7.7
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ✅ Moshi (JSON Parser)
    implementation("com.squareup.moshi:moshi:1.15.1") // Versi minor update
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1") // Versi minor update

    // ✅ Debug & Testing
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

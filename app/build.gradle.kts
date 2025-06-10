plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // Projenizi oluştururken kullandığınız paket ismi neyse o olmalı.
    // Eğer farklıysa, lütfen burayı kendi paket isminizle değiştirin.
    namespace = "com.example.tekseferlikan"

    // Projenin derleneceği Android SDK versiyonu.
    // Bu, önceki hatayı çözmek için en kritik satırdır.
    compileSdk = 34

    defaultConfig {
        // Bu da yukarıdaki namespace ile genellikle aynı olur.
        applicationId = "com.example.tekseferlikan"

        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    // Kodumuzun çalışması için gerekli olan kütüphaneler
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // Coroutine'leri (lifecycleScope) kullanabilmemiz için bu kütüphane şart!
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")

    // Standart test kütüphaneleri
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
plugins {
    alias(libs.plugins.android.library)
    // id("org.jetbrains.kotlin.android") больше не нужен
}

android {
    namespace = "com.nemajor.unityapkinstaller"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Kotlin больше не нужен
    // implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    // implementation("androidx.core:core-ktx:1.10.1") // core-ktx тоже Kotlin, можно убрать если не используешь KTX функции
    implementation("androidx.core:core:1.10.1")
    implementation(libs.appcompat)
    implementation(libs.material)
}
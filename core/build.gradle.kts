plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "me.y9san9.lifetime.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    api(libs.compose.navigation)
}

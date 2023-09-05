plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "me.y9san9.lifetime.feature.statistics"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.mdi.compose)

    implementation(projects.core)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.material)

    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)

    implementation(libs.vico.core)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m2)
}

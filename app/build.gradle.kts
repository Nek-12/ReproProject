import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.nek12.ktordeadlockrepro"

    defaultConfig {
        applicationId = "com.nek12.ktordeadlockrepro"
        targetSdk = 34
        minSdk = 26
        compileSdk = 34
        versionCode = 1
        versionName = "0.0.1"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get().toString()
        useLiveLiterals = true
    }

    // removes kotlinx.coroutines debug bins
    packaging {
        resources {
            excludes += setOf(
                "DebugProbesKt.bin",
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/versions/9/previous-compilation-data.bin"
            )
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.bundles.compose)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.serialization)
    implementation(libs.paging.compose)
    implementation(libs.paging.common)
    implementation(libs.bundles.utils)
    implementation(libs.kotlinx.datetime)

    implementation(libs.koin.compose)
}
tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
}

plugins {
    kotlin("plugin.serialization") version libs.versions.kotlin.get() apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.android.gradle)
        classpath(libs.kotlin.gradle)
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.22")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.6")
    }
}
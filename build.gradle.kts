import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
//    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false



}

buildscript {
    repositories {
        google()
        mavenCentral()


    }
    dependencies {
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6") {
            exclude(group = "com.google.protobuf")
        }


    }



}
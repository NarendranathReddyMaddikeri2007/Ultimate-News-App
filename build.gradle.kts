buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0-RC" apply false
    //id("android.navigation.safeargs.kotlin") version "2.4.0"
    //id ("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.0")
}
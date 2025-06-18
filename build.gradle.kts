// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.0")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:11.3.2")
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.kotlinKsp) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    id("org.jetbrains.dokka") version "1.9.20"
    alias(libs.plugins.compose.compiler) apply false
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath ("com.github.ben-manes:gradle-versions-plugin:0.48.0")
        classpath ("gradle.plugin.com.onesignal:onesignal-gradle-plugin:[0.12.10, 0.99.99]")
    }
}
plugins {
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id ("com.github.ben-manes.versions") version "0.42.0"
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.0" apply false

    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}
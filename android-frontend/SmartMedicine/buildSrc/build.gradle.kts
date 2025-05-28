// Add Model -> Java or Kotlin Lib
plugins {
    `kotlin-dsl`            // 用kotlin写Config
    `java-gradle-plugin`    // 用Java写config
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}
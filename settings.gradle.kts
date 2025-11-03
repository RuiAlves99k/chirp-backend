pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
}
rootProject.name = "chirp"
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
include("app")
include("user")
include("chat")
include("notification")
include("common")
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

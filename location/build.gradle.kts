plugins {
    kotlin("multiplatform")
    id("jacoco")
    id("maven-publish")
    id("com.android.library")
    id("org.jlleitschuh.gradle.ktlint")
}

val ext = (gradle as ExtensionAware).extra

apply(from = "../gradle/publishable_component.gradle")

group = "com.splendo.kaluga"
version = ext["library_version"]!!

dependencies {
    val play_services_version = (gradle as ExtensionAware).extra["play_services_version"]
    implementation("com.google.android.gms:play-services-location:$play_services_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.5")
}

kotlin {
    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(project(":base", ""))
                implementation(project(":logging", ""))
            }
        }
        getByName("commonTest") {
            dependencies {
                implementation(project(":test-utils", ""))
            }
        }
    }
}

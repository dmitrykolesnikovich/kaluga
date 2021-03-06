plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.3.72"
    id("jacoco")
    id("maven-publish")
    id("com.android.library")
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
    jcenter()
}

apply(from = "../../gradle/component.gradle")

kotlin {
    sourceSets {
        commonMain {
            val ext =  (gradle as ExtensionAware).extra

            dependencies {

                if (!(ext["exampleAsRoot"] as Boolean)) {
                    implementation(project(":location", ""))
                    implementation(project(":base", ""))
                    implementation(project(":logging", ""))
                    implementation(project(":alerts", ""))
                    implementation(project(":keyboard", ""))
                    implementation(project(":permissions", ""))
                    implementation(project(":hud", ""))
                    implementation(project(":architecture", ""))
                } else {
                    val libraryVersion = ext["library_version"]
                    implementation("com.splendo.kaluga:location:$libraryVersion")
                    implementation("com.splendo.kaluga:base:$libraryVersion")
                    implementation("com.splendo.kaluga:logging:$libraryVersion")
                    implementation("com.splendo.kaluga:alerts:$libraryVersion")
                    implementation("com.splendo.kaluga:keyboard:$libraryVersion")
                    implementation("com.splendo.kaluga:permissions:$libraryVersion")
                    implementation("com.splendo.kaluga:hud:$libraryVersion")
                    implementation("com.splendo.kaluga:architecture:$libraryVersion")
                }
            }
        }
    }
}
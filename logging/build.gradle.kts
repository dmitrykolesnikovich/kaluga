plugins {
    kotlin("multiplatform")
    id("jacoco")
    id("maven-publish")
    id("com.android.library")
}

val ext =  (gradle as ExtensionAware).extra

apply(from = "../gradle/publishable_component.gradle")

group = "com.splendo.kaluga"
version = ext["library_version"]!!

repositories {
    mavenCentral()
}

android {
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("ru.pocketbyte.kydra:kydra-log:1.0.4")
            }
        }

        commonTest {
            dependencies {
                implementation("co.touchlab:stately-collections:1.0.0-a2")
            }
        }

    }

}

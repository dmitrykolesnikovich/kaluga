plugins {
    kotlin("multiplatform")
    id("jacoco")
    id("com.android.library")
    id("maven-publish")
    id("org.jlleitschuh.gradle.ktlint")
}

val ext = (gradle as ExtensionAware).extra

apply(from = "../gradle/publishable_component.gradle")

group = "com.splendo.kaluga"
version = ext["library_version"]!!

android {
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("ru.pocketbyte.kydra:kydra-log:1.0.5")
            }
        }
        commonTest {
            dependencies {
                val ext = (gradle as ExtensionAware).extra
                // Stately Isolite is in flux and not part of the current statelyVersion. Upgrade this when tracked properly
                implementation("co.touchlab:stately-isolate:1.0.2-a4")
                implementation("co.touchlab:stately-iso-collections:1.0.2-a4")
            }
        }
    }
}
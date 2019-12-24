val android_gradle_plugin_version:String by settings
val kotlin_version:String by settings

pluginManagement {

    repositories {
        gradlePluginPortal()
        google()
        jcenter()
    }

    resolutionStrategy {
        eachPlugin {

            if (requested.id.id == "kotlin-multiplatform") {
                // The version here must be kept in sync with gradle/ext.gradle and settings.gradle in the root
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlin_version}")
            }
            if (requested.id.id == "com.android.library") {
                useModule("com.android.tools.build:gradle:${android_gradle_plugin_version}")
            }
            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:${android_gradle_plugin_version}")
            }
        }
    }
}

/***********************************************
 *
 * Changes made to this file should also be reflected in the `settings.gradle` in the root of the project
 *
 ***********************************************/


apply("../../../gradle/ext.gradle")

val ext =  (gradle as ExtensionAware).extra

if (!(ext["exampleAsRoot"] as Boolean)) {
    include(":alerts")
    project(":alerts").projectDir = file("../../../alerts")

    include(":base")
    project(":base").projectDir = file("../../../base")

    include(":collectionView")
    project(":collectionView").projectDir = file("../../../collectionView")

    include(":hud")
    project(":hud").projectDir = file("../../../hud")

    include(":location")
    project(":location").projectDir = file("../../../location")

    include(":logging")
    project(":logging").projectDir = file("../../../logging")

    include(":permissions")
    project(":permissions").projectDir = file("../../../permissions")

    include(":test-utils")
    project(":test-utils").projectDir = file("../../../test-utils")
}

include(":android")
project(":android").projectDir = file("../../android")

include(":KotlinNativeFramework")
project(":KotlinNativeFramework").projectDir = file("../KotlinNativeFramework")

include(":shared")
project(":shared").projectDir = file("../../shared")

rootProject.name = file("..").name

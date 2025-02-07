pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            credentials {
                username = "sdk"
                password = "sdk123456"
            }
            url = uri("https://www.valifystage.com/artifactory/libs-release/")
        }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
}

rootProject.name = "ValifyTask"
include(":app")
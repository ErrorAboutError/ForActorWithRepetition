pluginManagement {
    repositories {
        google()
        mavenCentral()
        jcenter()
        gradlePluginPortal()
        maven {
            url = uri("https://maven.google.com/")
            url = uri("https://jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven {
            url = uri("https://maven.google.com/")
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "ForActorWithRepetition"
include(":app")
 
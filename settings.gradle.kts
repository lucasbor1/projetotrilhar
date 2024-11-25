pluginManagement {
    repositories {
        google() // Configurando o repositório Google
        mavenCentral() // Configurando o repositório Maven Central
        gradlePluginPortal() // Configurando o repositório Gradle Plugin Portal
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

    }
}

rootProject.name = "telaPi"
include(":app")

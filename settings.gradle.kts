pluginManagement {
    repositories {
        // Sólo aquí definimos los repositorios de plugins y de dependencias
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // Plugin de Google Services para Kotlin DSL
        id("com.google.gms.google-services") version "4.3.15"
    }
}

dependencyResolutionManagement {
    // Obliga a que TODOS los repositorios se declaren únicamente en este archivo
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "FitGo"
include(":app")

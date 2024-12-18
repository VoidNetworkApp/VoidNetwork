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
        // Mapbox Maven repository
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                // Add your Mapbox token here if required
                username = "mapbox"
                password = "sk.eyJ1IjoibWNhcmRvc285NCIsImEiOiJjbTRpdDM5bG4wNmRiMmtyM3Z0cW0xamNmIn0.ECJbNiR04gCmt4rkUHBbzQ"
            }
        }
    }
}

rootProject.name = "VoidNetwork"
include(":app")
 
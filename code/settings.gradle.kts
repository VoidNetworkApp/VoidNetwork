import org.w3c.dom.Element
import java.io.FileNotFoundException
import javax.xml.parsers.DocumentBuilderFactory

fun getMapboxAccessToken(): String {
    val secretsFile = file("app/src/main/res/values/secrets.xml")
    if (!secretsFile.exists()) {
        throw FileNotFoundException("secrets.xml not found at ${secretsFile.absolutePath}")
    }
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(secretsFile)
    val root = document.documentElement
    val stringElements = root.getElementsByTagName("string")
    return (0 until stringElements.length)
        .asSequence()
        .map { stringElements.item(it) as Element }
        .find { it.getAttribute("name") == "mapbox_access_token" }
        ?.textContent
        ?: throw IllegalArgumentException("mapbox_access_token not found in secrets.xml")
}

val mapboxKey = getMapboxAccessToken()
gradle.extra.set("mapboxKey", mapboxKey)

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
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = mapboxKey
            }
        }
    }
}

rootProject.name = "VoidNetwork"
include(":app")
 
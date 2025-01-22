import org.w3c.dom.Element
import java.io.FileNotFoundException
import javax.xml.parsers.DocumentBuilderFactory

fun getAllTokensFromSecrets(): Map<String, String> {
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
        .associate { it.getAttribute("name") to it.textContent }
}

fun getTokenByName(tokenName: String): String {
    val tokens = getAllTokensFromSecrets()
    return tokens[tokenName] ?: throw IllegalArgumentException("$tokenName not found in secrets.xml")
}

val mapboxKey = getTokenByName("mapbox_access_token")
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
 
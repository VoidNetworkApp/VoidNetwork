package fcul.cmov.voidnetwork.ui.navigation

sealed class Screens(val route: String) {
    object Main: Screens("main")
    object Portal: Screens("portal/{id}")
    object RegisterPortal : Screens("register_portal/{latitude}/{longitude}") {
        fun createRoute(latitude: Double, longitude: Double): String {
            return "register_portal/$latitude/$longitude"
        }
    }
    object Languages: Screens("languages")
    object Language: Screens("language/{id}")
    object AutomaticMessage: Screens("automatic_message")
    object ManualMessage: Screens("manual_message")
}

object Arguments {
    const val id = "id"
}
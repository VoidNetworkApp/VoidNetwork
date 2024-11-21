package fcul.cmov.voidnetwork.ui.navigation

sealed class Screens(val route: String) {
    object Main: Screens("main")
    object Portals: Screens("portals")
    object Portal: Screens("portal/{id}")
    object Languages: Screens("languages")
    object Language: Screens("language/{id}")
    object AutomaticMessage: Screens("automatic_message")
    object ManualMessage: Screens("manual_message")
}

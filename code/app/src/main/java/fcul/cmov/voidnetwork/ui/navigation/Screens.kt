package fcul.cmov.voidnetwork.ui.navigation

sealed class Screens(val route: String) {
    object Main: Screens("main")
    object RegisterPortal : Screens("register_portal")
    object Languages: Screens("languages")
    object Language: Screens("language/{id}")
    object Portal: Screens("portal/{id}")
}

object Arguments {
    const val id = "id"
}
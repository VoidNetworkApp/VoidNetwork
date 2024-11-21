package fcul.cmov.voidnetwork.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fcul.cmov.voidnetwork.ui.screens.MainScreen
import fcul.cmov.voidnetwork.ui.screens.communication.LanguageScreen
import fcul.cmov.voidnetwork.ui.screens.communication.LanguagesScreen
import fcul.cmov.voidnetwork.ui.screens.portal.RegisterPortalScreen
import fcul.cmov.voidnetwork.ui.utils.getArgument

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.Main.route
    ) {
        composable(route = Screens.Main.route) {
            MainScreen(nav = navController)
        }
        composable(route = Screens.Languages.route) {
            LanguagesScreen(nav = navController)
        }
        composable(route = Screens.Language.route) { navBackStack ->
            val id = navBackStack.getArgument(Arguments.id)
            LanguageScreen(nav = navController, id = id)
        }
        composable(route = Screens.RegisterPortal.route) {
            RegisterPortalScreen(nav = navController)
        }
    }
}
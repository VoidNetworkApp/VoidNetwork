package fcul.cmov.voidnetwork.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fcul.cmov.voidnetwork.ui.screens.MainScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.Main.route
    ) {
        composable(route = Screens.Main.route) {
            MainScreen(navController = navController)
        }
        // ...
    }
}
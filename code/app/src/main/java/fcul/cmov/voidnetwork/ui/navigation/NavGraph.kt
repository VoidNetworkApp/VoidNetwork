package fcul.cmov.voidnetwork.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fcul.cmov.voidnetwork.ui.screens.MainScreen
import fcul.cmov.voidnetwork.ui.screens.communication.LanguageScreen
import fcul.cmov.voidnetwork.ui.screens.communication.LanguagesScreen
import fcul.cmov.voidnetwork.ui.screens.portal.RegisterPortalScreen
import fcul.cmov.voidnetwork.ui.utils.getArgument
import fcul.cmov.voidnetwork.ui.viewmodels.CommunicationViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.MusicPlayerViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    val languageViewModel: LanguageViewModel = viewModel()
    val communicationViewModel: CommunicationViewModel = viewModel()
    val portalViewModel: PortalViewModel = viewModel()
    val musicPlayerViewModel: MusicPlayerViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Screens.Main.route
    ) {
        composable(route = Screens.Main.route) {
            MainScreen(
                nav = navController,
                communicationViewModel = communicationViewModel,
                portalViewModel = portalViewModel,
                musicPlayerViewModel = musicPlayerViewModel,
                languageViewModel = languageViewModel
            )
        }
        composable(route = Screens.Languages.route) {
            LanguagesScreen(
                nav = navController,
                viewModel = languageViewModel
            )
        }
        composable(route = Screens.Language.route) { navBackStack ->
            val id = navBackStack.getArgument(Arguments.id)
            LanguageScreen(
                nav = navController,
                viewModel = languageViewModel,
                id = id
            )
        }
        composable(route = Screens.RegisterPortal.route) { backStackEntry ->
            // Extract arguments from the back stack entry
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull()
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull()

            RegisterPortalScreen(
                nav = navController,
                viewModel = portalViewModel,
                latitude = latitude,
                longitude = longitude
            )
        }
    }
}
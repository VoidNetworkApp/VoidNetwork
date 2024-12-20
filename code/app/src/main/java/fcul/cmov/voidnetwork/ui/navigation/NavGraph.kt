package fcul.cmov.voidnetwork.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.ui.screens.MainScreen
import fcul.cmov.voidnetwork.ui.screens.communication.LanguageScreen
import fcul.cmov.voidnetwork.ui.screens.communication.LanguagesScreen
import fcul.cmov.voidnetwork.ui.screens.portal.RegisterPortalScreen
import fcul.cmov.voidnetwork.ui.utils.getArgument
import fcul.cmov.voidnetwork.ui.viewmodels.MessageSenderViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.MessageReceiverViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.factories.SharedViewModelFactory
import fcul.cmov.voidnetwork.ui.viewmodels.repository.RepositoryViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    val application = LocalContext.current.applicationContext as Application
    val languagesRepository: RepositoryViewModel = viewModel()
    val factory = remember { SharedViewModelFactory(application, languagesRepository) }
    val languageViewModel: LanguageViewModel = viewModel(factory = factory)
    val messageReceiverViewModel: MessageReceiverViewModel = viewModel(factory = factory)
    val messageSenderViewModel: MessageSenderViewModel = viewModel(factory = factory)
    val portalViewModel: PortalViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screens.Main.route
    ) {
        composable(route = Screens.Main.route) {
            MainScreen(
                nav = navController,
                messageSenderViewModel = messageSenderViewModel,
                portalViewModel = portalViewModel,
                messageReceiverViewModel = messageReceiverViewModel,
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
            val coordinatesStr = backStackEntry.arguments?.getString("coordinates")
            val coordinates = Coordinates.fromString(coordinatesStr)
            RegisterPortalScreen(
                nav = navController,
                viewModel = portalViewModel,
                coordinates = coordinates
            )
        }
    }
}
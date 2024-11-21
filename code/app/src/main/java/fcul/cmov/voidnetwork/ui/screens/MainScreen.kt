package fcul.cmov.voidnetwork.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.ui.screens.communication.CommunicationScreen
import fcul.cmov.voidnetwork.ui.utils.HorizontalPager
import fcul.cmov.voidnetwork.ui.screens.musicplayer.MusicPlayerScreen
import fcul.cmov.voidnetwork.ui.screens.portal.PortalScreen
import fcul.cmov.voidnetwork.ui.viewmodels.CommunicationViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.MusicPlayerViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel

@Composable
fun MainScreen(navController: NavController) {
    val communicationViewModel: CommunicationViewModel = viewModel()
    val languageViewModel: LanguageViewModel = viewModel()
    val portalViewModel: PortalViewModel = viewModel()
    val musicPlayerViewModel: MusicPlayerViewModel = viewModel()

    HorizontalPager(
        initialPage = 1,
        screens = mapOf(
            Icons.Filled.MusicNote to {
                MusicPlayerScreen(navController = navController)
            },
            Icons.Filled.WifiTethering to {
                CommunicationScreen(navController = navController)
            },
            Icons.Filled.LocationOn to {
                PortalScreen(navController = navController)
            }
        )
    )
}
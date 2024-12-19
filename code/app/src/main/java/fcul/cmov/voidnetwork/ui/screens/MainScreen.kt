package fcul.cmov.voidnetwork.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.ui.screens.communication.CommunicationScreen
import fcul.cmov.voidnetwork.ui.screens.musicplayer.MusicPlayerScreen
import fcul.cmov.voidnetwork.ui.screens.portal.PortalScreen
import fcul.cmov.voidnetwork.ui.utils.composables.HorizontalPager
import fcul.cmov.voidnetwork.ui.viewmodels.CommunicationViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.MusicPlayerViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel

@Composable
fun MainScreen(
    nav: NavController,
    communicationViewModel: CommunicationViewModel,
    languageViewModel: LanguageViewModel,
    portalViewModel: PortalViewModel,
    musicPlayerViewModel: MusicPlayerViewModel
) {
    HorizontalPager(
        initialPage = 1,
        screens = mapOf(
            Icons.Filled.MusicNote to {
                MusicPlayerScreen(
                    nav = nav,
                    viewModel = musicPlayerViewModel
                )
            },
            Icons.Filled.WifiTethering to { navigateToPage ->
                CommunicationScreen(
                    nav = nav,
                    viewModel = communicationViewModel,
                    portalSelected = portalViewModel.portalSelected,
                    languageSelected = languageViewModel.languageSelected,
                    navigateToPage = navigateToPage
                )
            },
            Icons.Filled.LocationOn to {
                PortalScreen(
                    nav = nav,
                    viewModel = portalViewModel
                )
            }
        )
    )
}
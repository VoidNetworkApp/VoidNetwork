package fcul.cmov.voidnetwork.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.ui.screens.communication.CommunicationScreen
import fcul.cmov.voidnetwork.ui.screens.messages.MessagesScreen
import fcul.cmov.voidnetwork.ui.screens.portal.PortalsScreen
import fcul.cmov.voidnetwork.ui.utils.composables.HorizontalPager
import fcul.cmov.voidnetwork.ui.viewmodels.MessageSenderViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.MessageReceiverViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel

@Composable
fun MainScreen(
    nav: NavController,
    messageSenderViewModel: MessageSenderViewModel,
    languageViewModel: LanguageViewModel,
    portalViewModel: PortalViewModel,
    messageReceiverViewModel: MessageReceiverViewModel,
    currentLocation: Coordinates?
) {
    HorizontalPager(
        initialPage = 1,
        screens = mapOf(
            Icons.AutoMirrored.Filled.Message to {
                MessagesScreen(
                    nav = nav,
                    viewModel = messageReceiverViewModel
                )
            },
            Icons.Filled.WifiTethering to { navigateToPage ->
                CommunicationScreen(
                    nav = nav,
                    viewModel = messageSenderViewModel,
                    currentPosition = currentLocation,
                    portals = portalViewModel.portals,
                    languageSelected = languageViewModel.languageSelected,
                    onTranslate = { languageViewModel.translate(it) },
                    onUpdateDictionary = { signal, message ->
                        languageViewModel.languageSelected?.let {
                            languageViewModel.updateLanguageDictionary(
                                id = it.id,
                                signal = signal,
                                message = message
                            )
                        }
                    },
                    navigateToPage = navigateToPage
                )
            },
            Icons.Filled.LocationOn to {
                PortalsScreen(
                    nav = nav,
                    viewModel = portalViewModel,
                    currentLocation = currentLocation
                )
            }
        )
    )
}
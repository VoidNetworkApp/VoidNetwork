package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Coordinates
import kotlinx.coroutines.delay

@Composable
fun rememberSensorsPopupState(currentPosition: Coordinates?): Pair<() -> Unit, @Composable () -> Unit> {
    val popupState = rememberSaveablePopupState()
    val luminosityProvider = rememberLuminosityProvider()
    var luminosity by rememberSaveable { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            if (popupState.isVisible) luminosity = luminosityProvider()
            delay(1000)
        }
    }

    val popup = @Composable {
        if (popupState.isVisible) {
            Popup(
                title = stringResource(R.string.sensors_info),
                onClose = popupState::hide,
                modifier = Modifier.fillMaxHeight(0.4f).fillMaxWidth(0.8f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = stringResource(R.string.current_position),
                        )
                        Text(
                            text = currentPosition.toString(),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    Row {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = stringResource(R.string.current_position),
                        )
                        Text(
                            text = luminosity.toString(),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
    return Pair(popupState::show, popup)
}
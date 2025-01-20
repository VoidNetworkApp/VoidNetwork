package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import fcul.cmov.voidnetwork.repository.SharedStateManager
import kotlinx.coroutines.delay

const val UPSIDE_DOWN_STATE_DURATION = 5000L
const val DARK_LUMINOSITY_THRESHOLD = 10f
const val PERIODIC_CHECK_INTERVAL = 1000L

@Composable
fun rememberUpsideDownState(): Boolean {
    var inUpsideDown by rememberSaveable { mutableStateOf(false) }
    val luminosityProvider = rememberLuminosityProvider()
    var elapsedTime by rememberSaveable { mutableStateOf(0L) }
    var inDark by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(inUpsideDown) {
        // this is needed to share the state to the foreground service
        SharedStateManager.inUpsideDown = inUpsideDown
    }

    LaunchedEffect(Unit) {
        while (true) {
            val currentLuminosity = luminosityProvider()
            val inDarkNow = currentLuminosity <= DARK_LUMINOSITY_THRESHOLD
            if (inDarkNow == inDark) {
                elapsedTime += PERIODIC_CHECK_INTERVAL
                if (elapsedTime >= UPSIDE_DOWN_STATE_DURATION) {
                    inUpsideDown = inDarkNow
                }
            } else {
                inDark = inDarkNow
                elapsedTime = 0L
            }

            delay(PERIODIC_CHECK_INTERVAL)
        }
    }

    return inUpsideDown
}
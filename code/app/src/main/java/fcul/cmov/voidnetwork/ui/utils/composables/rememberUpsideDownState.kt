package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

const val UPSIDE_DOWN_LIGHT_DURATION = 10000L
const val DARK_LUMINOSITY_THRESHOLD = 10f
const val LIGHT_CHANGE_THRESHOLD = 5f

@Composable
fun rememberUpsideDownState(): Boolean {
    val luminosityProvider = rememberLuminosityProvider()
    var inUpsideDown by rememberSaveable { mutableStateOf(false) }
    var lastLuminosity by rememberSaveable { mutableStateOf(0f) }
    var elapsedTime by rememberSaveable { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            val luminosity = luminosityProvider()
            if (kotlin.math.abs(luminosity - lastLuminosity) > LIGHT_CHANGE_THRESHOLD) {
                elapsedTime = 0
                lastLuminosity = luminosity
            } else {
                elapsedTime += 100
            }
            inUpsideDown = elapsedTime >= UPSIDE_DOWN_LIGHT_DURATION && luminosity < DARK_LUMINOSITY_THRESHOLD
        }
    }
    return inUpsideDown
}

package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import fcul.cmov.voidnetwork.ui.utils.LONG
import fcul.cmov.voidnetwork.ui.utils.MIN_LIGHT_CODE_DURATION_SHORT
import fcul.cmov.voidnetwork.ui.utils.SHORT
import kotlinx.coroutines.delay

@Composable
fun rememberLightSequence(
    luminosityProvider: () -> Float,
    threshold: Float = 50f,
    signalThresholdDuration: Long = MIN_LIGHT_CODE_DURATION_SHORT
): Triple<String, Modifier, () -> Unit> {
    var sequence by rememberSaveable { mutableStateOf("") }
    var lightOnStartTime by rememberSaveable { mutableStateOf(0L) }
    var lastUpdateTime by rememberSaveable { mutableStateOf(0L) }
    var isLightOn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            val luminosity = luminosityProvider()
            val currentLightOn = luminosity > threshold
            if (currentLightOn && !isLightOn) {
                // light turned on
                lightOnStartTime = System.currentTimeMillis()
                isLightOn = true
            } else if (!currentLightOn && isLightOn) {
                // light turned off
                val lightOnDuration = System.currentTimeMillis() - lightOnStartTime
                sequence += if (lightOnDuration < signalThresholdDuration) SHORT else LONG
                isLightOn = false
                lastUpdateTime = System.currentTimeMillis()
            }
            delay(100) // periodically check the light state
        }
    }

    return Triple(sequence, Modifier) { sequence = "" }
}

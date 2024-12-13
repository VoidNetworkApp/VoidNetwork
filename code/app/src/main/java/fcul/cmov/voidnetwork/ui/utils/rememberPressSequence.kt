package fcul.cmov.voidnetwork.ui.utils

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

const val SHORT = "-"
const val LONG = "_"
const val MAX_CODE_LENGTH = 15
const val MIN_PRESS_DURATION_SHORT_PRESS = 250L
const val MAX_INACTIVITY_DURATION = 5000L

@Composable
fun rememberPressSequence(): Triple<String, Modifier, () -> Unit> {
    var sequence by rememberSaveable { mutableStateOf("") }
    var pressStartTime by rememberSaveable { mutableStateOf(0L) }
    var lastUpdateTime by rememberSaveable { mutableStateOf(0L) }

    // reset if inactivity
    LaunchedEffect(lastUpdateTime) {
        if (sequence.isNotBlank()) {
            delay(MAX_INACTIVITY_DURATION)
            if (System.currentTimeMillis() - lastUpdateTime >= MAX_INACTIVITY_DURATION) {
                sequence = ""
            }
        }
    }

    return Triple(
        sequence,
        Modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    if (sequence.length >= MAX_CODE_LENGTH) sequence = ""
                    pressStartTime = System.currentTimeMillis()
                    tryAwaitRelease()
                    val pressDuration = System.currentTimeMillis() - pressStartTime
                    sequence += if (pressDuration < MIN_PRESS_DURATION_SHORT_PRESS) SHORT else LONG
                    lastUpdateTime = System.currentTimeMillis()
                }
            )
        }
    ) { sequence = "" }
}

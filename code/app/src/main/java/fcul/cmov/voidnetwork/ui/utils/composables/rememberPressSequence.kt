package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import fcul.cmov.voidnetwork.ui.utils.LONG
import fcul.cmov.voidnetwork.ui.utils.MAX_CODE_LENGTH
import fcul.cmov.voidnetwork.ui.utils.MAX_INACTIVITY_DURATION
import fcul.cmov.voidnetwork.ui.utils.MIN_PRESS_DURATION_SHORT_PRESS
import fcul.cmov.voidnetwork.ui.utils.SHORT
import kotlinx.coroutines.delay

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

package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import fcul.cmov.voidnetwork.ui.utils.LONG
import fcul.cmov.voidnetwork.ui.utils.MAX_SIGNAL_LENGTH
import fcul.cmov.voidnetwork.ui.utils.MIN_CODE_DURATION_SHORT
import fcul.cmov.voidnetwork.ui.utils.SHORT

@Composable
fun rememberTouchSequence(): Triple<String, Modifier, () -> Unit> {
    var sequence by rememberSaveable { mutableStateOf("") }
    var pressStartTime by rememberSaveable { mutableStateOf(0L) }
    var lastUpdateTime by rememberSaveable { mutableStateOf(0L) }

    return Triple(
        sequence,
        Modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    if (sequence.length >= MAX_SIGNAL_LENGTH) sequence = ""
                    pressStartTime = System.currentTimeMillis()
                    tryAwaitRelease()
                    val pressDuration = System.currentTimeMillis() - pressStartTime
                    sequence += if (pressDuration < MIN_CODE_DURATION_SHORT) SHORT else LONG
                    lastUpdateTime = System.currentTimeMillis()
                }
            )
        }
    ) { sequence = "" }
}

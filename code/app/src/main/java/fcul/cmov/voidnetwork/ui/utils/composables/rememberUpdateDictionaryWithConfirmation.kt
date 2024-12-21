package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import fcul.cmov.voidnetwork.domain.Language
import fcul.cmov.voidnetwork.domain.Message
import fcul.cmov.voidnetwork.ui.screens.communication.ReplaceSignalPopup

@Composable
fun rememberUpdateDictionaryWithConfirmation(
    languageSelected: Language?,
    onUpdateDictionary: (String, String) -> Unit,
): Pair<@Composable () -> Unit, (String, String) -> Unit> {
    val popupState = rememberSaveablePopupState()
    var confirmAction by remember { mutableStateOf({}) }
    var existingMessage by remember { mutableStateOf<Message?>(null) }

    val updateDictionaryWithConfirmation = { signal: String, message: String ->
        if (message.isNotBlank()) {
            val onConfirmAction = { onUpdateDictionary(signal, message) }
            val translation = languageSelected?.dictionary?.get(signal)
            if (languageSelected != null && !popupState.isVisible && translation != message) {
                confirmAction = onConfirmAction
                existingMessage = Message(signal, translation)
                popupState.show()
            } else {
                onConfirmAction()
            }
        }
    }
    val replaceSignalPopup: @Composable () -> Unit = {
        if (popupState.isVisible && existingMessage != null) {
            ReplaceSignalPopup(
                existingMessage = existingMessage!!,
                onConfirm = confirmAction,
                onClose = popupState::hide
            )
        }
    }
    return Pair(replaceSignalPopup, updateDictionaryWithConfirmation)
}


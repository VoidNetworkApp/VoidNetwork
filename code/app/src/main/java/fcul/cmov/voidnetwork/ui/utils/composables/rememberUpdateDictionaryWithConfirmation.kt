package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Language
import fcul.cmov.voidnetwork.domain.Message

@Composable
fun rememberUpdateDictionaryWithConfirmation(
    languageSelected: Language?,
    onUpdateDictionary: (String, String) -> Unit,
    enabled: Boolean = true
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


@Composable
fun ReplaceSignalPopup(
    existingMessage: Message,
    onConfirm: () -> Unit,
    onClose: () -> Unit
) {
    Popup(
        title = stringResource(R.string.signal_already_in_dictionary),
        onClose = onClose,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.replace_signal_question)
                    .replace("{signal}", existingMessage.signal)
                    .replace("{message}", existingMessage.translation!!),
                color = MaterialTheme.colorScheme.onSecondary
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = stringResource(R.string.do_you_want_to_replace_it),
                color = MaterialTheme.colorScheme.onSecondary
            )
            Spacer(Modifier.size(10.dp))
            Row {
                Button(onClick = {
                    onConfirm()
                    onClose()
                }) {
                    Text(stringResource(R.string.replace))
                }
                Spacer(Modifier.size(10.dp))
                Button(onClick = onClose) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }

    }
}

package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class PopupState(value: Boolean) {
    var isVisible by mutableStateOf(value)
        private set

    fun show() { isVisible = true }
    fun hide() { isVisible = false }
}

object PopupStateSaver : Saver<PopupState, Boolean> {
    override fun restore(value: Boolean): PopupState {
        return PopupState(value)
    }

    override fun SaverScope.save(value: PopupState): Boolean {
        return value.isVisible
    }
}

@Composable
fun rememberSaveablePopupState(initialState: Boolean = false) =
    rememberSaveable(saver = PopupStateSaver) { PopupState(initialState) }
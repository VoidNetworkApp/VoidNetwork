package fcul.cmov.voidnetwork.ui.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fcul.cmov.voidnetwork.ui.viewmodels.MessageSenderViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.MessageReceiverViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel

class SharedViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MessageSenderViewModel::class.java) -> {
                MessageSenderViewModel(application) as T
            }
            modelClass.isAssignableFrom(LanguageViewModel::class.java) -> {
                LanguageViewModel(application) as T
            }
            modelClass.isAssignableFrom(MessageReceiverViewModel::class.java) -> {
                MessageReceiverViewModel(application) as T
            }
            modelClass.isAssignableFrom(PortalViewModel::class.java) -> {
                PortalViewModel(application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

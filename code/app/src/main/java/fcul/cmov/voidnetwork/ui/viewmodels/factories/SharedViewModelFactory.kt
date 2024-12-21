package fcul.cmov.voidnetwork.ui.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fcul.cmov.voidnetwork.ui.viewmodels.MessageSenderViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.MessageReceiverViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.repository.RepositoryViewModel

class SharedViewModelFactory(
    private val application: Application,
    private val repositoryViewModel: RepositoryViewModel
) : ViewModelProvider.Factory {

    private val creators: Map<Class<out ViewModel>, () -> ViewModel> = mapOf(
        LanguageViewModel::class.java to { LanguageViewModel(application, repositoryViewModel.languagesRepository) },
        MessageSenderViewModel::class.java to { MessageSenderViewModel(application) },
        MessageReceiverViewModel::class.java to { MessageReceiverViewModel(application, repositoryViewModel.languagesRepository) },
        PortalViewModel::class.java to { PortalViewModel(application) }
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        return creator() as T
    }
}

package fcul.cmov.voidnetwork.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fcul.cmov.voidnetwork.ui.viewmodels.CommunicationViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.repository.LanguagesRepositoryViewModel

class SharedViewModelFactory(private val viewModel: LanguagesRepositoryViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LanguageViewModel::class.java) -> LanguageViewModel(viewModel.repository) as T
            modelClass.isAssignableFrom(CommunicationViewModel::class.java) -> CommunicationViewModel(viewModel.repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

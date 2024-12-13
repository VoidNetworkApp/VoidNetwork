package fcul.cmov.voidnetwork.ui.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fcul.cmov.voidnetwork.ui.viewmodels.CommunicationViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.repository.RepositoryViewModel

class SharedViewModelFactory(
    private val application: Application,
    private val repositoryViewModel: RepositoryViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LanguageViewModel::class.java) ->
                LanguageViewModel(repositoryViewModel.languagesRepository) as T
            modelClass.isAssignableFrom(CommunicationViewModel::class.java) ->
                CommunicationViewModel(application, repositoryViewModel.languagesRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

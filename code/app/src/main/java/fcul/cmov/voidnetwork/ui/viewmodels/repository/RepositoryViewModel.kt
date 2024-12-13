package fcul.cmov.voidnetwork.ui.viewmodels.repository

import androidx.lifecycle.ViewModel
import fcul.cmov.voidnetwork.repository.LanguagesRepository

class RepositoryViewModel: ViewModel() {
    val languagesRepository = LanguagesRepository()
}

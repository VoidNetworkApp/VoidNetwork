package fcul.cmov.voidnetwork.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import fcul.cmov.voidnetwork.domain.CommunicationMode
import fcul.cmov.voidnetwork.domain.Message
import fcul.cmov.voidnetwork.domain.Signal
import fcul.cmov.voidnetwork.repository.LanguagesRepository
import fcul.cmov.voidnetwork.ui.utils.getCurrentUser


class MessageSenderViewModel(application: Application) : AndroidViewModel(application) {

    private val messagesRef = Firebase.database.reference.child("messages")

    fun sendSignal(
        language: String?,
        signal: String
    ) {
        if (signal.isEmpty()) return
        val payload = Signal(
            language = language,
            value = signal,
            timestamp = System.currentTimeMillis(),
            sender = getCurrentUser()?.uid ?: "Unknown"
        )
        messagesRef.push().setValue(payload)
    }
}

package fcul.cmov.voidnetwork.ui.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fcul.cmov.voidnetwork.domain.Message
import fcul.cmov.voidnetwork.repository.LanguagesRepository
import fcul.cmov.voidnetwork.repository.MessagesRepository
import fcul.cmov.voidnetwork.services.MessageReceiverForegroundService
import fcul.cmov.voidnetwork.storage.AppSettings
import fcul.cmov.voidnetwork.ui.utils.emitSynchronizedSignals
import kotlinx.coroutines.launch

class MessageReceiverViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val messagesRef = Firebase.database.reference.child("messages")
    val messages = MessagesRepository.messages

    init {
        loadMessagesFromFirebase()
    }

    fun replayMessage(message: Message) {
        viewModelScope.launch {
            getApplication<Application>().emitSynchronizedSignals(message.signal)
        }
    }

    private fun loadMessagesFromFirebase() {
        messagesRef
            .get()
            .addOnSuccessListener { dataSnapshot ->
                val initialMessages = dataSnapshot.children.mapNotNull { snapshot ->
                    val messagePayload = snapshot.value as? Map<*, *> ?: return@mapNotNull null
                    Message.fromMap(
                        map = messagePayload,
                        onTranslate = { language, signal ->
                            LanguagesRepository[language].dictionary[signal]
                        }
                    )
                }
                MessagesRepository.load(initialMessages)
            }
            .addOnFailureListener {
                Log.e("MessageReceiverService", "Failed to load initial messages", it)
            }
    }
}
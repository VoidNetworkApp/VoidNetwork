package fcul.cmov.voidnetwork.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import fcul.cmov.voidnetwork.domain.CommunicationMode
import fcul.cmov.voidnetwork.domain.Signal
import fcul.cmov.voidnetwork.repository.LanguagesRepository

class CommunicationViewModel(private val languages: LanguagesRepository) : ViewModel() {
    // central logic for sending signal messages and receiving translated messages
    // handles touch and luminosity sending signals
    // handles vibration and flashlight receiving signals

    private val messagingDatabase = Firebase.database.reference.child("messages")

    init {
        listenForSignals()
    }

    fun sendSignal(
        language: String,
        signal: String,
        mode: CommunicationMode,
    ) {
        val payload = Signal(
            language = language,
            value = signal,
            mode = mode,
            timestamp = System.currentTimeMillis(),
            sender = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown"
        )
        messagingDatabase.push().setValue(payload)
    }

    private fun listenForSignals() {
        messagingDatabase.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val messagePayload = snapshot.value as? Map<*, *> ?: return
                val sender = messagePayload["sender"] as? String ?: return
                if (sender == FirebaseAuth.getInstance().currentUser?.uid) return // ignore own messages
                val code = messagePayload["signal"] as? String ?: return
                val mode = CommunicationMode.valueOf(messagePayload["mode"] as? String ?: "")
                val language = messagePayload["language"] as? String ?: return
                val message = languages[language].dictionary[code]
                if (message != null) {
                    if (mode == CommunicationMode.LIGHT) {
                        emitFlashlightSignals(message)
                    } else {
                        vibrateForMessage(message)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e("CommunicationViewModel", "Error receiving signals", error.toException())
            }
        })
    }

    private fun vibrateForMessage(message: String) {
        Log.d("CommunicationViewModel", "Vibrating for message: $message")
        // Implement the vibration logic here
    }

    private fun emitFlashlightSignals(message: String) {
        Log.d("CommunicationViewModel", "Emitting flashlight signals for message: $message")
        // Implement the flashlight logic here
    }
}

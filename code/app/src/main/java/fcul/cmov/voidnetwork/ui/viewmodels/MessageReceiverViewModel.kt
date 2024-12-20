package fcul.cmov.voidnetwork.ui.viewmodels

import android.app.Application
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import fcul.cmov.voidnetwork.domain.CommunicationMode
import fcul.cmov.voidnetwork.domain.Message
import fcul.cmov.voidnetwork.repository.LanguagesRepository
import fcul.cmov.voidnetwork.ui.utils.INTERVAL_DURATION
import fcul.cmov.voidnetwork.ui.utils.LONG_DURATION
import fcul.cmov.voidnetwork.ui.utils.SHORT
import fcul.cmov.voidnetwork.ui.utils.SHORT_DURATION
import fcul.cmov.voidnetwork.ui.utils.getCurrentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageReceiverViewModel(
    application: Application,
    private val languages: LanguagesRepository
) : AndroidViewModel(application) {

    private val messagesRef = Firebase.database.reference.child("messages")

    var messages by mutableStateOf(emptyList<Message>())
        private set

    init {
        listenForSignals()
    }

    fun replayMessage(message: Message) {
        viewModelScope.launch {
            emitSynchronizedSignals(message.signal)
        }
    }

    private fun listenForSignals() {
        messagesRef
            .orderByChild("timestamp")
            .startAt(System.currentTimeMillis().toDouble())
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val messagePayload = snapshot.value as? Map<*, *> ?: throw IllegalArgumentException("Invalid message payload")
                    val sender = messagePayload["sender"] as? String ?: throw IllegalArgumentException("Invalid sender")
                    if (sender == getCurrentUser()?.uid) return // ignore own messages
                    val signal = messagePayload["value"] as? String ?: throw IllegalArgumentException("Invalid signal")
                    val language = messagePayload["language"] as? String
                    val message = language?.let { languages[it].dictionary[signal] } ?: "Unknown"
                    messages = messages + Message(signal, message)
                    viewModelScope.launch {
                        emitSynchronizedSignals(signal)
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

    private suspend fun emitSynchronizedSignals(signal: String) {
        val context = getApplication<Application>()
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            try {
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } catch (e: CameraAccessException) {
                false
            }
        }
        if (cameraId == null) {
            Log.e("CommunicationViewModel", "No camera with flashlight available")
            return
        }

        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (!vibrator.hasVibrator()) {
            Log.e("CommunicationViewModel", "Device does not support vibration")
            return
        }

        try {
            for (char in signal) {
                val duration = if (char == SHORT[0]) SHORT_DURATION else LONG_DURATION

                // start flashlight and vibration in sync
                cameraManager.setTorchMode(cameraId, true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(duration)
                }

                delay(duration)

                // turn off flashlight and wait for the interval
                cameraManager.setTorchMode(cameraId, false)
                delay(INTERVAL_DURATION)
            }
        } catch (e: CameraAccessException) {
            Log.e("CommunicationViewModel", "Error accessing the camera", e)
        } finally {
            try {
                // ensure flashlight is turned off
                cameraManager.setTorchMode(cameraId, false)
            } catch (e: CameraAccessException) {
                Log.e("CommunicationViewModel", "Error turning off flashlight", e)
            }
        }
    }

}
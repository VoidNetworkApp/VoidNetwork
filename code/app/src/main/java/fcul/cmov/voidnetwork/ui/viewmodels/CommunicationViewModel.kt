package fcul.cmov.voidnetwork.ui.viewmodels

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import fcul.cmov.voidnetwork.domain.CommunicationMode
import fcul.cmov.voidnetwork.domain.Message
import fcul.cmov.voidnetwork.domain.Signal
import fcul.cmov.voidnetwork.repository.LanguagesRepository
import fcul.cmov.voidnetwork.ui.utils.INTERVAL_DURATION
import fcul.cmov.voidnetwork.ui.utils.LONG_DURATION
import fcul.cmov.voidnetwork.ui.utils.SHORT
import fcul.cmov.voidnetwork.ui.utils.SHORT_DURATION
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.hardware.camera2.CameraCharacteristics
import com.google.firebase.database.ktx.database


class CommunicationViewModel(
    application: Application,
    private val languages: LanguagesRepository
) : AndroidViewModel(application) {
    // central logic for sending signal messages and receiving translated messages
    // handles touch and luminosity sending signals
    // handles vibration and flashlight receiving signals

    private val messagesRef = Firebase.database.reference.child("messages")

    var lastMessage by mutableStateOf<Message?>(null)

    init {
        listenForSignals()
    }

    fun sendSignal(
        language: String?,
        signal: String,
        mode: CommunicationMode,
    ) {
        if (signal.isEmpty()) return
        val payload = Signal(
            language = language,
            value = signal,
            mode = mode,
            timestamp = System.currentTimeMillis(),
            sender = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown"
        )
        messagesRef.push().setValue(payload)
    }

    private fun listenForSignals() {
        messagesRef
            .orderByChild("timestamp")
            .startAt(System.currentTimeMillis().toDouble())
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val messagePayload = snapshot.value as? Map<*, *> ?: throw IllegalArgumentException("Invalid message payload")
                    val sender = messagePayload["sender"] as? String ?: throw IllegalArgumentException("Invalid sender")
                    if (sender == FirebaseAuth.getInstance().currentUser?.uid) return // ignore own messages
                    val signal = messagePayload["value"] as? String ?: throw IllegalArgumentException("Invalid signal")
                    val mode = CommunicationMode.valueOf(messagePayload["mode"] as? String ?: "")
                    val language = messagePayload["language"] as? String
                    val message = language?.let { languages[it].dictionary[signal] } ?: "Unknown"
                    lastMessage = Message(signal, message)
                    if (mode == CommunicationMode.AUTO) {
                        emitFlashlightSignal(signal)
                    } else {
                        emitVibrationSignal(signal)
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

    private fun emitVibrationSignal(signal: String) {
        val context = getApplication<Application>()
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
        val pattern = signal.flatMap {
            if (it == SHORT[0]) {
                listOf(SHORT_DURATION, INTERVAL_DURATION)
            } else {
                listOf(LONG_DURATION, INTERVAL_DURATION)
            }
        }.toLongArray()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, -1) // -1 means no repetition
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    private fun emitFlashlightSignal(signal: String) {
        val context = getApplication<Application>()
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        // get camera with flashlight
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            try {
                val hasFlash = cameraManager
                    .getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                hasFlash == true
            } catch (e: CameraAccessException) {
                false
            }
        }
        if (cameraId == null) {
            Log.e("CommunicationViewModel", "No camera with flashlight available")
            return
        }
        CoroutineScope(Dispatchers.Default).launch {
            try {
                for (char in signal) {
                    val duration = if (char == SHORT[0]) SHORT_DURATION else LONG_DURATION
                    // turn on flashlight
                    cameraManager.setTorchMode(cameraId, true)
                    delay(duration)
                    // turn off flashlight
                    cameraManager.setTorchMode(cameraId, false)
                    delay(INTERVAL_DURATION) // interval between signals
                }
            } catch (e: CameraAccessException) {
                Log.e("CommunicationViewModel", "Error accessing the camera", e)
            } finally {
                // ensure flashlight is turned off
                try {
                    cameraManager.setTorchMode(cameraId, false)
                } catch (e: CameraAccessException) {
                    Log.e("CommunicationViewModel", "Error turning off flashlight", e)
                }
            }
        }
    }

}

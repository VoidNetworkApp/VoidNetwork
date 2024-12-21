package fcul.cmov.voidnetwork.services

import android.os.Build
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fcul.cmov.voidnetwork.MainActivity
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.storage.AppSettings
import fcul.cmov.voidnetwork.domain.Message
import fcul.cmov.voidnetwork.repository.LanguagesRepository
import fcul.cmov.voidnetwork.repository.MessagesRepository
import fcul.cmov.voidnetwork.ui.utils.emitSynchronizedSignals
import fcul.cmov.voidnetwork.ui.utils.getCurrentUser

class MessageReceiverForegroundService : Service() {

    private val messagesRef = Firebase.database.reference.child("messages")
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var settings: AppSettings

    override fun onCreate() {
        super.onCreate()
        settings = AppSettings(applicationContext)
        startForegroundService()
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "signals_channel")
            .setContentTitle("Listening for Signals")
            .setContentText("Service is running...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)
        listenForSignals()
    }

    private fun listenForSignals() {
        messagesRef.orderByChild("timestamp")
            .startAt(System.currentTimeMillis().toDouble())
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val messagePayload = snapshot.value as? Map<*, *> ?: return
                    val message = Message.fromMap(
                        map = messagePayload,
                        onTranslate = { language, signal ->
                            LanguagesRepository[language].dictionary[signal]
                        }
                    ) ?: return
                    MessagesRepository += message
                    if (message.sender == getCurrentUser()?.uid) return // ignore own messages
                    scope.launch {
                        showNotification(message)
                    }
                    if (settings.allowReceiveSignals) {
                        scope.launch {
                            emitSynchronizedSignals(message.signal)
                        }
                    }

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.e("MessageReceiverService", "Error listening for messages", error.toException())
                }
            })
    }


    private fun showNotification(message: Message) {
        val channelId = "default_channel"
        val channelName = "Default Channel"

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent for notification tap action
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("New Signal Received!")
            .setContentText(message.toString())
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)

        // Show notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

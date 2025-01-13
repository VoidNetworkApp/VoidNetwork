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
import androidx.core.content.ContextCompat
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
import fcul.cmov.voidnetwork.ui.utils.getMessages
import kotlinx.coroutines.cancel

private const val CHANNEL_ID = "signals_channel"
private const val CHANNEL_NAME = "Signals Channel"
private const val NOTIFICATION_ID = 1
private const val LISTENING_NOTIFICATION_TITLE = "Listening for Signals"
private const val LISTENING_NOTIFICATION_TEXT = "Service is running..."
private const val RECEIVED_SIGNAL_NOTIFICATION_TITLE = "New Signal Received!"

class MessageReceiverForegroundService : Service() {

    private val messagesRef = Firebase.database.getMessages()
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var settings: AppSettings
    private lateinit var childEventListener: ChildEventListener

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MessageReceiverForegroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, MessageReceiverForegroundService::class.java)
            context.stopService(intent)

            // delete notification if it exists
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.cancel(0)
        }
    }

    override fun onCreate() {
        super.onCreate()
        settings = AppSettings(applicationContext)
        createNotificationChannel()
        startForegroundService()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        if (::childEventListener.isInitialized) {
            messagesRef.removeEventListener(childEventListener)
        }
    }


    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val pendingIntent = getNewMainActivityIntent()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(LISTENING_NOTIFICATION_TITLE)
            .setContentText(LISTENING_NOTIFICATION_TEXT)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        listenForSignals()
    }

    private fun listenForSignals() {
        childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val messagePayload = snapshot.value as? Map<*, *> ?: return
                val message = Message.fromMap(
                    map = messagePayload,
                    onTranslate = { language, signal ->
                        LanguagesRepository[language].dictionary[signal]
                    }
                )
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
        }

        messagesRef.orderByChild("timestamp")
            .startAt(System.currentTimeMillis().toDouble())
            .addChildEventListener(childEventListener)
    }

    private fun getNewMainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun showNotification(message: Message) {
        val pendingIntent = getNewMainActivityIntent()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(RECEIVED_SIGNAL_NOTIFICATION_TITLE)
            .setContentText(message.toString())
            .setAutoCancel(true)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.notify(0, notification)
    }
}

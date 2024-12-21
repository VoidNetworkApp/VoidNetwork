package fcul.cmov.voidnetwork.domain

import fcul.cmov.voidnetwork.ui.utils.getCurrentUser

data class Message(
    val signal: String,
    val translation: String?,
    val timestamp: Long,
) {
    override fun toString(): String {
        return "$signal -> $translation"
    }

    companion object {
        fun fromMap(map: Map<*, *>, onTranslate: (String, String) -> String?): Message? {
            val sender = map["sender"] as? String ?: throw IllegalArgumentException("Invalid sender")
            if (sender == getCurrentUser()?.uid) return null // ignore own messages
            val timestamp = map["timestamp"] as? Long ?: throw IllegalArgumentException("Invalid timestamp")
            val signal = map["value"] as? String ?: throw IllegalArgumentException("Invalid signal")
            val language = map["language"] as? String
            val translation = language?.let { onTranslate(it, signal) } ?: "Unknown"
            return Message(signal, translation, timestamp)
        }
    }
}
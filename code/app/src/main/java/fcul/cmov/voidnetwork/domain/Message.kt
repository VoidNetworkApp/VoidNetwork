package fcul.cmov.voidnetwork.domain

data class Message(
    val signal: String,
    val translation: String?,
    val sender: String? = null,
    val timestamp: Long? = null,
) {
    override fun toString(): String {
        if (translation != null) {
            return "$signal  $translation"
        }
        return signal
    }

    companion object {
        fun fromMap(map: Map<*, *>, onTranslate: (String, String) -> String?): Message {
            val sender = map["sender"] as? String ?: throw IllegalArgumentException("Invalid sender")
            val timestamp = map["timestamp"] as? Long ?: throw IllegalArgumentException("Invalid timestamp")
            val signal = map["value"] as? String ?: throw IllegalArgumentException("Invalid signal")
            val language = map["language"] as? String
            val translation = language?.let { onTranslate(it, signal) }
            return Message(signal, translation, sender, timestamp)
        }
    }
}
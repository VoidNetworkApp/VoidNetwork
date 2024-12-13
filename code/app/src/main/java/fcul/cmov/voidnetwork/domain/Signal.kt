package fcul.cmov.voidnetwork.domain

data class Signal(
    val language: String?,
    val value: String,
    val mode: CommunicationMode,
    val timestamp: Long,
    val sender: String,
)
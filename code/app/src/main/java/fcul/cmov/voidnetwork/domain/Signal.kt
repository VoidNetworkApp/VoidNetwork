package fcul.cmov.voidnetwork.domain

data class Signal(
    val value: String,
    val language: String?,
    val timestamp: Long,
    val sender: String,
)
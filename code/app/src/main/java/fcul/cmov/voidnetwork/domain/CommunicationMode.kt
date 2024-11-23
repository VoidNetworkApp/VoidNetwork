package fcul.cmov.voidnetwork.domain

enum class CommunicationMode {
    MANUAL, AUTOMATIC;

    fun other(): CommunicationMode {
        return when (this) {
            MANUAL -> AUTOMATIC
            AUTOMATIC -> MANUAL
        }
    }
}

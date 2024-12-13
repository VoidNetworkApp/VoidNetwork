package fcul.cmov.voidnetwork.domain

enum class CommunicationMode {
    AUTO, TOUCH, LIGHT;

    fun next(): CommunicationMode {
        return CommunicationMode.values()[(ordinal + 1) % CommunicationMode.values().size]
    }
}

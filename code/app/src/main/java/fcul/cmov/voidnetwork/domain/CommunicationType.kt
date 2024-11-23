package fcul.cmov.voidnetwork.domain

enum class CommunicationType {
    TOUCH, LIGHT;

    fun other(): CommunicationType {
        return when (this) {
            TOUCH -> LIGHT
            LIGHT -> TOUCH
        }
    }
}
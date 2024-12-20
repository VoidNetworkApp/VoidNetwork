package fcul.cmov.voidnetwork.domain

data class Message(
    val signal: String,
    val translation: String?
) {
    override fun toString(): String {
        return "$signal -> $translation"
    }
}
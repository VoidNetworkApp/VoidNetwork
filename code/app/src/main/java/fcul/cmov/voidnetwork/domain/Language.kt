package fcul.cmov.voidnetwork.domain

data class Language(
    val id: String,
    val name: String,
    val dictionary: Map<String, String>
)

package fcul.cmov.voidnetwork.domain

data class Language(
    val id: String,
    val name: String,
    val dictionary: MutableMap<String, String>
)

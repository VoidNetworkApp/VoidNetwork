package fcul.cmov.voidnetwork.repository

import fcul.cmov.voidnetwork.domain.Language

object LanguagesRepository {
    private val languages = mutableListOf<Language>()

    operator fun plusAssign(language: Language) {
        synchronized(this) {
            languages.add(language)
        }
    }

    operator fun minusAssign(language: Language) {
        synchronized(this) {
            languages.remove(language)
        }
    }

    operator fun get(id: String): Language {
        synchronized(this) {
            return languages.find { it.id == id } ?: throw IllegalArgumentException("Language not found")
        }
    }

    fun update(updatedLanguage: Language) {
        synchronized(this) {
            val index = languages.indexOfFirst { it.id == updatedLanguage.id }
            if (index != -1) languages[index] = updatedLanguage
        }
    }

    fun get(): List<Language> {
        synchronized(this) {
            return languages
        }
    }

    fun load(languages: List<Language>) {
        synchronized(this) {
            this.languages.clear()
            this.languages.addAll(languages)
        }
    }
}

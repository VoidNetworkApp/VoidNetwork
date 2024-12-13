package fcul.cmov.voidnetwork.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fcul.cmov.voidnetwork.domain.Language

class LanguagesRepository {
    private var languages by mutableStateOf<List<Language>>(emptyList())

    operator fun plusAssign(language: Language) {
        synchronized(this) {
            languages += language
        }
    }

    operator fun minusAssign(language: Language) {
        synchronized(this) {
            languages = languages.filter { it.id != language.id }
        }
    }

    operator fun get(id: String): Language {
        synchronized(this) {
            return languages.find { it.id == id } ?: throw IllegalArgumentException("Language not found")
        }
    }

    fun update(updatedLanguage: Language) {
        synchronized(this) {
            languages = languages.map { if (it.id == updatedLanguage.id) updatedLanguage else it }
        }
    }

    fun get(): List<Language> {
        synchronized(this) {
            return languages
        }
    }

    fun load(languages: List<Language>) {
        synchronized(this) {
            this.languages = languages
        }
    }
}

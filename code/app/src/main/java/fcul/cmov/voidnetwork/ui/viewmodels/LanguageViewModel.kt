package fcul.cmov.voidnetwork.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import fcul.cmov.voidnetwork.domain.Language

class LanguageViewModel : ViewModel() {
    // handles language crud operations and selection

    val languages by mutableStateOf(
        mutableMapOf(
            "abc" to Language(
                id = "abc",
                name = "Morse Code",
                dictionary = mapOf(
                    "...___..." to "SOS",
                    ".-.. --- ...- ." to "LOVE",
                    ".... . .-.. .-.. ---" to "HELLO",
                    "--. --- --- -.. -... -.-- ." to "GOODBYE"
                )
            ),
            "def" to Language(
                id = "def",
                name = "Simple Lang",
                dictionary = mapOf(
                    "..--.." to "HELLO",
                    "--..--" to "GOODBYE",
                    ".-.--." to "FRIEND",
                    "--.-.." to "THANK YOU"
                )
            )
        )
    )

    fun getOrAddLanguageById(id: String): Language {
        val language = languages[id]
        return language ?: Language(
            id = id,
            name = "New Language",
            dictionary = mapOf()
        ).also { languages[id] = it }
    }
}
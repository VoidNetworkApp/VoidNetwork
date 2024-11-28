package fcul.cmov.voidnetwork.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import fcul.cmov.voidnetwork.domain.Language

const val MAX_MESSAGE_LENGTH = 20
const val MAX_CODE_LENGTH = 20

class LanguageViewModel : ViewModel() {
    // handles language crud operations and selection

    val languages by mutableStateOf(
        mutableStateMapOf(
            "abc" to Language(
                id = "abc",
                name = "Morse Code",
                dictionary = mutableStateMapOf(
                    "...___..." to "SOS",
                    ".-.. --- ...- ." to "LOVE",
                    ".... . .-.. .-.. ---" to "HELLO",
                    "--. --- --- -.. -... -.-- ." to "GOODBYE",
                    "..-. .-. .. . -. -.." to "FRIEND",
                    "- .... .- -. -.-" to "THANK YOU",
                    "..-. ..- -." to "FUN",
                    "-.-. --- --- .-." to "COOL",
                )
            ),
            "def" to Language(
                id = "def",
                name = "Simple Lang",
                dictionary = mutableStateMapOf(
                    "..--.." to "HELLO",
                    "--..--" to "GOODBYE",
                    ".-.--." to "FRIEND",
                    "--.-.." to "THANK YOU"
                )
            )
        )
    )

    var languageSelected: Language? by mutableStateOf(languages["abc"])

    fun getOrAddLanguageById(id: String): Language {
        val language = languages[id]
        return language ?: Language(
            id = id,
            name = "New Language",
            dictionary = mutableMapOf()
        ).also { languages[id] = it }
    }

    fun selectLanguage(id: String) {
        require(languages.containsKey(id)) { "Language with id $id does not exist" }
        languageSelected = languages[id]
    }

    fun onUpdateLanguageDictionary(languageId: String, code: String, msg: String) {
        require(code.length <= MAX_CODE_LENGTH) { "Code length must be less than $MAX_CODE_LENGTH" }
        require(msg.length <= MAX_MESSAGE_LENGTH) { "Message length must be less than $MAX_MESSAGE_LENGTH" }
        val language = getOrAddLanguageById(languageId)
        language.dictionary[code] = msg
    }
}
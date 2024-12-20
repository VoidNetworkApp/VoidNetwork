package fcul.cmov.voidnetwork.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fcul.cmov.voidnetwork.domain.Language
import fcul.cmov.voidnetwork.repository.LanguagesRepository
import fcul.cmov.voidnetwork.ui.utils.MAX_CODE_LENGTH
import fcul.cmov.voidnetwork.ui.utils.MAX_MESSAGE_LENGTH

class LanguageViewModel(private val languages: LanguagesRepository) : ViewModel() {
    // handles language crud operations and selection

    private val languagesRef = Firebase.database.reference.child("languages")

    var languageSelected: Language? by mutableStateOf(null)

    init {
        loadLanguagesFromFirebase()
        listenToChangesFromFirebase()
    }

    fun translate(signal: String): String? {
        return languageSelected?.dictionary?.get(signal)
    }

    fun getLanguage(id: String): Language {
        return languages[id]
    }

    fun getLanguageOrNull(id: String): Language? {
        return try {
            getLanguage(id)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun getLanguages(): List<Language> {
        return languages.get()
    }

    fun addLanguage(onCompleted: (String) -> Unit) {
        addLanguageToFirebase(onCompleted)
    }

    fun selectLanguage(id: String) {
        languageSelected = getLanguage(id)
    }

    fun deleteLanguage(id: String) {
        deleteLanguageFromFirebase(id)
    }

    fun editLanguage(id: String, name: String) {
        require(id.isNotBlank()) { "Id must not be blank" }
        require(name.isNotBlank()) { "Name must not be blank" }
        require(name.length <= MAX_MESSAGE_LENGTH) { "Language name length must be less than $MAX_MESSAGE_LENGTH" }

        val language = getLanguage(id)
        if (language.name == name) return // no changes
        val updatedLanguage = language.copy(name = name)
        updateLanguageInFirebase(updatedLanguage)
    }

    fun updateLanguageDictionary(id: String, code: String, msg: String) {7
        require(id.isNotBlank()) { "Id must not be blank" }
        require(code.length <= MAX_CODE_LENGTH) { "Code length must be less than $MAX_CODE_LENGTH" }
        require(msg.length <= MAX_MESSAGE_LENGTH) { "Message length must be less than $MAX_MESSAGE_LENGTH" }
        if (msg == "Unknown") return

        val language = getLanguage(id)
        val updatedLanguage = language.copy(dictionary = language.dictionary + (code to msg))
        updateLanguageInFirebase(updatedLanguage)
    }

    fun deleteMessageFromLanguage(id: String, code: String) {
        val language = getLanguage(id)
        val updatedLanguage = language.copy(dictionary = language.dictionary - code)
        updateLanguageInFirebase(updatedLanguage)
    }

    private fun loadLanguagesFromFirebase() {
        languagesRef.get().addOnSuccessListener { dataSnapshot ->
            // get languages from database
            val languages = dataSnapshot.children.mapNotNull { it.getValue(Language::class.java) }
            // update languages in memory
            this.languages.load(languages)
        }
    }

    private fun addLanguageToFirebase(onCompleted: (String) -> Unit): String {
        val newRef = languagesRef.push()
        val newId = newRef.key ?: throw IllegalStateException("Failed to generate a new key for the language")
        val newLanguages = languages.get().count { it.name.startsWith("New Language") }
        val name = "New Language${if (newLanguages > 0) " ($newLanguages)" else ""}"
        val newLanguage = Language(newId, name, emptyMap())
        newRef.setValue(newLanguage)
            .addOnSuccessListener {
                onCompleted(newId)
            }
        return newId
    }

    private fun updateLanguageInFirebase(language: Language) {
        languagesRef.child(language.id).setValue(language)
    }

    private fun deleteLanguageFromFirebase(id: String) {
        languagesRef.child(id).removeValue()
    }

    private fun listenToChangesFromFirebase() {
        // updates languages in memory when database changes
        languagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val language = snapshot.getValue(Language::class.java)
                requireNotNull(language) { "Language is null" }
                languages += language
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val language = snapshot.getValue(Language::class.java)
                requireNotNull(language) { "Language is null" }
                synchronized(languages) {
                    languages.update(language)
                    if (languageSelected?.id == language.id) {
                        languageSelected = language
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val language = snapshot.getValue(Language::class.java)
                requireNotNull(language) { "Language is null" }
                synchronized(languages) {
                    languages -= language
                    if (languageSelected?.id == language.id) {
                        languageSelected = null
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e("LanguageViewModel", "Database error: $error")
            }
        })
    }
}
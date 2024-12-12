package fcul.cmov.voidnetwork.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fcul.cmov.voidnetwork.domain.Language

class LanguageViewModel : ViewModel() {
    // handles language crud operations and selection

    private val database by lazy { Firebase.database.reference }

    private val languageDatabase
        get() = database.child("languages")


    val languages = mutableStateMapOf<String, Language>()
    var languageSelected: Language? by mutableStateOf(null)

    init {
        loadLanguagesFromFirebase()
        listenToChangesFromFirebase()
    }

    companion object {
        const val MAX_MESSAGE_LENGTH = 20
        const val MAX_CODE_LENGTH = 20
    }

    fun getLanguage(id: String): Language {
        return languages[id] ?: throw IllegalStateException("Language with id $id does not exist")
    }

    fun addLanguage(onCompleted: (String) -> Unit) {
        addLanguageToFirebase(onCompleted)
    }

    fun selectLanguage(id: String) {
        require(languages.containsKey(id)) { "Language with id $id does not exist" }
        languageSelected = languages[id]
    }

    fun deleteLanguage(id: String) {
        deleteLanguageFromFirebase(id)
    }

    fun onUpdateLanguageDictionary(languageId: String, code: String, msg: String) {7
        require(code.length <= MAX_CODE_LENGTH) { "Code length must be less than $MAX_CODE_LENGTH" }
        require(msg.length <= MAX_MESSAGE_LENGTH) { "Message length must be less than $MAX_MESSAGE_LENGTH" }

        val language = getLanguage(languageId)
        val updatedLanguage = language.copy(dictionary = language.dictionary + (code to msg))
        updateLanguageInFirebase(updatedLanguage)
    }

    fun onDeleteMessageFromLanguage(id: String, code: String) {
        val language = getLanguage(id)
        val updatedLanguage = language.copy(dictionary = language.dictionary - code)
        updateLanguageInFirebase(updatedLanguage)
    }

    private fun loadLanguagesFromFirebase() {
        languageDatabase.get().addOnSuccessListener { dataSnapshot ->
            // get languages from database
            val languages = dataSnapshot.children.associate { snapshot ->
                val language = snapshot.getValue(Language::class.java)
                requireNotNull(language) { "Language is null" }
                language.id to language
            }
            // update languages in memory
            synchronized(languages) {
                this.languages.clear()
                this.languages.putAll(languages)
            }
        }
    }

    private fun addLanguageToFirebase(onCompleted: (String) -> Unit): String {
        val newRef = languageDatabase.push()
        val newId = newRef.key ?: throw IllegalStateException("Failed to generate a new key for the language")
        val newLanguages = languages.values.count { it.name.startsWith("New Language") }
        val name = "New Language${if (newLanguages > 0) " ($newLanguages)" else ""}"
        val newLanguage = Language(newId, name, emptyMap())
        newRef.setValue(newLanguage)
            .addOnSuccessListener {
                onCompleted(newId)
            }
        return newId
    }

    private fun updateLanguageInFirebase(language: Language) {
        languageDatabase.child(language.id).setValue(language)
    }

    private fun deleteLanguageFromFirebase(id: String) {
        languageDatabase.child(id).removeValue()
    }

    private fun listenToChangesFromFirebase() {
        // updates languages in memory when database changes
        languageDatabase.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val language = snapshot.getValue(Language::class.java)
                requireNotNull(language) { "Language is null" }
                synchronized(languages) {
                    languages[language.id] = language
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val language = snapshot.getValue(Language::class.java)
                requireNotNull(language) { "Language is null" }
                synchronized(languages) {
                    languages[language.id] = language
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val language = snapshot.getValue(Language::class.java)
                requireNotNull(language) { "Language is null" }
                synchronized(languages) {
                    languages.remove(language.id)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e("LanguageViewModel", "Database error: $error")
            }
        })
    }
}
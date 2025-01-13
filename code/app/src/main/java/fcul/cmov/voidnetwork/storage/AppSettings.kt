package fcul.cmov.voidnetwork.storage

import android.content.Context
import android.content.SharedPreferences

class AppSettings(context: Context) {

    companion object {
        private const val PREF_NAME = "AppSettings"
        private const val KEY_ALLOW_RECEIVE_SIGNALS = "allowReceiveSignals"
        private const val KEY_LANGUAGE_SELECTED = "languageSelected"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    var allowReceiveSignals: Boolean
        get() = sharedPreferences.getBoolean(KEY_ALLOW_RECEIVE_SIGNALS, true)
        set(value) { editor.putBoolean(KEY_ALLOW_RECEIVE_SIGNALS, value).apply() }

    var languageSelected: String?
        get() = sharedPreferences.getString(KEY_LANGUAGE_SELECTED, null)
        set(value) { editor.putString(KEY_LANGUAGE_SELECTED, value).apply() }
}

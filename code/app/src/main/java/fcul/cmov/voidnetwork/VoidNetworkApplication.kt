package fcul.cmov.voidnetwork

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import fcul.cmov.voidnetwork.ui.utils.getCurrentUser

class VoidNetworkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        if (getCurrentUser() == null) {
            FirebaseAuth.getInstance().signInAnonymously()
        }
    }
}
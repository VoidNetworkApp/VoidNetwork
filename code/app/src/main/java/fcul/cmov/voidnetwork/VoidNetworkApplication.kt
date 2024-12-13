package fcul.cmov.voidnetwork

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class VoidNetworkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            FirebaseAuth.getInstance().signInAnonymously()
        }
    }
}
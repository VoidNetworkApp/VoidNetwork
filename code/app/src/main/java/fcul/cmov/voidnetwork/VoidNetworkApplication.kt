package fcul.cmov.voidnetwork

import android.app.Application
import com.google.firebase.FirebaseApp

class VoidNetworkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
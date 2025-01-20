package fcul.cmov.voidnetwork.repository

import fcul.cmov.voidnetwork.domain.Coordinates

// singleton used to share state between the app and the foreground service
object SharedStateManager {
    @Volatile
    var inUpsideDown: Boolean = false

    @Volatile
    var lastKnownLocation: Coordinates? = null
}
package fcul.cmov.voidnetwork.repository

import fcul.cmov.voidnetwork.domain.Portal

object PortalsRepository {
    private val _portals = mutableListOf<Portal>()
    val portals: List<Portal> get() = _portals

    fun load(portals: List<Portal>) {
        synchronized(this) {
            _portals.clear()
            _portals.addAll(portals)
        }
    }
}
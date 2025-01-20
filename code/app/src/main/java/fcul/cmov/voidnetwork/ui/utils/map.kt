package fcul.cmov.voidnetwork.ui.utils

import android.location.Location
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.domain.Portal

/**
 * Calculates the distance between two coordinates in kilometers
 */
fun calculateDistance(
    fromCoordinates: Coordinates,
    toCoordinates: Coordinates
): Float {
    val startLocation = Location("start").apply {
        latitude = fromCoordinates.latitude
        longitude = fromCoordinates.longitude
    }
    val endLocation = Location("end").apply {
        latitude = toCoordinates.latitude
        longitude = toCoordinates.longitude
    }
    return startLocation.distanceTo(endLocation) / 1000f
}

/**
 * Checks if the given location is within the range of any of the portals
 */
fun isWithinPortalRange(
    location: Coordinates,
    portals: List<Portal>
): Boolean {
    return portals.any { portal ->
        calculateDistance(location, portal.coordinates) <= MAX_DISTANCE_FROM_PORTAL
    }
}
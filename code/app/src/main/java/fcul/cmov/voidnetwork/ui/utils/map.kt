package fcul.cmov.voidnetwork.ui.utils

import android.location.Location
import fcul.cmov.voidnetwork.domain.Coordinates

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
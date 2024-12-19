package fcul.cmov.voidnetwork.ui.utils

import android.location.Location
import fcul.cmov.voidnetwork.domain.Coordinates

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
    return startLocation.distanceTo(endLocation) / 1000f // distance in meters
}
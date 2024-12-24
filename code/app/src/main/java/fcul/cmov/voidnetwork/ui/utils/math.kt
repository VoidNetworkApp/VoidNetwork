package fcul.cmov.voidnetwork.ui.utils

import com.mapbox.geojson.Point
import fcul.cmov.voidnetwork.domain.Coordinates
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.*

private const val EARTH_RADIUS_METERS = 6_378_137.0
private const val DEFAULT_CIRCLE_POINTS = 60

fun createCirclePoints(
    center: Coordinates,
    radiusInMeters: Double,
    pointsCount: Int = DEFAULT_CIRCLE_POINTS
): List<Point> {
    val circleCoordinates = mutableListOf<Point>()
    val centerLat = toRadians(center.latitude)
    val centerLon = toRadians(center.longitude)
    val distanceDivEarth = radiusInMeters / EARTH_RADIUS_METERS

    for (i in 0..pointsCount) {
        val bearing = 2.0 * PI * i / pointsCount
        val lat = asin(
            sin(centerLat) * cos(distanceDivEarth) + cos(centerLat) * sin(distanceDivEarth) * cos(bearing)
        )
        val lon = centerLon + atan2(
            sin(bearing) * sin(distanceDivEarth) * cos(centerLat),
            cos(distanceDivEarth) - sin(centerLat) * sin(lat)
        )
        circleCoordinates += Point.fromLngLat(toDegrees(lon), toDegrees(lat))
    }
    return circleCoordinates
}
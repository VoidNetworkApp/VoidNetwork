package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.domain.Portal
import fcul.cmov.voidnetwork.ui.utils.MAX_DISTANCE_FROM_PORTAL
import fcul.cmov.voidnetwork.ui.utils.calculateDistance

const val SIGNIFICANT_CHANGE_THRESHOLD = 50 // meters

@Composable
fun rememberClosestPortal(
    currentPosition: Coordinates?,
    portals: List<Portal>,
): Portal? {
    var closestPortal by remember { mutableStateOf<Portal?>(null) }
    var lastCheckedPosition by remember { mutableStateOf<Coordinates?>(null) }

    LaunchedEffect(currentPosition, portals) {
        if (currentPosition == null) return@LaunchedEffect // no location available
        val isSignificantChange = lastCheckedPosition?.let {
            calculateDistance(it, currentPosition) * 1000 >= SIGNIFICANT_CHANGE_THRESHOLD
        } ?: true

        if (isSignificantChange) {
            lastCheckedPosition = currentPosition
            val sortedPortals = portals.mapNotNull { portal ->
                val distance = calculateDistance(currentPosition, portal.coordinates)
                if (distance <= MAX_DISTANCE_FROM_PORTAL) portal to distance else null
            }.sortedBy { it.second }

            closestPortal = sortedPortals.firstOrNull()?.first
        }
    }
    return closestPortal
}

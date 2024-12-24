package fcul.cmov.voidnetwork.ui.utils.composables

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import fcul.cmov.voidnetwork.domain.Coordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun rememberCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    usePreciseLocation: Boolean = true
): Coordinates? {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentLocation by remember { mutableStateOf<Coordinates?>(null) }

    // Fetch current location
    LaunchedEffect(fusedLocationClient, usePreciseLocation) {
        scope.launch(Dispatchers.IO) {
            try {
                // Check if location permissions are granted
                val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasFineLocationPermission || hasCoarseLocationPermission) {
                    val priority = if (usePreciseLocation && hasFineLocationPermission) {
                        Priority.PRIORITY_HIGH_ACCURACY
                    } else {
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY
                    }
                    val result = fusedLocationClient.getCurrentLocation(
                        priority,
                        CancellationTokenSource().token
                    ).await()
                    result?.let {
                        currentLocation = Coordinates(it.latitude, it.longitude)
                    }
                } else {
                    // Handle the case where permissions are not granted
                    currentLocation = null
                }
            } catch (e: Exception) {
                // Handle other exceptions (e.g., location unavailable)
                currentLocation = null
            }
        }
    }
    return currentLocation
}

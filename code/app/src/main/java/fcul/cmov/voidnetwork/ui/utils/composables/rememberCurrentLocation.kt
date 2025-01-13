package fcul.cmov.voidnetwork.ui.utils.composables

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import fcul.cmov.voidnetwork.domain.Coordinates

const val LOCATION_REQUEST_INTERVAL = 10000L
const val LOCATION_REQUEST_MIN_INTERVAL = 5000L

@Composable
fun rememberCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    usePreciseLocation: Boolean = true
): Coordinates? {
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<Coordinates?>(null) }
    val locationManager = remember { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    // for location updates
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    // only update location if GPS is enabled
                    currentLocation = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Coordinates(it.latitude, it.longitude)
                    } else {
                        null
                    }
                }
            }
        }
    }

    // for monitoring location provider status
    DisposableEffect(locationManager) {
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                if (provider == LocationManager.GPS_PROVIDER) {
                    currentLocation = null
                }
            }
        }

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_REQUEST_MIN_INTERVAL,
                    0f,
                    locationListener
                )
            } catch (e: SecurityException) {
                currentLocation = null
            }
        }

        onDispose {
            try {
                locationManager.removeUpdates(locationListener)
            } catch (e: SecurityException) {
                // permission denial during cleanup
            }
        }
    }

    LaunchedEffect(fusedLocationClient, usePreciseLocation) {
        try {
            // location permissions check
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

                // create location request
                val locationRequest = LocationRequest.Builder(LOCATION_REQUEST_INTERVAL)
                    .setPriority(priority)
                    .setMinUpdateIntervalMillis(LOCATION_REQUEST_MIN_INTERVAL)
                    .build()

                // start location updates
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } catch (e: Exception) {
            currentLocation = null
        }
    }

    // clean up
    DisposableEffect(fusedLocationClient) {
        onDispose {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            } catch (e: SecurityException) {
                // permission denial during cleanup
            }
        }
    }

    return currentLocation
}
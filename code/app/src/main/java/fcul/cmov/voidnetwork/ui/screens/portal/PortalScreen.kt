package fcul.cmov.voidnetwork.ui.screens.portal


import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Portal
import fcul.cmov.voidnetwork.ui.navigation.Screens
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel

private var view: MapView? = null

val portals = listOf(
    Portal("Rua das Flores", 2.1f),
    Portal("Avenida do Sol", 23.4f),
    Portal("Rua da Glória", 35.9f)
)

var lat: Double = 0.0
var long: Double = 0.0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalScreen(
    nav: NavController,
    viewModel: PortalViewModel = PortalViewModel()
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val latitude = lat ?: 0.0
                    val longitude = long ?: 0.0
                    nav.navigate(Screens.RegisterPortal.createRoute(latitude, longitude)) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 60.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_portal),
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End, // bottom-right
        content = { paddingValues ->
            PortalScreenContent(
                portals = portals,
                modifier = Modifier.padding(paddingValues)
            )
        }
    )

}

@Composable
fun PortalScreenContent(portals: List<Portal>, modifier : Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(R.string.upside_down_portals))

        Box(Modifier.size(350.dp)) {
            MapboxScreen()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            portals.forEach { portal ->
                Button(onClick = { /*TODO*/ marker(lat,long)}) {
                    Text(
                       buildString {
                            append("${portal.street} (${portal.distance} km)")
                            if (portal.distance > 5) {
                                append(" - ${stringResource(R.string.out_of_range)}")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MapboxScreen() {
    val mapViewportState = rememberMapViewportState()
    var userLocation by remember { mutableStateOf<Location?>(null) }

    // Composable for Mapbox Map
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
    ) {
        MapEffect(Unit) { mapView ->
            // Enable location puck
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }

            view = mapView

            // Transition to follow the user's puck
            mapViewportState.transitionToFollowPuckState()

            // Add a listener for position changes
            mapView.location.addOnIndicatorPositionChangedListener { point ->
                userLocation = Location("").apply {
                    latitude = point.latitude()
                    longitude = point.longitude()
                    lat = latitude
                    long = longitude
                    Log.d("lat2", lat.toString())
                    Log.d("long2", long.toString())
                }
            }
        }
    }
}

fun marker(latitude: Double, longitude: Double) {
    Log.d("lat", latitude.toString())
    Log.d("long", longitude.toString())
    // Create an instance of the Annotation API and get the CircleAnnotationManager.
    val annotationApi = view?.annotations
    val circleAnnotationManager = annotationApi?.createCircleAnnotationManager()
    // Set options for the resulting circle layer.
    val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
        // Define a geographic coordinate.
        .withPoint(Point.fromLngLat(longitude, latitude))
        // Style the circle that will be added to the map.
        .withCircleRadius(8.0)
        .withCircleColor("#ee4e8b")
        .withCircleStrokeWidth(2.0)
        .withCircleStrokeColor("#ffffff")
    // Add the resulting circle to the map.
    circleAnnotationManager?.create(circleAnnotationOptions)
}

//@Preview(showBackground = true)
//@Composable
//fun PortalScreenPreview() {
//    val fusedLocationClient = null
//    PortalScreen(
//        nav = NavController(LocalContext.current),
//        fusedLocationClient = fusedLocationClient
//    )
//}
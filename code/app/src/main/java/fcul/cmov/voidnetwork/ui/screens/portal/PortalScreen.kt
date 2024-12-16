package fcul.cmov.voidnetwork.ui.screens.portal


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.Marker
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
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

var fusedLocClient: FusedLocationProviderClient? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalScreen(
    nav: NavController,
    viewModel: PortalViewModel = PortalViewModel(),
    fusedLocationClient: FusedLocationProviderClient
) {
    fusedLocClient = fusedLocationClient
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { nav.navigate(Screens.RegisterPortal.route) },
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
            Button(onClick = { view?.let { marker(it) } }) { }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            portals.forEach { portal ->
                Button(onClick = { /*TODO*/ }) {
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
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
    ) {
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            mapViewportState.transitionToFollowPuckState()
            view = mapView
        }
    }
}

fun marker(mapView: MapView) {
    Log.d("chegou","chegou")
    // Create an instance of the Annotation API and get the CircleAnnotationManager.
    val annotationApi = mapView?.annotations
    val circleAnnotationManager = annotationApi?.createCircleAnnotationManager()
    // Set options for the resulting circle layer.
    val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
        // Define a geographic coordinate.
        .withPoint(Point.fromLngLat(38.7565, -9.1563))
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
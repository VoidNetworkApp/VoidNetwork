package fcul.cmov.voidnetwork.ui.screens.portal


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.domain.Portal
import fcul.cmov.voidnetwork.ui.navigation.Screens
import fcul.cmov.voidnetwork.ui.utils.args
import fcul.cmov.voidnetwork.ui.utils.calculateDistance
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel
import kotlinx.coroutines.delay

private var view: MapView? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalScreen(
    nav: NavController,
    viewModel: PortalViewModel,
    navigateToPage: (Int) -> Unit = {},
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    nav.navigate(
                        Screens.RegisterPortal.route.args("coordinates" to viewModel.currentPosition)
                    )
                },
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
                currentPosition = viewModel.currentPosition,
                portals = viewModel.portals,
                onSelectPortal = { viewModel.selectPortal(it); navigateToPage(1) },
                onPositionChanged = { viewModel.currentPosition = it },
                onAddMarker = { viewModel.addMarker(view, it) },
                onRegisterPortal = { viewModel.registerPortal(view) },
                modifier = Modifier.padding(paddingValues),
            )
        }
    )

}

@Composable
fun PortalScreenContent(
    currentPosition: Coordinates,
    portals: List<Portal>,
    onSelectPortal: (String) -> Unit,
    onPositionChanged: (Coordinates) -> Unit,
    onAddMarker: (Coordinates) -> Unit,
    onRegisterPortal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(R.string.upside_down_portals))
        Box(Modifier.size(350.dp)) {
            MapboxScreen(onPositionChanged = onPositionChanged)
            Button(onClick = onRegisterPortal) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_portal),
                    tint = Color.White
                )
            }
        }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            items(portals) { portal ->
                var distance by rememberSaveable { mutableStateOf(0f) }
                onAddMarker(portal.coordinates)
                Button(onClick = { onSelectPortal(portal.id) }) {
                    LaunchedEffect(Unit) {
                        while (true) { // calculate the distance every second
                            distance = calculateDistance(currentPosition, portal.coordinates)
                            delay(1000L)
                        }
                    }
                    Text(
                        buildString {
                            append("${portal.street} - ${"%.1f".format(distance)} km")
                            if (distance > 5) {
                                append(" (${stringResource(R.string.out_of_range)})")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MapboxScreen(
    onPositionChanged: (Coordinates) -> Unit,
) {
    val mapViewportState = rememberMapViewportState()
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
    ) {
        MapEffect(Unit) { mapView ->
            // enable location puck
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            view = mapView

            // transition to follow the user's puck
            mapViewportState.transitionToFollowPuckState()

            // listener for position changes
            mapView.location.addOnIndicatorPositionChangedListener { point ->
                val coordinates = Coordinates(point.latitude(), point.longitude())
                onPositionChanged(coordinates)
            }
        }
    }
}


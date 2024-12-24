package fcul.cmov.voidnetwork.ui.screens.portal


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import fcul.cmov.voidnetwork.ui.utils.MAX_DISTANCE_FROM_PORTAL
import fcul.cmov.voidnetwork.ui.utils.args
import fcul.cmov.voidnetwork.ui.utils.calculateDistance
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalsScreen(
    nav: NavController,
    viewModel: PortalViewModel,
    currentLocation: Coordinates?
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
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
            PortalsScreenContent(
                onUpdateMapView = { mapView = it },
                currentPosition = currentLocation,
                portals = viewModel.portals,
                onAddMarker = { viewModel.addMarker(mapView, it) },
                onNavigateToPortal = { nav.navigate(Screens.Portal.route.args("id" to it)) },
                modifier = Modifier.padding(paddingValues),
            )
        }
    )
}
@Composable
fun PortalsScreenContent(
    onUpdateMapView: (MapView) -> Unit,
    currentPosition: Coordinates?,
    portals: List<Portal>,
    onAddMarker: (Coordinates) -> Unit,
    onNavigateToPortal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPortalListVisible by remember { mutableStateOf(false) }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MapboxScreen(
            onUpdateMapView = onUpdateMapView,
            modifier = Modifier.fillMaxSize()
        )
        AnimatedVisibility(
            visible = isPortalListVisible,
            enter = slideInVertically { fullHeight -> -fullHeight } + fadeIn(),
            exit = slideOutVertically { fullHeight -> -fullHeight } + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .align(Alignment.TopCenter)
                .zIndex(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onPrimary),
            ) {
                Text(
                    text = stringResource(R.string.upside_down_portals),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(10.dp)
                )
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    items(portals) { portal ->
                        var distance by remember { mutableStateOf<Float?>(null) }
                        LaunchedEffect(currentPosition, portal.coordinates) {
                            distance = if (currentPosition != null) {
                                calculateDistance(currentPosition, portal.coordinates)
                            } else {
                                null
                            }
                        }
                        onAddMarker(portal.coordinates)
                        Button(onClick = { onNavigateToPortal(portal.id) }) {
                            Text(
                                buildString {
                                    append(portal.street)
                                    distance?.let {
                                        append(" - ${"%.1f".format(distance)} km")
                                        if (it > MAX_DISTANCE_FROM_PORTAL) {
                                            append(" (${stringResource(R.string.out_of_range)})")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        Button(
            onClick = { isPortalListVisible = !isPortalListVisible },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .zIndex(2f)
        ) {
            Text(
                if (isPortalListVisible) stringResource(id = R.string.hide_portals)
                else stringResource(id = R.string.show_portals)
            )
        }
    }
}



@Composable
fun MapboxScreen(
    onUpdateMapView: (MapView) -> Unit,
    modifier: Modifier = Modifier
) {
    val mapViewportState = rememberMapViewportState()
    MapboxMap(
        modifier = modifier,
        mapViewportState = mapViewportState,
    ) {
        MapEffect(Unit) { mapView ->
            onUpdateMapView(mapView)
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            mapViewportState.transitionToFollowPuckState()
        }
    }
}
package fcul.cmov.voidnetwork.ui.screens.portal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
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
import fcul.cmov.voidnetwork.ui.utils.composables.rememberUpsideDownState
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
    ) { paddingValues ->
        PortalsScreenContent(
            onUpdateMapView = { mapView = it },
            currentPosition = currentLocation,
            portals = viewModel.portals,
            onNavigateToPortal = { nav.navigate(Screens.Portal.route.args("id" to it)) },
            modifier = Modifier.padding(paddingValues),
            viewModel = viewModel
        )
    }
}

@Composable
fun PortalsScreenContent(
    onUpdateMapView: (MapView) -> Unit,
    currentPosition: Coordinates?,
    portals: List<Portal>,
    onNavigateToPortal: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PortalViewModel
) {
    var isPortalListVisible by remember { mutableStateOf(false) }
    var localMapViewState by remember { mutableStateOf<MapView?>(null) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MapboxScreen(
            onUpdateMapView = { mapView ->
                localMapViewState = mapView
                onUpdateMapView(mapView)
            },
            modifier = Modifier.fillMaxSize()
        )
        LaunchedEffect(portals, localMapViewState) {
            localMapViewState?.let { mapView ->
                portals.forEach { portal ->
                    viewModel.addPortalMarker(mapView, portal)
                }
            }
        }
        AnimatedVisibility(
            visible = isPortalListVisible,
            enter = slideInVertically { fullHeight -> -fullHeight } + fadeIn(),
            exit = slideOutVertically { fullHeight -> -fullHeight } + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .align(Alignment.TopCenter)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) { detectTapGestures {} }
            ) {
                Text(
                    text = stringResource(R.string.upside_down_portals),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(30.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    items(portals) { portal ->
                        var distance by remember { mutableStateOf<Float?>(null) }

                        LaunchedEffect(currentPosition, portal.coordinates) {
                            distance = currentPosition?.let {
                                calculateDistance(it, portal.coordinates)
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = { // navigate to portal location in map
                                    localMapViewState?.mapboxMap?.setCamera(
                                        CameraOptions.Builder()
                                            .center(Point.fromLngLat(portal.longitude, portal.latitude))
                                            .zoom(16.0)
                                            .build()
                                    )
                                }
                            ) {
                                Text(
                                    buildString {
                                        append(portal.street)
                                        distance?.let {
                                            append(" - ${"%.1f".format(it)} km")
                                            if (it > MAX_DISTANCE_FROM_PORTAL) {
                                                append(" (${stringResource(R.string.out_of_range)})")
                                            }
                                        }
                                    }
                                )
                            }
                            Button(
                                onClick = { onNavigateToPortal(portal.id) },
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = stringResource(R.string.navigate_to_portal),
                                )
                            }
                        }
                    }
                }
            }
        }
        Button(
            onClick = { isPortalListVisible = !isPortalListVisible },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(vertical = 80.dp, horizontal = 20.dp)
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
    val inUpsideDown = rememberUpsideDownState()
    val mapViewState = remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(inUpsideDown, mapViewState.value) {
        mapViewState.value?.mapboxMap?.loadStyle(
            if (inUpsideDown) Style.DARK else Style.STANDARD
        )
    }

    MapboxMap(
        modifier = modifier,
        mapViewportState = mapViewportState,
    ) {
        MapEffect(Unit) { mapView ->
            mapViewState.value = mapView
            onUpdateMapView(mapView)
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            mapView.mapboxMap.loadStyle(
                if (inUpsideDown) Style.DARK else Style.STANDARD
            )
            mapViewportState.transitionToFollowPuckState()
        }
    }
}

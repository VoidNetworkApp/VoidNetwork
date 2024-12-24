package fcul.cmov.voidnetwork.ui.screens.portal


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.domain.Portal
import fcul.cmov.voidnetwork.ui.utils.MAX_DISTANCE_FROM_PORTAL
import fcul.cmov.voidnetwork.ui.utils.calculateDistance
import fcul.cmov.voidnetwork.ui.utils.composables.ScreenWithTopBar
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel

@Composable
fun PortalScreen(
    nav: NavController,
    viewModel: PortalViewModel,
    id: String,
    currentLocation: Coordinates?,
) {
    val portal = viewModel.getPortalOrNull(id)
    if (portal == null) {
        // navigate back if the portal is deleted or not found
        LaunchedEffect(Unit) {
            nav.popBackStack()
        }
        return
    }
    ScreenWithTopBar(
        title = stringResource(R.string.portal_info),
        nav = nav
    ) { paddingValues ->
        PortalScreenContent(
            modifier = Modifier.padding(paddingValues),
            portal = portal,
            currentLocation = currentLocation,
        )
    }
}

@Composable
fun PortalScreenContent(
    modifier: Modifier = Modifier,
    portal: Portal,
    currentLocation: Coordinates?,
) {
    var distance by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(currentLocation) {
        if (currentLocation != null) {
            distance = calculateDistance(currentLocation, portal.coordinates)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = portal.street,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )

        // TODO: Replace with captured image from camera (stored in firebase)
        AsyncImage(
            model = "https://i.ibb.co/vzVc0v1/treeportal.png",
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.portal_captured_with_camera),
            modifier = Modifier
                .size(300.dp)
                .background(Color.Black)
        )
        Text(
            text = portal.coordinates.toString()
        )
        distance?.let {
            Text(text = stringResource(R.string.x_km_away).replace("{distance}", it.toString()))
            Text(
                text = if (it > MAX_DISTANCE_FROM_PORTAL) {
                    " (${stringResource(R.string.out_of_range)})"
                } else {
                    " (${stringResource(R.string.in_range)})"
                }
            )
        }
    }
}


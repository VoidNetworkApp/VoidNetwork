package fcul.cmov.voidnetwork.ui.screens.portal


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.domain.Portal
import fcul.cmov.voidnetwork.ui.utils.MAX_DISTANCE_FROM_PORTAL
import fcul.cmov.voidnetwork.ui.utils.calculateDistance
import fcul.cmov.voidnetwork.ui.utils.composables.ScreenWithTopBar
import fcul.cmov.voidnetwork.ui.utils.readFile
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
        modifier = modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = portal.street,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )
        var imageUrl = ""
        readFile(portal.id, onImageUrlRetrieved = { url -> imageUrl = url })
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.portal_captured_with_camera),
            modifier = Modifier
                .size(300.dp)
                .background(Color.Black)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text("${stringResource(R.string.latitude)}: ${portal.coordinates.latitude}")
            Text("${stringResource(R.string.longitude)}: ${portal.coordinates.longitude}")
            distance?.let {
                Text(text = stringResource(R.string.x_km_away).replace("{distance}", "%.1f".format(it)))
                Text(
                    text = if (it > MAX_DISTANCE_FROM_PORTAL) {
                        " (${stringResource(R.string.out_of_range)})"
                    } else {
                        " (${stringResource(R.string.in_range)})"
                    },
                    modifier = Modifier.padding(10.dp),
                    fontSize = 15.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PortalScreenPreview() {
    PortalScreenContent(
        portal = Portal(
            id = "123",
            street = "Rua das Flores",
            coordinates = Coordinates(0.0, 0.0),
        ),
        currentLocation = Coordinates(0.0, 0.0),
    )
}


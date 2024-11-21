package fcul.cmov.voidnetwork.ui.screens.portal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.ui.navigation.Screens

data class Portal(val street: String, val distance: Float) // other props...

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalScreen(nav: NavController) {
    val portals = listOf(
        Portal("Rua das Flores", 2.1f),
        Portal("Avenida do Sol", 23.4f),
        Portal("Rua da Glória", 35.9f)
    )
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
                    contentDescription = "Add Portal",
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
        Text("Upside Down Portals")

        AsyncImage(
            model = "https://developers.google.com/static/maps/images/landing/hero_geocoding_api.png",
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "Embed Google Maps",
            modifier = Modifier.size(300.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            portals.forEach { portal ->
                Button(onClick = { /*TODO*/ }) {
                    Text("${portal.street} (${portal.distance} km)" +
                            if (portal.distance > 5) " - Out of range" else ""
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PortalScreenPreview() {
    PortalScreen(nav = NavController(LocalContext.current))
}
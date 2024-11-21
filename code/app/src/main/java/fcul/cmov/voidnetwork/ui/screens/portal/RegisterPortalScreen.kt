package fcul.cmov.voidnetwork.ui.screens.portal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.ui.utils.ScreenWithTopBar

@Composable
fun RegisterPortalScreen(nav: NavController) {
    ScreenWithTopBar(title = "Register New Portal", nav = nav) { paddingValues ->
        RegisterPortalScreenContent(
            nav = nav,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun RegisterPortalScreenContent(nav: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        AsyncImage(
            model = "https://i.ibb.co/vzVc0v1/treeportal.png",
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "Portal captured with camera",
            modifier = Modifier
                .size(300.dp)
                .background(Color.Black)
        )

        Button(onClick = { /*TODO*/ }, enabled = false) {
            Text("Register Portal")
        }
        Text("Waiting for photo verification")
        Button(onClick = { /*TODO*/ }) {
            Text("Open Camera")
        }
    }
}
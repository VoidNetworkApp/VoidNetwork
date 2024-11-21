package fcul.cmov.voidnetwork.ui.screens.communication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.ui.navigation.Screens
import fcul.cmov.voidnetwork.ui.utils.args
import fcul.cmov.voidnetwork.ui.utils.generateRandomUUID

@Composable
fun CommunicationScreen(nav: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Void Network",
            fontSize = 50.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(20.dp)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            LanguageView(
                languageSelected = "Morse Code", // TODO: get selected language
                onLanguageSelection = { nav.navigate(Screens.Languages.route) },
                onAddLanguage = {
                    val randomId = generateRandomUUID() // TODO: change later
                    nav.navigate(Screens.Language.route.args("id" to randomId))
                }
            )
            PortalSelectionView()
            MessageView()
        }
    }
}

@Composable
fun LanguageView(
    languageSelected: String,
    onLanguageSelection: () -> Unit,
    onAddLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = onLanguageSelection) {
            Text(languageSelected)
        }
        Button(onClick = onAddLanguage) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Language"
            )
        }
    }
}

@Composable
fun PortalSelectionView(
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = { /*TODO*/ }) {
            Text("No Portal Selected")
            Text("") // street name
        }
    }
}

@Composable
fun MessageView(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = { /*TODO*/ }) {
            Text("Send Automatic Message")
        }
        Button(onClick = { /*TODO*/ }) {
            Text("Send Manual Message")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunicationScreenPreview() {
    CommunicationScreen(nav = NavController(LocalContext.current))
}
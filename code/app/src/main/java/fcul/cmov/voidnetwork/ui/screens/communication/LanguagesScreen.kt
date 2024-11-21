package fcul.cmov.voidnetwork.ui.screens.communication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.ui.utils.ScreenWithTopBar

@Composable
fun LanguagesScreen(nav: NavController) {
    ScreenWithTopBar(title = "Language Selection", nav = nav) { paddingValues ->
        LanguagesScreenContent(
            nav = nav,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun LanguagesScreenContent(nav: NavController, modifier: Modifier = Modifier) {
    Column (
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Select a language")
    }
}
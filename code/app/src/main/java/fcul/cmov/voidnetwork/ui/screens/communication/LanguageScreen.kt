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
import fcul.cmov.voidnetwork.ui.screens.portal.RegisterPortalScreenContent
import fcul.cmov.voidnetwork.ui.utils.ScreenWithTopBar

@Composable
fun LanguageScreen(nav: NavController, id: String) {
    ScreenWithTopBar(title = "Add/Edit Language", nav = nav) { paddingValues ->
        LanguageScreenContent(
            nav = nav,
            id = id,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun LanguageScreenContent(nav: NavController, id: String, modifier: Modifier = Modifier) {
    Column (
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Add/Edit Language $id")
    }
}

package fcul.cmov.voidnetwork.ui.screens.communication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.ui.screens.portal.RegisterPortalScreenContent
import fcul.cmov.voidnetwork.ui.utils.ScreenWithTopBar
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel

@Composable
fun LanguageScreen(
    nav: NavController,
    viewModel: LanguageViewModel,
    id: String
) {
    ScreenWithTopBar(
        title = stringResource(R.string.add_edit_language),
        nav = nav
    ) { paddingValues ->
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
        Text(stringResource(R.string.add_edit_language))
        Text(id)
    }
}

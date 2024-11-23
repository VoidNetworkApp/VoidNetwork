package fcul.cmov.voidnetwork.ui.screens.communication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Language
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
            modifier = Modifier.padding(paddingValues),
            language = viewModel.getOrAddLanguageById(id)
        )
    }
}

@Composable
fun LanguageScreenContent(
    nav: NavController,
    language: Language,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(language.name)
        Spacer(Modifier.size(20.dp))
        language.dictionary.forEach {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(it.key)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                    contentDescription = stringResource(R.string.arrow_right)
                )
                Text(it.value)
            }
        }
    }
}

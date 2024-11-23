package fcul.cmov.voidnetwork.ui.screens.communication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
        title = stringResource(R.string.language_dictionary),
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = language.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(Modifier.size(50.dp))

        // dictionary table
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // table header row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.code),
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = stringResource(R.string.message),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            // table rows
            items(language.dictionary.toList()) { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = key,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = value,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

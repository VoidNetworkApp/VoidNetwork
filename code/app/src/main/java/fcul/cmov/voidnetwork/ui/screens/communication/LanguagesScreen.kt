package fcul.cmov.voidnetwork.ui.screens.communication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Language
import fcul.cmov.voidnetwork.ui.navigation.Screens
import fcul.cmov.voidnetwork.ui.utils.composables.ScreenWithTopBar
import fcul.cmov.voidnetwork.ui.utils.args
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagesScreen(
    nav: NavController,
    viewModel: LanguageViewModel
) {
    ScreenWithTopBar(
        title = stringResource(R.string.language_selection),
        nav = nav
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.addLanguage(
                            onCompleted = { id ->
                                nav.navigate(Screens.Language.route.args("id" to id))
                            }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_language),
                        tint = Color.White
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End, // bottom-right,
            content = { paddingValues ->
                LanguagesScreenContent(
                    nav = nav,
                    languages = viewModel.getLanguages(),
                    onSelectLanguage = viewModel::selectLanguage,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        )
    }
}

@Composable
fun LanguagesScreenContent(
    nav: NavController,
    languages: List<Language>,
    onSelectLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        languages.forEach { language ->
            LanguageView(
                language = language,
                onSelectLanguage = {
                    onSelectLanguage(language.id)
                    nav.popBackStack()
                },
                onViewLanguage = {
                    nav.navigate(Screens.Language.route.args("id" to language.id))
                },
            )
        }
    }
}

@Composable
fun LanguageView(
    language: Language,
    onSelectLanguage: () -> Unit,
    onViewLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onSelectLanguage,
            modifier = modifier.fillMaxWidth(0.6f)
        ) {
            Text(language.name)
        }
        Row {
            Button(
                onClick = onViewLanguage,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.view_language),
                )
            }
        }
    }
}

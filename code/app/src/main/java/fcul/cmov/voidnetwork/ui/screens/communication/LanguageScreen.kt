package fcul.cmov.voidnetwork.ui.screens.communication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Language
import fcul.cmov.voidnetwork.ui.utils.MAX_MESSAGE_LENGTH
import fcul.cmov.voidnetwork.ui.utils.composables.ScreenWithTopBar
import fcul.cmov.voidnetwork.ui.utils.composables.rememberPressSequence
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel

@Composable
fun LanguageScreen(
    nav: NavController,
    viewModel: LanguageViewModel,
    id: String
) {
    val language = viewModel.getLanguageOrNull(id)
    if (language == null) {
        // navigate back if the language is deleted or not found
        LaunchedEffect(Unit) {
            nav.popBackStack()
        }
        return
    }
    ScreenWithTopBar(
        title = stringResource(R.string.language_dictionary),
        nav = nav
    ) { paddingValues ->
        LanguageScreenContent(
            modifier = Modifier.padding(paddingValues),
            language = viewModel.getLanguage(id),
            onDeleteMessage = { code -> viewModel.onDeleteMessageFromLanguage(id, code) },
            onUpdateLanguageDictionary = { code, msg ->
                viewModel.onUpdateLanguageDictionary(id, code, msg)
            },
            onDeleteLanguage = { viewModel.deleteLanguage(id) },
            onEditLanguage = { viewModel.onEditLanguage(language.id, it) }
        )
    }
}

@Composable
fun LanguageScreenContent(
    language: Language,
    onUpdateLanguageDictionary: (String, String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onDeleteLanguage: () -> Unit,
    onEditLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAddingMessage by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxHeight().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        LanguageTopView(
            language = language,
            onDeleteLanguage = onDeleteLanguage,
            onEditLanguage = onEditLanguage,
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            LanguageDictionary(
                language = language,
                onDeleteMessage = onDeleteMessage,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(20.dp)
        ) {
            if (isAddingMessage) {
                AddMessageForm(
                    onUpdateLanguageDictionary = { code, msg ->
                        onUpdateLanguageDictionary(code, msg)
                        isAddingMessage = false
                    }
                )
            }
            Button(onClick = { isAddingMessage = !isAddingMessage }) {
                Text(
                    text = if (isAddingMessage) stringResource(R.string.cancel)
                    else stringResource(R.string.add_message)
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageTopView(
    language: Language,
    onDeleteLanguage: () -> Unit,
    onEditLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var languageName by rememberSaveable { mutableStateOf(language.name) }

    // in case of external changes, keep language name in sync
    LaunchedEffect(language.name) {
        if (!isEditing) languageName = language.name
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isEditing) {
            TextField(
                value = languageName,
                onValueChange = { languageName = it },
                label = { Text(stringResource(R.string.language_name)) },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    onEditLanguage(languageName)
                    isEditing = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.save_changes),
                )
            }
        } else {
            Text(
                text = language.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 30.dp).weight(1f)
            )
            Button(
                onClick = { isEditing = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit_language)
                )
            }
        }
        Button(
            onClick = onDeleteLanguage,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete_language),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMessageForm(
    onUpdateLanguageDictionary: (String, String) -> Unit
) {
    var message by rememberSaveable { mutableStateOf("") }
    val (sequence, pressModifier, resetSequence) = rememberPressSequence()
    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextField(
            value = message,
            onValueChange = { message = it.take(MAX_MESSAGE_LENGTH) },
            label = { Text(stringResource(R.string.enter_translation)) },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
                .padding(16.dp)
                .fillMaxWidth()
                .then(pressModifier),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.hold_or_tap),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = sequence,
            fontSize = 40.sp,
            modifier = Modifier.padding(0.dp)
        )
        Button(
            onClick = {
                onUpdateLanguageDictionary(sequence, message)
                resetSequence()
                message = ""
            },
            enabled = sequence.isNotBlank() && message.isNotBlank()
        ) {
            Text(text = stringResource(R.string.add_message))
        }
    }
}

@Composable
fun LanguageDictionary(
    language: Language,
    onDeleteMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        // dictionary table header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.code),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.message),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(2f),
            )
            Text(
                text = stringResource(R.string.actions),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(0.5f),
                color = Color.Transparent
            )
        }
        // dictionary table rows
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(language.dictionary.toList()) { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(2f),
                    )
                    Button(
                        onClick = { onDeleteMessage(key) },
                        contentPadding = PaddingValues(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = stringResource(R.string.delete_message),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

package fcul.cmov.voidnetwork.ui.screens.communication

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Language
import fcul.cmov.voidnetwork.ui.utils.ScreenWithTopBar
import fcul.cmov.voidnetwork.ui.viewmodels.LanguageViewModel
import fcul.cmov.voidnetwork.ui.viewmodels.MAX_CODE_LENGTH
import fcul.cmov.voidnetwork.ui.viewmodels.MAX_MESSAGE_LENGTH

const val MIN_PRESS_DURATION_SHORT_PRESS = 250 // ms
const val DOT = "."
const val DASH = "-"

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
            language = viewModel.getOrAddLanguageById(id),
            onUpdateLanguageDictionary = { code, msg ->
                viewModel.onUpdateLanguageDictionary(id, code, msg)
            }
        )
    }
}

@Composable
fun LanguageScreenContent(
    nav: NavController,
    language: Language,
    onUpdateLanguageDictionary: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAddingMessage by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LanguageDictionary(
            language = language,
            modifier = modifier,
            onDeleteMessage = { language.dictionary.remove(it) }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(20.dp)
        ) {
            Button(onClick = { isAddingMessage = !isAddingMessage }) {
                Text(
                    text = if (isAddingMessage) stringResource(R.string.cancel)
                        else stringResource(R.string.add_message)
                )
            }
            if (isAddingMessage) {
                AddMessageForm(
                    onUpdateLanguageDictionary = { code, msg ->
                        onUpdateLanguageDictionary(code, msg)
                        isAddingMessage = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMessageForm(
    onUpdateLanguageDictionary: (String, String) -> Unit
) {
    var code by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var isPressing by rememberSaveable { mutableStateOf(false) }
    var pressStartTime by rememberSaveable { mutableStateOf(0L) }

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
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            if (code.length >= MAX_CODE_LENGTH) code = ""
                            isPressing = true
                            pressStartTime = System.currentTimeMillis()
                            tryAwaitRelease()
                            val pressDuration = System.currentTimeMillis() - pressStartTime
                            isPressing = false
                            code += if (pressDuration < MIN_PRESS_DURATION_SHORT_PRESS) {
                                DOT // short press
                            } else {
                                DASH // long press
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.hold_or_tap),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = code,
            fontSize = 40.sp
        )
        Button(
            onClick = {
                if (code.isNotBlank() && message.isNotBlank()) {
                    onUpdateLanguageDictionary(code, message)
                    code = ""
                    message = ""
                }
            },
            enabled = code.isNotBlank() && message.isNotBlank()
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
        modifier = modifier,
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
                    Button(onClick = { onDeleteMessage(key) }) {
                        Icon(
                            Icons.Filled.Remove,
                            contentDescription = stringResource(R.string.delete_message)
                        )
                    }
                }
            }
        }
    }
}

package fcul.cmov.voidnetwork.ui.screens.communication

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.CommunicationMode
import fcul.cmov.voidnetwork.domain.Language
import fcul.cmov.voidnetwork.domain.Portal
import fcul.cmov.voidnetwork.repository.LanguagesRepository
import fcul.cmov.voidnetwork.ui.navigation.Screens
import fcul.cmov.voidnetwork.ui.utils.composables.LightSignalMessage
import fcul.cmov.voidnetwork.ui.utils.composables.TouchSignalMessage
import fcul.cmov.voidnetwork.ui.viewmodels.MessageSenderViewModel
import kotlinx.coroutines.delay

@Composable
fun CommunicationScreen(
    nav: NavController,
    viewModel: MessageSenderViewModel,
    portalSelected: Portal?,
    languageSelected: Language?,
    onTranslate: (String) -> String?,
    onUpdateDictionary: (String, String) -> Unit,
    navigateToPage: (Int) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 40.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(20.dp)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LanguageView(
                    languageSelected = languageSelected?.name ?: stringResource(R.string.no_language_selected),
                    onLanguageSelection = { nav.navigate(Screens.Languages.route) },
                )
                Spacer(Modifier.size(10.dp))
                PortalSelectionView(
                    portalSelected = portalSelected,
                    onPortalsClick = { navigateToPage(2) }
                )
                Spacer(Modifier.size(10.dp))
            }
            MessageView(
                languageSelected = languageSelected,
                sendSignal = { viewModel.sendSignal(languageSelected?.id, it) },
                onTranslate = onTranslate,
                onUpdateDictionary = onUpdateDictionary,
            )
            Spacer(Modifier.size(10.dp))
        }
    }
}

@Composable
fun LanguageView(
    languageSelected: String,
    onLanguageSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        onClick = onLanguageSelection
    ) {
        Text(languageSelected)
    }
}

@Composable
fun PortalSelectionView(
    portalSelected: Portal?,
    onPortalsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = onPortalsClick) {
            Text(
                if (portalSelected == null) stringResource(R.string.no_portal_selected)
                else stringResource(R.string.portal_selected)
            )
            portalSelected?.let { portal ->
                Text(text = portal.street)
            }
        }
    }
}

@Composable
fun MessageView(
    languageSelected: Language?,
    sendSignal: (String) -> Unit,
    onTranslate: (String) -> String?,
    onUpdateDictionary: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var communicationMode: CommunicationMode by rememberSaveable { mutableStateOf(CommunicationMode.TOUCH)}

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            communicationMode = communicationMode.next()
        }) {
            Text(
                text = stringResource(
                    when (communicationMode) {
                        CommunicationMode.TOUCH -> R.string.touch
                        CommunicationMode.LIGHT -> R.string.light
                        CommunicationMode.AUTO -> R.string.auto
                    }
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize(0.9f)
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            val onSubmit = { sequence: String, message: String ->
                onUpdateDictionary(sequence, message)
                sendSignal(sequence)
            }
            when (communicationMode) {
                CommunicationMode.TOUCH ->
                    TouchMessageView(onTranslate, onSubmit)
                CommunicationMode.LIGHT ->
                    LightMessageView(onTranslate, onSubmit)
                CommunicationMode.AUTO ->
                    AutomaticMessageView(languageSelected, sendSignal)
            }
        }
    }
}

@Composable
fun TouchMessageView(
    onTranslate: (String) -> String?,
    onSubmit: (String, String) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        TouchSignalMessage(
            submitText = stringResource(R.string.send),
            onTranslate = onTranslate,
            onSubmit = onSubmit
        )
    }
}

@Composable
fun LightMessageView(
    onTranslate: (String) -> String?,
    onSubmit: (String, String) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        LightSignalMessage(
            submitText = stringResource(R.string.send),
            onTranslate = onTranslate,
            onSubmit = onSubmit
        )
    }
}

@Composable
fun AutomaticMessageView(
    languageSelected: Language?,
    sendSignal: (String) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.8f)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (languageSelected != null) {
            languageSelected.dictionary.forEach { (code, message) ->
                Button(
                    onClick = { sendSignal(code) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Text(message)
                }
            }
        } else {
            Text(stringResource(R.string.no_language_selected))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CommunicationScreenPreview() {
    CommunicationScreen(
        nav = NavController(LocalContext.current),
        viewModel = MessageSenderViewModel(LocalContext.current as Application, LanguagesRepository()),
        languageSelected = null,
        portalSelected = null,
        onTranslate = { null },
        onUpdateDictionary = { _, _ -> }
    )
}
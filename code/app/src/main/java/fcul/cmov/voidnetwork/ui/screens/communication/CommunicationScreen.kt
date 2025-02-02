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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.CommunicationMode
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.domain.Language
import fcul.cmov.voidnetwork.domain.Message
import fcul.cmov.voidnetwork.domain.Portal
import fcul.cmov.voidnetwork.services.MessageReceiverForegroundService
import fcul.cmov.voidnetwork.storage.AppSettings
import fcul.cmov.voidnetwork.ui.navigation.Screens
import fcul.cmov.voidnetwork.ui.utils.composables.LightSignalMessage
import fcul.cmov.voidnetwork.ui.utils.composables.Popup
import fcul.cmov.voidnetwork.ui.utils.composables.TouchSignalMessage
import fcul.cmov.voidnetwork.ui.utils.composables.rememberClosestPortal
import fcul.cmov.voidnetwork.ui.utils.composables.rememberSensorsPopupState
import fcul.cmov.voidnetwork.ui.utils.composables.rememberUpdateDictionaryWithConfirmation
import fcul.cmov.voidnetwork.ui.utils.composables.rememberUpsideDownState
import fcul.cmov.voidnetwork.ui.utils.isWithinPortalRange
import fcul.cmov.voidnetwork.ui.viewmodels.MessageSenderViewModel

@Composable
fun CommunicationScreen(
    nav: NavController,
    viewModel: MessageSenderViewModel,
    currentPosition: Coordinates?,
    portals: List<Portal>,
    languageSelected: Language?,
    onTranslate: (String) -> String?,
    onUpdateDictionary: (String, String) -> Unit,
    navigateToPage: (Int) -> Unit = {},
) {
    val closestPortal = rememberClosestPortal(currentPosition, portals)
    val inUpsideDown = rememberUpsideDownState()
    val (replaceSignalPopup, onUpdateDictionaryWithConfirmation) =
        rememberUpdateDictionaryWithConfirmation(languageSelected, onUpdateDictionary)
    val (showSensorsPopup, sensorsPopup) = rememberSensorsPopupState(currentPosition)
    val withinPortalRange = closestPortal != null

    Box(modifier = Modifier.fillMaxSize()) {
        AllowReceiveSignalsButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
        )
        IconButton(onClick = showSensorsPopup,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.BugReport,
                contentDescription = stringResource(R.string.sensors_info),
                modifier = Modifier.size(30.dp)
            )
        }
        sensorsPopup()
        replaceSignalPopup()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 40.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(20.dp)
            )
            Spacer(Modifier.size(20.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        if (inUpsideDown) stringResource(R.string.in_upside_down)
                        else stringResource(R.string.in_real_world)
                    )
                    Spacer(Modifier.size(20.dp))
                    LanguageView(
                        languageSelected = languageSelected?.name ?: stringResource(R.string.no_language_selected),
                        onLanguageSelection = { nav.navigate(Screens.Languages.route) },
                    )
                    Spacer(Modifier.size(5.dp))
                    PortalSelectedView(
                        portal = closestPortal,
                        onPortalsClick = { navigateToPage(2) }
                    )
                    Spacer(Modifier.size(5.dp))
                }
                MessageView(
                    languageSelected = languageSelected,
                    sendSignal = { viewModel.sendSignal(languageSelected?.id, it) },
                    onTranslate = onTranslate,
                    onUpdateDictionary = onUpdateDictionaryWithConfirmation,
                    enabled = !inUpsideDown || withinPortalRange
                )
                Spacer(Modifier.size(5.dp))
            }
        }
    }
}

@Composable
fun AllowReceiveSignalsButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val settings by remember { mutableStateOf(AppSettings(context)) }
    var allowReceiveSignals by rememberSaveable { mutableStateOf(settings.allowReceiveSignals) }

    IconButton(
        modifier = modifier,
        onClick = {
            allowReceiveSignals = !allowReceiveSignals
            settings.allowReceiveSignals = allowReceiveSignals
            if (allowReceiveSignals) {
                MessageReceiverForegroundService.start(context)
            } else {
                MessageReceiverForegroundService.stop(context)
            }
        },
    ) {
        Icon(
            imageVector = if (allowReceiveSignals) Icons.Default.Sensors else Icons.Default.SensorsOff,
            contentDescription = stringResource(R.string.allow_receive_signals),
            modifier = Modifier.size(30.dp)
        )
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
fun PortalSelectedView(
    portal: Portal?,
    onPortalsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = onPortalsClick) {
            Text(portal?.street ?: stringResource(R.string.no_portals_nearby))
        }
    }
}

@Composable
fun MessageView(
    languageSelected: Language?,
    sendSignal: (String) -> Unit,
    onTranslate: (String) -> String?,
    onUpdateDictionary: (String, String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var communicationMode: CommunicationMode by rememberSaveable { mutableStateOf(CommunicationMode.TOUCH)}

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (enabled) {
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

            if (enabled) {
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
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.cannot_send_messages))
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = stringResource(R.string.cannot_send_messages),
                        modifier = Modifier.size(150.dp).padding(30.dp)
                    )
                    Text(stringResource(R.string.outside_portal_range))
                }
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
        viewModel = MessageSenderViewModel(LocalContext.current as Application),
        languageSelected = null,
        portals = emptyList(),
        currentPosition = Coordinates(0.0, 0.0),
        onTranslate = { null },
        onUpdateDictionary = { _, _ -> }
    )
}
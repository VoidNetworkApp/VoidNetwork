package fcul.cmov.voidnetwork.ui.screens.communication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import fcul.cmov.voidnetwork.domain.CommunicationType
import fcul.cmov.voidnetwork.domain.Language
import fcul.cmov.voidnetwork.domain.Portal
import fcul.cmov.voidnetwork.ui.navigation.Screens
import fcul.cmov.voidnetwork.ui.viewmodels.CommunicationViewModel
import kotlinx.coroutines.delay

@Composable
fun CommunicationScreen(
    nav: NavController,
    viewModel: CommunicationViewModel,
    portalSelected: Portal?,
    languageSelected: Language?,
    navigateToPage: (Int) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 50.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(50.dp)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
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
            }
            MessageView(languageSelected)
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
    modifier: Modifier = Modifier,
) {
    var communicationMode: CommunicationMode by rememberSaveable { mutableStateOf(CommunicationMode.MANUAL)}
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            communicationMode = communicationMode.other()
        }) {
            Text(
                text = stringResource(
                    when (communicationMode) {
                        CommunicationMode.MANUAL -> R.string.manual_message
                        CommunicationMode.AUTOMATIC -> R.string.automatic_message
                    }
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxHeight(0.6f)
                .fillMaxWidth(0.9f)
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (communicationMode == CommunicationMode.MANUAL) {
                ManualMessageView()
            } else {
                AutomaticMessageView(languageSelected)
            }
        }

    }
}

@Composable
fun ManualMessageView() {
    var communicationType: CommunicationType by rememberSaveable { mutableStateOf(CommunicationType.TOUCH)}

    Button(onClick = {
        communicationType = communicationType.other()
    }) {
        Text(
            text = stringResource(
                when (communicationType) {
                    CommunicationType.TOUCH -> R.string.touch
                    CommunicationType.LIGHT -> R.string.light
                }
            )
        )
    }

    if (communicationType == CommunicationType.TOUCH) {
        TouchMessageView()
    } else {
        LightMessageView()
    }

}

@Composable
fun TouchMessageView() {
    Column {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = { /*TODO*/ })
        ) {
            Icon(
                imageVector = Icons.Filled.TouchApp,
                contentDescription = stringResource(R.string.touch),
            )
        }
    }
}

@Composable
fun LightMessageView() {
    // simulate light intensity changes TODO: remove later
    var lightIntensity: Int by rememberSaveable { mutableStateOf(0) }
    val lightColor = Color(255, 255, 255, lightIntensity * 255 / 100)
    LaunchedEffect(lightIntensity) {
        while (true) {
            lightIntensity = (lightIntensity + 1) % 100
            delay(1000)
        }
    }

    Column {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(lightColor)
        ) {
            Icon(
                imageVector = Icons.Filled.LightMode,
                contentDescription = stringResource(R.string.touch),
                tint = Color.Black
            )
        }
    }
}

@Composable
fun AutomaticMessageView(languageSelected: Language?) {
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
            languageSelected.dictionary.values.forEach { message ->
                Button(
                    onClick = { /* TODO */ },
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
        viewModel = CommunicationViewModel(),
        languageSelected = null,
        portalSelected = null
    )
}
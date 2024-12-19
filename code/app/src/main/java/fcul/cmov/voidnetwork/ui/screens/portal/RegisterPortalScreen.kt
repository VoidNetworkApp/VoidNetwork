package fcul.cmov.voidnetwork.ui.screens.portal

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.ui.navigation.Screens
import fcul.cmov.voidnetwork.ui.utils.composables.CameraButton
import fcul.cmov.voidnetwork.ui.utils.composables.CameraPhoto
import fcul.cmov.voidnetwork.ui.utils.composables.ScreenWithTopBar
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel

@Composable
fun RegisterPortalScreen(
    nav: NavController,
    viewModel: PortalViewModel,
    coordinates: Coordinates?
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (viewModel.capturedImageUri == null) {
            viewModel.createImageUri(context)
        }
    }
    ScreenWithTopBar(
        title = stringResource(R.string.register_portal),
        nav = nav
    ) { paddingValues ->
        RegisterPortalScreenContent(
            nav = nav,
            modifier = Modifier.padding(paddingValues),
            capturedImageUri = viewModel.capturedImageUri,
            onPhotoCaptured = { viewModel.capturedImageUri = it }
        )
    }
}

@Composable
fun RegisterPortalScreenContent(
    nav: NavController,
    capturedImageUri: Uri?,
    onPhotoCaptured: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {

        CameraPhoto(capturedImageUri)
        Button(
            enabled = false, // TODO: only when photo is verified to be a portal
            onClick = {
                // TODO: complete this
                nav.navigate(Screens.Main.route)
            }
        ) {
            Text(stringResource(R.string.register_portal))
        }
        Text(stringResource(R.string.waiting_for_photo_verification))
        CameraButton(
            onPhotoCaptured = { onPhotoCaptured(it) },
            text = stringResource(R.string.capture_photo),
            uri = capturedImageUri ?: Uri.EMPTY
        )
    }
}
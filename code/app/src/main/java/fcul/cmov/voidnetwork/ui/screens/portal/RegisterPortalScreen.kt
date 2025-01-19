package fcul.cmov.voidnetwork.ui.screens.portal

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.ui.utils.composables.CameraButton
import fcul.cmov.voidnetwork.ui.utils.composables.CameraPhoto
import fcul.cmov.voidnetwork.ui.utils.composables.ScreenWithTopBar
import fcul.cmov.voidnetwork.ui.utils.scanPortalInCapturedImage
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterPortalScreen(
    nav: NavController,
    viewModel: PortalViewModel,
    currentLocation: Coordinates?
) {
    var capturedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    ScreenWithTopBar(
        title = stringResource(R.string.register_portal),
        nav = nav
    ) { paddingValues ->
        RegisterPortalScreenContent(
            modifier = Modifier.padding(paddingValues),
            capturedImageUri = capturedImageUri,
            onPhotoCaptured = { capturedImageUri = it },
            locationEnabled = currentLocation != null,
            onRegisterPortal = {
                currentLocation?.let { viewModel.registerPortal(it) }
                nav.popBackStack()
            },
        )
    }
}

@Composable
fun RegisterPortalScreenContent(
    modifier: Modifier = Modifier,
    capturedImageUri: Uri?,
    locationEnabled: Boolean,
    onPhotoCaptured: (Uri) -> Unit,
    onRegisterPortal: () -> Unit,
) {
    val context = LocalContext.current
    var portalDetected by remember { mutableStateOf<Boolean?>(null) }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        CameraPhoto(capturedImageUri)
        Button(
            enabled = portalDetected == true && locationEnabled,
            onClick = onRegisterPortal,
        ) {
            Text(stringResource(R.string.register_portal))
        }

        if (locationEnabled) {
            if (portalDetected == null) {
                Text(stringResource(R.string.waiting_for_photo_verification))
            } else {
                if (portalDetected == true) {
                    Text(stringResource(R.string.portal_detected))
                } else {
                    Text(stringResource(R.string.portal_not_found))
                }
            }
            CameraButton(
                onPhotoCaptured = {
                    onPhotoCaptured(it)
                    CoroutineScope(Dispatchers.IO).launch {
                        portalDetected = scanPortalInCapturedImage(context, it)
                    }
                },
                text = stringResource(R.string.capture_photo),
            )
        } else {
            Column {
                Text(stringResource(R.string.location_disabled))
                Text(stringResource(R.string.enable_location_to_register_portal))
            }
        }
    }
}

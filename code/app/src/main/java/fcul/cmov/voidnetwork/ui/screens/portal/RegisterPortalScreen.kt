package fcul.cmov.voidnetwork.ui.screens.portal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.domain.Labels
import fcul.cmov.voidnetwork.ui.utils.composables.CameraButton
import fcul.cmov.voidnetwork.ui.utils.composables.CameraPhoto
import fcul.cmov.voidnetwork.ui.utils.composables.ScreenWithTopBar
import fcul.cmov.voidnetwork.ui.utils.composables.createImageFile
import fcul.cmov.voidnetwork.ui.utils.detectTree
import fcul.cmov.voidnetwork.ui.utils.imageLabeling
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
            onPhotoCaptured = { viewModel.capturedImageUri = it },
            onRegisterPortal = {
                currentLocation?.let { viewModel.registerPortal(it) }
                nav.navigateUp()
            },
        )
    }
}

@Composable
fun RegisterPortalScreenContent(
    nav: NavController,
    modifier: Modifier = Modifier,
    capturedImageUri: Uri?,
    onPhotoCaptured: (Uri) -> Unit,
    onRegisterPortal: () -> Unit,
) {
    val context = LocalContext.current
    var photoLabels by remember { mutableStateOf(Labels()) }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        // TODO Second new photo doesn't appear
        CameraPhoto(capturedImageUri)
        Button(
            enabled = photoLabels.lables.isNotEmpty() || photoLabels.detectedTrees,
            onClick = {
                Log.d("MLLog12", capturedImageUri.toString())
                onRegisterPortal()
                //nav.popBackStack()
            },
        ) {
            Text(stringResource(R.string.register_portal))
        }

        if(photoLabels.lables.isEmpty()) {
            Text(stringResource(R.string.waiting_for_photo_verification))
        } else {
            LabelItems(photoLabels)
            if (photoLabels.detectedTrees) {
                Text("Portal Found, want to register it?")
            } else {
                Text("Portal not Found, try again")
            }
        }
        Log.d("MLLog1", capturedImageUri.toString())
        CameraButton(
            onPhotoCaptured = {
                onPhotoCaptured(it)
                Log.d("MLLog", "Button Clicked")
                if (capturedImageUri != null) {
                    Log.d("MLLog", "Available URI")
                    CoroutineScope(Dispatchers.IO).launch {
                        photoLabels =  imageLabeling(context, capturedImageUri) ?: Labels()
                    }
                    Log.d("MLLog11", capturedImageUri.toString())
                } else {
                    Log.d("MLLog", "URI not available")
                }
            },
            text = stringResource(R.string.capture_photo),
            uri = capturedImageUri ?: Uri.EMPTY
        )
    }
}

@Composable
fun LabelItems(labels: Labels) {
    for(label in labels.lables) {
        Text(label.text + " " + label.confidence.toString() + " " + label.index.toString())
    }
}

fun createImageUri(context: Context ): Uri {
    val imageFile = context.createImageFile()
    var capturedImageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
    return capturedImageUri
}
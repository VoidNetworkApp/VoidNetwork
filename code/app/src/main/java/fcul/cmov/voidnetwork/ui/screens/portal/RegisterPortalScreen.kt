package fcul.cmov.voidnetwork.ui.screens.portal

import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    capturedImageUri: Uri?,
    onPhotoCaptured: (Uri) -> Unit,
    onRegisterPortal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val file = context.createImageFile()
    Log.d("TAG_FilePath", file.absolutePath)
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.getPackageName() + ".provider", file
    )
    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()){
            capturedImageUri = uri
        }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        Log.d("TAG", "here")
        cameraLauncher.launch(uri)
    }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        AsyncImage(
            model = capturedImageUri,
            placeholder = painterResource(R.drawable.treeportal),
            error = painterResource(R.drawable.treeportal),
            contentDescription = stringResource(R.string.portal_captured_with_camera),
            modifier = Modifier
                .size(300.dp)
                .background(Color.Black)
        )
        Button(onClick = { if (latitude != null && longitude != null) { marker(latitude, longitude) }
            nav.navigate(Screens.Main.route)
                         }, enabled = true) {

        /* CameraPhoto(capturedImageUri)
        Button(
            enabled = true, // TODO: only when photo is verified to be a portal
            onClick = onRegisterPortal,
        ) {
            Text(stringResource(R.string.register_portal))
        } */
        Text(stringResource(R.string.waiting_for_photo_verification))
        CameraButton(
            onPhotoCaptured = { onPhotoCaptured(it) },
            text = stringResource(R.string.capture_photo),
            uri = capturedImageUri ?: Uri.EMPTY
        )
    }
}
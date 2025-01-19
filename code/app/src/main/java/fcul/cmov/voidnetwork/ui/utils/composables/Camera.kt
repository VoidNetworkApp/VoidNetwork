package fcul.cmov.voidnetwork.ui.utils.composables

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fcul.cmov.voidnetwork.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraPhoto(
    uri: Uri?,
    modifier: Modifier = Modifier
) {
    if (uri?.path?.isNotEmpty() == true) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.treeportal),
            error = painterResource(R.drawable.treeportal),
            contentDescription = stringResource(id = R.string.photo_captured),
            modifier = modifier
                .size(300.dp)
                .background(Color.Black),
        )
    } else {
        Text(text = stringResource(id = R.string.no_photo_captured))
    }
}

@Composable
fun CameraButton(
    uri: Uri,
    text: String,
    onPhotoCaptured: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        //capturedImageUri = uri
        if (success) {
            onPhotoCaptured(uri)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(uri)
        }
    }
    Button(onClick = {
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(uri)
        } else {
            // Request the CAMERA permission
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }, modifier = modifier) {
        Text(text = text)
    }
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = externalCacheDir ?: cacheDir
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}
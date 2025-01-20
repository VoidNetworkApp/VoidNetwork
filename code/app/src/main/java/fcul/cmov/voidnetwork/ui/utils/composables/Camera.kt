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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.CachePolicy
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
    if (uri != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .diskCachePolicy(CachePolicy.DISABLED)
                .memoryCachePolicy(CachePolicy.DISABLED) // disable cache to always show the latest photo
                .crossfade(true)
                .build(),
            contentDescription = stringResource(id = R.string.photo_captured),
            contentScale = ContentScale.Crop,
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
    text: String,
    onPhotoCaptured: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    var uri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            uri?.let { onPhotoCaptured(it) }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            uri?.let { cameraLauncher.launch(it) }
        }
    }
    Button(
        modifier = modifier,
        onClick = {
            uri = createImageUri(context)
            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                uri?.let { cameraLauncher.launch(it) }
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    ) {
        Text(text = text)
    }
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = externalCacheDir ?: cacheDir
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}

fun createImageUri(context: Context): Uri {
    val imageFile = context.createImageFile()
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}
//package fcul.cmov.voidnetwork.ui.screens.portal
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import androidx.navigation.NavController
//import coil.ImageLoader
//import coil.compose.AsyncImage
//import fcul.cmov.voidnetwork.R
//import fcul.cmov.voidnetwork.ui.navigation.Screens
//import fcul.cmov.voidnetwork.ui.utils.ScreenWithTopBar
//import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Objects
//
//@Composable
//fun RegisterPortalScreen(
//    nav: NavController,
//    viewModel: PortalViewModel,
//    latitude: Double?,
//    longitude: Double?
//) {
//    ScreenWithTopBar(
//        title = stringResource(R.string.register_portal),
//        nav = nav
//    ) { paddingValues ->
//        RegisterPortalScreenContent(
//            nav = nav,
//            modifier = Modifier.padding(paddingValues),
//            latitude = latitude,
//            longitude = longitude
//        )
//    }
//}
//
//@Composable
//fun RegisterPortalScreenContent(
//    nav: NavController,
//    modifier: Modifier = Modifier,
//    latitude: Double?,
//    longitude: Double?
//) {
////    val context = LocalContext.current
////    val file = context.createImageFile()
////    val uri = FileProvider.getUriForFile(
////        Objects.requireNonNull(context),
////        context.getPackageName() + ".provider", file
////    )
////    var capturedImageUri by remember {
////        mutableStateOf<Uri>(Uri.EMPTY)
////    }
////    val cameraLauncher =
////        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
////            capturedImageUri = uri
////        }
////
////    val permissionLauncher = rememberLauncherForActivityResult(
////        ActivityResultContracts.RequestPermission()
////    ) {
////        cameraLauncher.launch(uri)
////    }
//    var capturedImageUri by rememberSaveable { mutableStateOf(Uri.EMPTY) }
//    Column(
//        modifier = modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.SpaceEvenly,
//    ) {
//        CameraButton(onPhotoCaptured = { capturedImageUri = it })
//        CameraPhoto(capturedImageUri)
////        AsyncImage(
////            model = capturedImageUri,
////            placeholder = painterResource(R.drawable.ic_launcher_foreground),
////            error = painterResource(R.drawable.ic_launcher_foreground),
////            contentDescription = stringResource(R.string.portal_captured_with_camera),
////            modifier = Modifier
////                .size(300.dp)
////                .background(Color.Black)
////        )
////
////        Button(onClick = { if (latitude != null && longitude != null) { marker(latitude, longitude) }
////            nav.navigate(Screens.Main.route)
////                         }, enabled = true) {
////            Text(stringResource(R.string.register_portal))
////        }
////        Text(stringResource(R.string.waiting_for_photo_verification))
////        Button(onClick = { val permissionCheckResult =
////            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
////            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
////                cameraLauncher.launch(uri)
////            } else {
////                // Request a permission
////                permissionLauncher.launch(android.Manifest.permission.CAMERA)
////            }
////        }) {
////            Text(stringResource(R.string.open_camera))
////        }
//    }
//}
//
//// Auxiliary function to create file name in preparation for the photo file
//fun Context.createImageFile(): File {
//    // Create an image file name
//    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//    val imageFileName = "JPEG_" + timeStamp + "_"
//    val image = File.createTempFile(
//        imageFileName, /* prefix */
//        ".jpg", /* suffix */
//        externalCacheDir /* directory */
//    )
//    return image
//}
//
//
//@Composable
//fun CameraButton(
//    onPhotoCaptured: (Uri) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    val context = LocalContext.current
//
//    // Preparing file name
//    val file = context.createImageFile()
//    val uri = FileProvider.getUriForFile(
//        Objects.requireNonNull(context),
//        context.packageName + ".provider", file
//    )
//
//    // Reacting to Camera taking a photo
//    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
//        if (!it) return@rememberLauncherForActivityResult // permission denied
//        onPhotoCaptured(uri)
//    }
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) {
//        cameraLauncher.launch(uri) // Reacting to permission granting
//    }
//    Button(onClick = {
//        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
//
//        // Requesting permission if needed, if not, launching photo preview
//        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
//            cameraLauncher.launch(uri)
//        } else {
//            // Request a permission
//            permissionLauncher.launch(Manifest.permission.CAMERA)
//        }
//    }) {
//        Text(text = "Photo")
//    }
//}
//
//@Composable
//fun CameraPhoto(
//    capturedImageUri: Uri,
//    modifier: Modifier = Modifier
//) {
////    if (capturedImageUri.path?.isNotEmpty() == true) {
//    val imageLoader = ImageLoader.Builder(LocalContext.current)
//        .components {
//            add(ContentUriFetcher.Factory()) // Support for content:// URIs
//        }
//        .build()
//    Text(capturedImageUri.toString())
//        AsyncImage(
//            model = capturedImageUri,
//            imageLoader = imageLoader,
//            placeholder = painterResource(R.drawable.ic_launcher_foreground),
//            error = painterResource(R.drawable.ic_launcher_foreground),
//            contentDescription = "photo",
//            modifier = modifier.size(200.dp)
//        )
////    }
//}

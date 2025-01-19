package fcul.cmov.voidnetwork.ui.utils

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.CompletableDeferred
import java.io.IOException

// we assume that the portal is a tree
val portalLabels = setOf("Tree", "Trunk", "Branch", "Plant", "Forest", "Twig")

suspend fun scanPortalInCapturedImage(context: Context, uri: Uri): Boolean {
    val image = try {
        InputImage.fromFilePath(context, uri)
    } catch (e: IOException) {
        null
    } ?: return false
    val completable = CompletableDeferred<Boolean>()
    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    labeler.process(image)
        .addOnSuccessListener { labels ->
            val detectedPortal = isPortalDetected(labels, 0.6f)
            completable.complete(detectedPortal)
        }.addOnFailureListener {
            completable.complete(false)
        }
    return completable.await()
}

fun isPortalDetected(labels: List<ImageLabel>, confidenceLevel: Float): Boolean {
    return labels.any { label ->
        label.confidence >= confidenceLevel && label.text in portalLabels
    }
}

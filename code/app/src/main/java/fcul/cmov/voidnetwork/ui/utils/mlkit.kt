package fcul.cmov.voidnetwork.ui.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import fcul.cmov.voidnetwork.domain.Label
import fcul.cmov.voidnetwork.domain.Labels
import kotlinx.coroutines.CompletableDeferred
import java.io.IOException

suspend fun imageLabeling (context: Context, uri: Uri): Labels? {
    var image: InputImage?
    image = null
    try {
        image = InputImage.fromFilePath(context, uri)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    // To use default options:
    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

     // Or, to set the minimum confidence required:
//     val options = ImageLabelerOptions.Builder()
//         .setConfidenceThreshold(0.5f)
//         .build()
//     val labeler = ImageLabeling.getClient(options)

    var lableList = CompletableDeferred<Labels?>()
    if (image != null) {
        labeler.process(image)
            .addOnSuccessListener { labels ->
                // Task completed successfully
                var l = ArrayList<Label>()
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    val index = label.index
                    Log.d("MLLog", String.format("%s, %s, %s", text, confidence, index))
                    l.add(Label(text, confidence, index))
                }
                val allLabels = Labels(l, detectedTrees = detectTree(l, 0.6f))
                lableList.complete(allLabels)
                Log.d("MLLog", lableList.toString())
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
    }
    Log.d("MLLog", lableList.toString())
    return lableList.await()
}

fun detectTree(lables: ArrayList<Label>, confidenceLevel: Float): Boolean {
    val treeLables: Set<String> = setOf("Trunk", "Branch", "Plant", "Forest", "Twig")
    for (label in lables) {
        return (label.confidence >= confidenceLevel && label.text in treeLables)
    }
    return false
}

package fcul.cmov.voidnetwork.ui.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import fcul.cmov.voidnetwork.domain.Label
import fcul.cmov.voidnetwork.domain.Labels
import java.io.IOException

fun imageLabeling (context: Context, uri: Uri): Labels {
    var image: InputImage?
    image = null
    try {
        image = InputImage.fromFilePath(context, uri)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    // To use default options:
    //val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

     // Or, to set the minimum confidence required:
     val options = ImageLabelerOptions.Builder()
         .setConfidenceThreshold(0.7f)
         .build()
     val labeler = ImageLabeling.getClient(options)

    var lableList = Labels()
    if (image != null) {
        labeler.process(image)
            .addOnSuccessListener { labels ->
                // Task completed successfully
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    val index = label.index
                    Log.d("MLLog", String.format("%s, %s, %s", text, confidence, index))
                    lableList.lables.add(Label(text, confidence, index))
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
    }
    return lableList
}


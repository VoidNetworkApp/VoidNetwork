package fcul.cmov.voidnetwork.ui.utils

import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Feature.Type
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.protobuf.ByteString
import java.io.File

fun imageLabeling (imageFile: File) {
    // import com.google.cloud.vision.v1.ImageAnnotatorClient
    // import java.io.File
    val imgProto = ByteString.copyFrom(imageFile.readBytes())
    val vision = ImageAnnotatorClient.create()

    // Set up the Cloud Vision API request.
    val img = Image.newBuilder().setContent(imgProto).build()
    val feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build()
    val request = AnnotateImageRequest.newBuilder()
        .addFeatures(feat)
        .setImage(img)
        .build()

    // Call the Cloud Vision API and perform label detection on the image.
    val result = vision.batchAnnotateImages(arrayListOf(request))

    // Print the label annotations for the first response.
    result.responsesList[0].labelAnnotationsList.forEach { label ->
        println("${label.description} (${(label.score * 100).toInt()}%)")
    }
}

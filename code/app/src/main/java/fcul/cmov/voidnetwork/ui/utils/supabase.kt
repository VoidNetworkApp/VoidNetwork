package fcul.cmov.voidnetwork.ui.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import fcul.cmov.voidnetwork.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.utils.io.errors.IOException
import kotlin.jvm.Throws

val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.supabaseUrl,
    supabaseKey = BuildConfig.supabaseKey
) {
    install(Storage)
}

suspend fun uploadFile(bucketName: String, fileName: String, byteArray: ByteArray) {
    try {
        val bucket = supabase.storage[bucketName]
        bucket.upload("$fileName.jpg", byteArray)
    } catch (e: Exception) {
        Log.d("VoidNetworkErrors", e.toString())
        Log.d("VoidNetworkErrors", "Something went wrong with the upload")
    }
}

fun readFile(fileName: String, onImageUrlRetrieved:(url: String) -> Unit) {
    try {
        val bucket = supabase.storage["VoidNetwork"]
        val url = bucket.publicUrl("$fileName.jpg")
        onImageUrlRetrieved(url)
    } catch (e: Exception) {
        Log.d("VoidNetworkErrors", e.toString())
        Log.d("VoidNetworkErrors", "Something went wrong with reading")
    }
}

@Throws(IOException::class)
fun Uri.uriToByteArray(context: Context) =
    context.contentResolver.openInputStream(this)?.use { it.buffered().readBytes() }
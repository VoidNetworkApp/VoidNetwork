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

const val BUCKET_NAME = "VoidNetwork"
val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.supabaseUrl,
    supabaseKey = BuildConfig.supabaseKey
) {
    install(Storage)
}


suspend fun uploadImageToSupabase(fileName: String, byteArray: ByteArray) {
    try {
        val bucket = supabase.storage[BUCKET_NAME]
        bucket.upload("$fileName.jpg", byteArray)
    } catch (e: Exception) {
        Log.d("Supabase", e.toString())
    }
}

fun getImageUrlFromSupabase(fileName: String, onCompleted:(url: String) -> Unit) {
    try {
        val bucket = supabase.storage["VoidNetwork"]
        val url = bucket.publicUrl("$fileName.jpg")
        onCompleted(url)
    } catch (e: Exception) {
        Log.d("Supabase", e.toString())
    }
}

@Throws(IOException::class)
fun Uri.uriToByteArray(context: Context) =
    context.contentResolver.openInputStream(this)?.use { it.buffered().readBytes() }
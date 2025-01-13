package fcul.cmov.voidnetwork.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolygonAnnotationManager
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Coordinates
import fcul.cmov.voidnetwork.domain.Portal
import fcul.cmov.voidnetwork.ui.theme.BloodRed
import fcul.cmov.voidnetwork.ui.utils.MAX_DISTANCE_FROM_PORTAL
import fcul.cmov.voidnetwork.ui.utils.composables.createImageFile
import fcul.cmov.voidnetwork.ui.utils.createCirclePoints
import fcul.cmov.voidnetwork.ui.utils.getPortals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class PortalViewModel(application: Application) : AndroidViewModel(application) {

    private val portalsRef = Firebase.database.getPortals()
    var portals by mutableStateOf<List<Portal>>(emptyList())
    var capturedImageUri by mutableStateOf<Uri?>(null)

    init {
        fetchPortalsFromFirebase()
        listenToChangesFromFirebase()
    }

    fun getPortalOrNull(id: String): Portal? {
        return portals.find { it.id == id }
    }

    fun registerPortal(currentPosition: Coordinates) {
        val context = getApplication<Application>().applicationContext
        val mapView = MapView(context)
        getStreetName(mapView, currentPosition) { street ->
            if (street == null) return@getStreetName
            val newRef = portalsRef.push()
            val newId = newRef.key ?: throw IllegalStateException("Failed to generate a new key for the portal")
            val portal = Portal(newId, street, currentPosition)
            newRef.setValue(portal)
        }
    }

    fun addPortalMarker(view: MapView, portal: Portal) {
        // portal marker
        val annotationApi = view.annotations
        val circleAnnotationManager = annotationApi.createCircleAnnotationManager()
        val circleAnnotationOptions = CircleAnnotationOptions()
            .withPoint(Point.fromLngLat(portal.longitude, portal.latitude))
            .withCircleRadius(8.0)
            .withCircleColor(BloodRed.toArgb())
            .withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#ffffff")
        circleAnnotationManager.create(circleAnnotationOptions)

        // radius circle
        val polygonAnnotationManager = annotationApi.createPolygonAnnotationManager()
        val circlePoints = createCirclePoints(portal.coordinates, MAX_DISTANCE_FROM_PORTAL * 1000.0)

        val polygonAnnotationOptions = PolygonAnnotationOptions()
            .withPoints(listOf(circlePoints))
            .withFillColor(BloodRed.copy(alpha=0.2f).toArgb())
            .withFillOpacity(0.4)
        polygonAnnotationManager.create(polygonAnnotationOptions)
    }

    fun createImageUri(context: Context) {
        val imageFile = context.createImageFile()
        capturedImageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }

    private fun getStreetName(
        view: MapView,
        coordinates: Coordinates,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            val (latitude, longitude) = coordinates
            val accessToken = view.context.getString(R.string.mapbox_access_token)
            val url = "https://api.mapbox.com/geocoding/v5/mapbox.places/$longitude,$latitude.json?access_token=$accessToken"
            val client = OkHttpClient()

            try {
                val response = withContext(Dispatchers.IO) {
                    val request = Request.Builder().url(url).build()
                    client.newCall(request).execute()
                }

                response.use {
                    if (!it.isSuccessful) {
                        Log.e("Mapbox", "HTTP request failed: ${it.code}")
                        onResult(null)
                        return@launch
                    }

                    val jsonResponse = JSONObject(it.body?.string() ?: "")
                    val features = jsonResponse.getJSONArray("features")
                    if (features.length() > 0) {
                        val streetName = features.getJSONObject(0).optString("text")
                        onResult(streetName)
                    } else {
                        onResult(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("Mapbox", "Error fetching street name", e)
                onResult(null)
            }
        }
    }

    private fun fetchPortalsFromFirebase() {
        portalsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    portals = snapshot.children.mapNotNull { it.getValue(Portal::class.java) }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("PortalViewModel", "Error fetching data: ${error.message}")
            }
        })
    }

    private fun listenToChangesFromFirebase() {
        portalsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    portals = snapshot.children.mapNotNull { it.getValue(Portal::class.java) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PortalViewModel", "Error fetching data: ${error.message}")
            }
        })
    }
}

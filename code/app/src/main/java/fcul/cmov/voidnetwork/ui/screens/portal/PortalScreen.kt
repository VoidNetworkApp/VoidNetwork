package fcul.cmov.voidnetwork.ui.screens.portal


import android.content.ContentValues.TAG
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Portal
import fcul.cmov.voidnetwork.ui.navigation.Screens
import fcul.cmov.voidnetwork.ui.viewmodels.PortalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

private var view: MapView? = null

private var lat: Double = 0.0
private var lon: Double = 0.0

val database = Firebase.database
val portalsRef = database.getReference("portals")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalScreen(
    nav: NavController,
    viewModel: PortalViewModel = PortalViewModel()
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    nav.navigate(Screens.RegisterPortal.createRoute(lat, lon)) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 60.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_portal),
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End, // bottom-right
        content = { paddingValues ->

            PortalScreenContent(
                modifier = Modifier.padding(paddingValues)
            )
        }
    )

}

@Composable
fun PortalScreenContent(modifier: Modifier = Modifier) {
    val portals = remember { mutableStateOf<List<Portal>>(emptyList()) }

    LaunchedEffect(true) {
        fetchPortalsFromFirebase(portals)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(R.string.upside_down_portals))

        Box(Modifier.size(350.dp)) {
            MapboxScreen()
            Button(onClick = {
                var street = ""
                fetchStreetName(lat, lon) { streetName ->
                    if (streetName != null) {
                        street = streetName
                        Log.d("Street Name", streetName)
                        val newPortalRef = portalsRef.push()
                        val portal = Portal(street , lat, lon)
                        newPortalRef.setValue(portal)
                            .addOnSuccessListener {
                                println("Portal created successfully!")
                            }
                            .addOnFailureListener { e ->
                                println("Error creating portal: $e")
                            }
                    } else {
                        Log.d("Street Name", "Failed to fetch.")
                    }
                }}) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_portal),
                    tint = Color.White
                )
            }
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Log.d("portais", portals.value.toString())
            items(portals.value) { portal ->
                var distance by remember { mutableStateOf(0f) }
                marker(portal.lat, portal.lon)
                Button(onClick = { /*TODO*/
                }) {
                    LaunchedEffect(Unit) {
                        while (true) {
                            // Calculate the distance every second
                            distance = calculateDistanceFromUser(portal.lat, portal.lon)

                            // Wait for 1 second before updating again
                            delay(1000L)
                        }
                    }
                    Text(
                        buildString {
                            append("${portal.street} (${"%.3f".format(distance)} km)")
                            if (distance > 5) {
                                append(" - ${stringResource(R.string.out_of_range)}")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MapboxScreen() {
    val mapViewportState = rememberMapViewportState()

    portalsRef.addValueEventListener(object: ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            //val teste = snapshot.getValue<Long>()!!
            //Log.d(TAG, teste.toString())
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "Failed to read value.", error.toException())
        }

    })
    // Composable for Mapbox Map
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
    ) {
        MapEffect(Unit) { mapView ->
            // Enable location puck
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }

            view = mapView

            // Transition to follow the user's puck
            mapViewportState.transitionToFollowPuckState()

            // Add a listener for position changes
            mapView.location.addOnIndicatorPositionChangedListener { point ->
                lat = point.latitude()
                lon = point.longitude()
            }
        }
    }
}

fun marker(latitude: Double, longitude: Double) {
    Log.d("lat", lat.toString())
    Log.d("lon", lon.toString())
    // Create an instance of the Annotation API and get the CircleAnnotationManager.
    val annotationApi = view?.annotations
    val circleAnnotationManager = annotationApi?.createCircleAnnotationManager()
    // Set options for the resulting circle layer.
    val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
        // Define a geographic coordinate.
        .withPoint(Point.fromLngLat(longitude, latitude))
        // Style the circle that will be added to the map.
        .withCircleRadius(8.0)
        .withCircleColor("#ee4e8b")
        .withCircleStrokeWidth(2.0)
        .withCircleStrokeColor("#ffffff")
    // Add the resulting circle to the map.
    circleAnnotationManager?.create(circleAnnotationOptions)
}

fun calculateDistanceFromUser(
    latTarget: Double, lonTarget: Double
): Float {
    val startLocation = Location("start").apply {
        latitude = lat
        longitude = lon
    }

    val endLocation = Location("end").apply {
        latitude = latTarget
        longitude = lonTarget
    }

    return startLocation.distanceTo(endLocation) / 1000f // Distance in meters
}

//Auxiliar function to make the HTTP request for street name
fun fetchStreetName(
    latitude: Double,
    longitude: Double,
    onResult: (String?) -> Unit
) {
    // Launch Coroutine tied to lifecycle
    view?.context?.let { context ->
        (context as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope?.launch {
            val streetName = withContext(Dispatchers.IO) {
                getStreetName(latitude, longitude, context)
            }
            onResult(streetName)
        }
    }
}

fun getStreetName(
    latitude: Double,
    longitude: Double,
    context: android.content.Context
): String? {
    val accessToken = context.getString(R.string.mapbox_access_token)
    val url = "https://api.mapbox.com/geocoding/v5/mapbox.places/$longitude,$latitude.json?access_token=$accessToken"

    val client = OkHttpClient()

    return try {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("Mapbox", "HTTP request failed: ${response.code}")
                return null
            }
            val jsonResponse = JSONObject(response.body?.string() ?: "")
            val features = jsonResponse.getJSONArray("features")
            if (features.length() > 0) {
                features.getJSONObject(0).optString("text") // Street name
            } else null
        }
    } catch (e: Exception) {
        Log.e("Mapbox", "Error fetching street name", e)
        null
    }
}

fun fetchPortalsFromFirebase(portalsState: MutableState<List<Portal>>) {

    portalsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val portalsList = mutableListOf<Portal>()

                for (portalSnapshot in snapshot.children) {
                    val portal = portalSnapshot.getValue(Portal::class.java)
                    portal?.let { portalsList.add(it) }
                }

                // Update the MutableState with the fetched portals
                portalsState.value = portalsList
            }
        }

        override fun onCancelled(error: DatabaseError) {
            println("Error fetching data: ${error.message}")
        }
    })
}

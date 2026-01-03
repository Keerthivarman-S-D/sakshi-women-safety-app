package com.example.sakshi.map

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.sakshi.sos.LiveSosRepository
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun LiveSosMapScreen(
    uid: String = "demo_uid"
) {
    val context = LocalContext.current
    val repo = remember { LiveSosRepository() }

    var location by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(Unit) {
        repo.listenToSos(uid) { lat, lon ->
            if (!isInvalidLocation(lat, lon)) {
                location = GeoPoint(lat, lon)
            }
        }
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(18.0)

            // Default to India so the initial view is relevant
            controller.setCenter(GeoPoint(20.5937, 78.9629))
        }
    }

    DisposableEffect(mapView) {
        try {
            mapView.onResume()
        } catch (_: Exception) {}
        onDispose {
            try {
                mapView.onPause()
            } catch (_: Exception) {}
            try {
                mapView.onDetach()
            } catch (_: Exception) {}
        }
    }

    AndroidView(
        factory = { mapView },
        update = { map ->
            // we only manage the live marker overlay here
            // leave other overlays intact
            location?.let {
                map.controller.setCenter(it)
                // remove previous live marker(s)
                val toRemove = map.overlays.filterIsInstance<Marker>().filter { m ->
                    m.title == "SOS LIVE LOCATION"
                }
                toRemove.forEach { map.overlays.remove(it) }

                map.overlays.add(
                    Marker(map).apply {
                        position = it
                        title = "SOS LIVE LOCATION"
                    }
                )
            }
            map.invalidate()
        }
    )
}

private fun isInvalidLocation(lat: Double, lon: Double): Boolean {
    if (lat == 0.0 && lon == 0.0) return true
    if (lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) return true
    return false
}

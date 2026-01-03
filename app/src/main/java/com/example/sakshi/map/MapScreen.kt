package com.example.sakshi.map

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.sakshi.location.LocationProvider
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapScreen(onNavigateBack: () -> Unit = {}) {

    val context = LocalContext.current
    val locationProvider = remember { LocationProvider(context) }
    val routeRepo = remember { RouteRepository() }
    val overpassRepo = remember { OverpassRepository() }

    var start by remember { mutableStateOf<GeoPoint?>(null) }
    var destination by remember { mutableStateOf<GeoPoint?>(null) }
    var routePoints by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    var safetyLevel by remember { mutableStateOf<SafetyLevel?>(null) }

    // Try last known location once on composition to avoid default ocean view
    LaunchedEffect(Unit) {
        val last = locationProvider.getLastLocation()
        if (last != null && !isInvalidLocation(last.latitude, last.longitude)) {
            start = GeoPoint(last.latitude, last.longitude)
        }
    }

    // 🔴 Live location
    LaunchedEffect(Unit) {
        locationProvider.locationUpdates().collect {
            // ignore invalid 0,0 spikes
            if (!isInvalidLocation(it.latitude, it.longitude)) {
                start = GeoPoint(it.latitude, it.longitude)
            }
        }
    }

    // 🔴 Fetch route + safety ONCE per destination change
    LaunchedEffect(destination) {
        if (start == null || destination == null) return@LaunchedEffect

        val encoded =
            routeRepo.getRoute(
                start!!.latitude,
                start!!.longitude,
                destination!!.latitude,
                destination!!.longitude
            )

        if (encoded.isNotEmpty()) {
            routePoints = PolylineDecoder.decode(encoded.first())

            val tags =
                overpassRepo.fetchPOIs(
                    destination!!.latitude,
                    destination!!.longitude
                )

            val score =
                RouteSafetyEvaluator.calculateScore(tags)

            safetyLevel =
                RouteSafetyEvaluator.classify(score)
        }
    }

    // Create MapView once and manage its lifecycle explicitly
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(16.0)

            // Default center to India so initial view is relevant to the app's users
            controller.setCenter(GeoPoint(20.5937, 78.9629))

            val eventsReceiver = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    p?.let {
                        // Post state update to ensure it runs on the UI thread
                        this@apply.post { destination = it }
                    }
                    return true
                }

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    return false
                }
            }

            val eventsOverlay = MapEventsOverlay(eventsReceiver)
            overlays.add(eventsOverlay)
        }
    }

    // Ensure MapView lifecycle methods are called when this Composable is active
    DisposableEffect(mapView) {
        try {
            mapView.onResume()
        } catch (_: Exception) {
        }
        onDispose {
            try {
                mapView.onPause()
            } catch (_: Exception) {
            }
            try {
                mapView.onDetach()
            } catch (_: Exception) {
            }
        }
    }

    AndroidView(
        factory = { mapView },
        update = { map ->

            // keep MapEventsOverlay (index 0) and remove other overlays we manage
            val iterator = map.overlays.iterator()
            while (iterator.hasNext()) {
                val overlay = iterator.next()
                if (overlay !is MapEventsOverlay) {
                    iterator.remove()
                }
            }

            start?.let {
                // only center if the location is valid (non-zero)
                if (!isInvalidLocation(it.latitude, it.longitude)) {
                    map.controller.setCenter(it)
                    map.overlays.add(
                        Marker(map).apply {
                            position = it
                            title = "You"
                        }
                    )
                }
            }

            destination?.let {
                map.overlays.add(
                    Marker(map).apply {
                        position = it
                        title = "Destination"
                    }
                )
            }

            if (routePoints.isNotEmpty()) {

                val color = when (safetyLevel) {
                    SafetyLevel.SAFE -> android.graphics.Color.GREEN
                    SafetyLevel.MODERATE -> android.graphics.Color.YELLOW
                    SafetyLevel.UNSAFE -> android.graphics.Color.RED
                    else -> android.graphics.Color.BLUE
                }

                val polyline = Polyline().apply {
                    setPoints(routePoints)
                    outlinePaint.color = color
                    outlinePaint.strokeWidth = 8f
                }

                map.overlays.add(polyline)
            }

            map.invalidate()
        }
    )
}

// simple heuristic to ignore invalid coordinates like 0,0 or extreme outliers
private fun isInvalidLocation(lat: Double, lon: Double): Boolean {
    // ignore exact zero coords
    if (lat == 0.0 && lon == 0.0) return true
    // basic world bounds check
    if (lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) return true
    return false
}

package com.example.sakshi.map

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class RouteRepository {

    private val client = OkHttpClient()

    suspend fun getRoute(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): List<String> = withContext(Dispatchers.IO) {

        val url =
            "https://router.project-osrm.org/route/v1/driving/" +
                    "$startLon,$startLat;$endLon,$endLat?overview=full&geometries=polyline"

        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        val body = response.body?.string() ?: return@withContext emptyList()

        val json = JSONObject(body)
        val routes = json.getJSONArray("routes")
        val geometry = routes.getJSONObject(0).getString("geometry")

        listOf(geometry)
    }
}

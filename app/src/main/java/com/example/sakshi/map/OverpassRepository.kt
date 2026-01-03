package com.example.sakshi.map

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class OverpassRepository {

    private val client = OkHttpClient()

    suspend fun fetchPOIs(
        lat: Double,
        lon: Double,
        radius: Int = 500
    ): List<String> = withContext(Dispatchers.IO) {

        val query = """
            [out:json];
            (
              node(around:$radius,$lat,$lon)["amenity"="police"];
              node(around:$radius,$lat,$lon)["amenity"="hospital"];
              node(around:$radius,$lat,$lon)["amenity"="bar"];
              node(around:$radius,$lat,$lon)["amenity"="pub"];
              node(around:$radius,$lat,$lon)["shop"="alcohol"];
            );
            out;
        """.trimIndent()

        val url =
            "https://overpass-api.de/api/interpreter?data=${query}"

        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return@withContext emptyList()

        val json = JSONObject(body)
        val elements = json.getJSONArray("elements")

        val tags = mutableListOf<String>()
        for (i in 0 until elements.length()) {
            val obj = elements.getJSONObject(i)
            if (obj.has("tags")) {
                val tagKeys = obj.getJSONObject("tags").keys()
                while (tagKeys.hasNext()) {
                    tags.add(tagKeys.next())
                }
            }
        }
        tags
    }
}

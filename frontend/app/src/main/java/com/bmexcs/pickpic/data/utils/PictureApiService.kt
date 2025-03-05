package com.bmexcs.pickpic.data.utils

import android.util.Log
import com.bmexcs.pickpic.data.models.EventDetailsResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

private const val TAG = "PictureApiService"

class PictureApiService {
    private val baseUrl = URL("https://pick-pic-service-627889116714.northamerica-northeast2.run.app")

    private val client = OkHttpClient()

    // GET /picture/{event_id}/{image_id}/
    // Response: ByteArray
    suspend fun get(eventId: String, imageId: String, token: String): String = withContext(Dispatchers.IO) {
        val endpoint = "picture/$eventId/$imageId/"
        val url = URL("$baseUrl/$endpoint")

        Log.d(TAG, "GET: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", HttpContentType.OCTET_STREAM.toString())
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            if (response.code == 404) {
                throw NotFoundException("Endpoint does not exist")
            }

            val body = response.body?.string()
                ?: throw HttpException(response.code, "Empty response body")

            return@withContext body
        }
    }

    // PUT /picture/{event_id}/
    // Response
    // TODO()
}

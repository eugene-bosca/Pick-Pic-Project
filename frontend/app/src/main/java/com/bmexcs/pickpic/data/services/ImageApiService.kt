package com.bmexcs.pickpic.data.services

import android.util.Log
import com.bmexcs.pickpic.data.utils.Api
import com.bmexcs.pickpic.data.utils.HttpContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private const val TAG = "ImageApiService"

class ImageApiService {
    private val client = OkHttpClient()

    // GET image/{event_id}/{image_id}/
    // Response: ByteArray
    suspend fun download(
        eventId: String,
        imageId: String,
        token: String
    ) : ByteArray? = withContext(Dispatchers.IO) {
        val endpoint = "image/$eventId/$imageId/"
        val url = Api.url(endpoint)

        Log.d(TAG, "GET $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", HttpContentType.OCTET_STREAM.toString())
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            Api.handleResponseStatus(response)

            return@withContext response.body?.bytes()
        }
    }

    // PUT image/{event_id}/
    // Response: Empty
    suspend fun upload(
        eventId: String,
        imageData: ByteArray,
        token: String,
        contentType: HttpContentType = HttpContentType.PNG
    ) =
        withContext(Dispatchers.IO) {
            val endpoint = "image/$eventId/"
            val url = Api.url(endpoint)

            Log.d(TAG, "PUT $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", contentType.toString())
                .put(imageData.toRequestBody())
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)
            }
        }

}

package com.bmexcs.pickpic.data.utils.apiServices

import android.util.Log
import com.bmexcs.pickpic.data.utils.HttpContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

suspend fun downloadPicture(
    eventID: String,
    imageID: String,
    token: String
) : ByteArray? = withContext(Dispatchers.IO) {
    val endpoint = "/picture/$eventID/$imageID"

    Log.d("$TAG/Picture:downloadPicture", "making request to: $endpoint")

    val request = Request.Builder()
        .url(buildUrl(endpoint))
        .addHeader("Authorization", "Bearer $token")
        .addHeader("Content-Type", HttpContentType.OCTET_STREAM.toString())
        .get()
        .build()

    client.newCall(request).execute().use { response ->
        val responseOK = handleResponseStatus(response)

        return@withContext response.body?.bytes()
    }
}

suspend fun uploadPicture(
    eventID: String,
    image: ByteArray,
    token: String,
    contentType: HttpContentType = HttpContentType.PNG
) : String? = withContext(Dispatchers.IO) {
    val endpoint = "/picture/$eventID"

    Log.d("$TAG/Picture:uploadPicture", "making request to: $endpoint")

    val request = Request.Builder()
        .url(buildUrl(endpoint))
        .addHeader("Authorization", "Bearer $token")
        .addHeader("Content-Type", contentType.toString())
        .put(image.toRequestBody())
        .build()

    client.newCall(request).execute().use { response ->
        val responseOK = handleResponseStatus(response)

        return@withContext response.body?.string()
    }
}
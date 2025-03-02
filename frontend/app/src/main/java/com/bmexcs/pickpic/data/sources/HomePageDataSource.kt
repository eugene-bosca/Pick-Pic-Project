package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.EventItem
import javax.inject.Inject
import okhttp3.OkHttpClient
import okhttp3.Request

class HomePageDataSource @Inject constructor() {
    private val client = OkHttpClient()

    suspend fun getEvents(userId: String): List<EventItem> {
        Log.d("HomePageDataSource", "Fetching events for user: $userId")

        val url = "$BASE_URL/event-users/$userId/"

        val token = getFirebaseToken()

        // Build the request with the Bearer token.
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            Log.d("HomePageDataSource", "Response code: ${response.code}")

            validateResponse(response)

            val body = response.body?.string() ?: throw Exception("Empty response body")

            val eventsArray = parseResponseBody<Array<EventItem>>(body).toList()

            return eventsArray
        }
    }
}

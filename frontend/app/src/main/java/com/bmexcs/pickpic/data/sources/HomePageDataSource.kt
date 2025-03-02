package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.EventItem
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import okhttp3.OkHttpClient
import okhttp3.Request

class HomePageDataSource @Inject constructor() {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val baseUrl = "https://pick-pic-service-627889116714.northamerica-northeast2.run.app"

    suspend fun getEvents(userId: String): List<EventItem> {
        Log.d("HomePageDataSource", "Fetching events for user: $userId")
        val url = "$baseUrl/api/event-users/$userId/"

        // Retrieve the Firebase ID token.
        val firebaseUser = FirebaseAuth.getInstance().currentUser
            ?: throw Exception("User is not authenticated")
        val tokenResult = firebaseUser.getIdToken(false).await()
        val token = tokenResult.token ?: throw Exception("Failed to get token")

        // Build the request with the Bearer token.
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            Log.d("HomePageDataSource", "Response code: ${response.code}")
            if (!response.isSuccessful) {
                throw Exception("Error fetching events: ${response.code}")
            }
            val body = response.body?.string() ?: throw Exception("Empty response body")
            try {
                // Parse the JSON array into an Array of EventItem, then convert it to a list.
                val eventsArray = gson.fromJson(body, Array<EventItem>::class.java)
                return eventsArray.toList()
            } catch (e: JsonSyntaxException) {
                throw Exception("Error parsing JSON: ${e.message}")
            }
        }
    }
}

package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.Image
import com.bmexcs.pickpic.data.repositories.AuthRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject


class EventsDataSource @Inject constructor(
    private val authRepository: AuthRepository
) {
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun getEvents(userId: String): List<Event> {
        // TODO make request
        return listOf()
    }

    suspend fun getImagesByEventId(eventId: String): List<Image> {
        val firebaseUser = authRepository.getCurrentUser()
            ?: throw Exception("User is not authenticated")
        val tokenResult = firebaseUser.getIdToken(false).await()
        val token = tokenResult.token ?: throw Exception("Failed to get token")

        // Get List of Image Objects
        val request = Request.Builder()
            .addHeader("Authorization", "Bearer $token")
            .url("https://pick-pic-service-627889116714.northamerica-northeast2.run.app/event-contents/$eventId")
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return emptyList()
                }
                val responseBody = response.body?.string()

                if (responseBody != null) {
                    val jsonObject = JSONObject(responseBody)

                    if (jsonObject.getString("status") == "success") {
                        val imagesArray = gson.fromJson(responseBody, Array<Image>::class.java)

                        val test = imagesArray.toString()
                        Log.d("EventsDataSource", "Fetching events for user: $test")
                        return imagesArray.toList()
                    }
                }
            }
        } catch (e: JsonSyntaxException) {
            throw Exception("Error parsing JSON: ${e.message}")
        }
        return emptyList()
    }

    suspend fun addImageByEventId(image: Image) {
        val firebaseUser = authRepository.getCurrentUser()
            ?: throw Exception("User is not authenticated")
        val tokenResult = firebaseUser.getIdToken(false).await()
        val token = tokenResult.token ?: throw Exception("Failed to get token")

        val body = image.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // Get List of Image Objects
        val request = Request.Builder()
            .url("https://pick-pic-service-627889116714.northamerica-northeast2.run.app/event-contents")
            .addHeader("Authorization", "Bearer $token")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Error fetching events: ${response.code}")
            }
        }
    }

    suspend fun deleteImageByEventId(imageId: String) {
        val firebaseUser = authRepository.getCurrentUser()
            ?: throw Exception("User is not authenticated")
        val tokenResult = firebaseUser.getIdToken(false).await()
        val token = tokenResult.token ?: throw Exception("Failed to get token")

        // Delete Image
        val request = Request.Builder()
            .addHeader("Authorization", "Bearer $token")
            .url("https://localhost:8080/images/$imageId")
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Error fetching events: ${response.code}")
            }
        }
    }
}

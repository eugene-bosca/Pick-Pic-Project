package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.Image
import com.bmexcs.pickpic.data.models.Profile
import okhttp3.OkHttp
import javax.inject.Inject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class EventsDataSource @Inject constructor(
    authDataSource: AuthDataSource
) {
    private val client = OkHttpClient()

    suspend fun getEvents(userId: String): List<Event> {
        // TODO make request
        return listOf()
    }

    suspend fun getImageByEventId(eventId: Int): List<String> {
        val client = OkHttpClient()

        // Get List of Image Objects
        val request = Request.Builder()
            .url("https://localhost:8080/images/get")
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
                        val images = mutableListOf<String>()
                        val jsonArray = jsonObject.getJSONArray("message")

                        for (i in 0 until jsonArray.length()) {
                            images.add(jsonArray.getString(i))
                        }

                        return images
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return emptyList()
    }

    suspend fun addImageByEventId(image: Image) {
        val client = OkHttpClient()

        // Get List of Image Objects
        val request = Request.Builder()
            .url("https://localhost:8080/images/add")
            .build()

        try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteImageByEventId(image: Image) {
        val client = OkHttpClient()

        // Get List of Image Objects
        val request = Request.Builder()
            .url("https://localhost:8080/images/delete")
            .build()

        try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

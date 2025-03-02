package com.bmexcs.pickpic.data.sources

import android.graphics.Bitmap
import com.bmexcs.pickpic.data.serializable.SerializableUUID
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

class ImageDataSource @Inject constructor() {
    suspend fun getImageByImageId(imageId: SerializableUUID): Bitmap? {
        val client = OkHttpClient()

        // Get List of Image Objects
        val request = Request.Builder()
            .url("https://pick-pic-service-627889116714.northamerica-northeast2.run.app/api/images/$imageId")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return null
                }
                val responseBody = response.body?.string()

                if (responseBody != null) {
                    val jsonObject = JSONObject(responseBody)

                    if (jsonObject.getString("status") == "success") {
                        val jsonArray = jsonObject.getJSONArray("image")

                        return jsonArray
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
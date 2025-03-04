package com.bmexcs.pickpic.data.sources

import android.graphics.Bitmap
import com.bmexcs.pickpic.data.serializable.SerializableUUID
import com.bmexcs.pickpic.data.utils.ApiService
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    private val client = OkHttpClient()

    suspend fun getImageByImageId(imageId: String): Bitmap {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val response = ApiService.fetch("images/$imageId", Bitmap::class.java, token)

        return response
    }
}
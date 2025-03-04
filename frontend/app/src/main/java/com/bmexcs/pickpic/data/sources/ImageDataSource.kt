package com.bmexcs.pickpic.data.sources

import android.graphics.Bitmap
import com.bmexcs.pickpic.data.utils.ApiService
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    private val client = OkHttpClient()

    suspend fun getImageBinary(imageId: String): String {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val response = ApiService.get("images/$imageId", String::class.java, token)

        return response
    }

    suspend fun addImageBinary(imageByte: ByteArray?) : String {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        return ApiService.put("picture", imageByte, String::class.java, token)
    }
}
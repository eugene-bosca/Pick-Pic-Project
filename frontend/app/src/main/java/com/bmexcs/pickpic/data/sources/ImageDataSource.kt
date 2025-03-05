package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.utils.ApiService
import okhttp3.OkHttpClient
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

    suspend fun addImageBinary(eventId: String, imageByte: ByteArray?) : String {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        Log.d("EVENT", eventId)
        return ApiService.put("picture/$eventId", imageByte, String::class.java, token)
    }
}
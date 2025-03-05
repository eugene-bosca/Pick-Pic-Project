package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.EventContent
import com.bmexcs.pickpic.data.models.EventPicture
import com.bmexcs.pickpic.data.utils.ApiService
import com.bmexcs.pickpic.data.utils.HttpContentType
import com.google.gson.Gson
import kotlinx.serialization.json.internal.decodeStringToJsonTree
import okhttp3.OkHttpClient
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    private val gson = Gson()

    suspend fun getImageBinary(eventId:String, imageId: String): ByteArray? {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        return ApiService.getImage("picture/$eventId/$imageId/", ByteArray::class.java, token)
    }

    suspend fun addImageBinary(eventId: String, imageByte: ByteArray) : EventPicture {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val test =  ApiService.put("picture/$eventId/", imageByte, token, HttpContentType.JPEG)

        return ApiService.parseResponseBody(test, EventPicture::class.java)
    }
}


package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.utils.ApiService
import com.bmexcs.pickpic.data.utils.HttpContentType
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun getImageBinary(eventId:String, imageId: String): String {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        return ApiService.get("picture/$eventId/$imageId", String::class.java, token)
    }

    suspend fun addImageBinary(eventId: String, imageByte: ByteArray?) : String {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        requireNotNull(imageByte)
        return ApiService.put("picture/$eventId/", imageByte, token, HttpContentType.PNG)
    }
}
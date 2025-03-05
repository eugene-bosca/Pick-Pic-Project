package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.utils.ApiService
import com.bmexcs.pickpic.data.utils.HttpContentType
import okhttp3.OkHttpClient
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
        return ApiService.put(
            "picture/b3a54208-6622-4386-b32f-b6d10f81670e/",
            imageByte, String::class.java,
            token,
            contentType = HttpContentType.JPEG
        )
    }
}

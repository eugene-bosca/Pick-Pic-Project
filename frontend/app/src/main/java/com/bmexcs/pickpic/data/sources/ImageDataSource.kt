package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.services.ImageApiService
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {

    private val imageApi = ImageApiService()

    suspend fun getImageBinary(eventId:String, imageId: String): ByteArray? {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return imageApi.download(eventId, imageId, token)
    }

    suspend fun addImageBinary(eventId: String, imageByte: ByteArray) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        imageApi.upload(eventId, imageByte, token)
    }
}

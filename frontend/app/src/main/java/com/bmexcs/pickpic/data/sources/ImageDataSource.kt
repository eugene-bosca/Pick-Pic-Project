package com.bmexcs.pickpic.data.sources

interface ImageDataSource {
    suspend fun getImage(eventId: String, imageId: String, token: String): ByteArray?
    suspend fun addImage(eventId: String, imageData: ByteArray, token: String)
    suspend fun deleteImage(eventId: String, imageId: String, token: String)
    fun clearCache()
}
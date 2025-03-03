package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.EmptyResponse
import com.bmexcs.pickpic.data.utils.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateEventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun postEvent(body: CreateEvent): EmptyResponse = withContext(Dispatchers.IO) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val eventResponse = ApiService.post("event/", body, EmptyResponse::class.java, token)
        return@withContext eventResponse
    }
}
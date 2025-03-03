package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.EventListItem
import com.bmexcs.pickpic.data.models.EventListResponse
import com.bmexcs.pickpic.data.utils.ApiService
import com.google.android.gms.common.api.Response
import javax.inject.Inject

class CreateEventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun postEvent(body: CreateEvent) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
//        val eventResponse = ApiService.post("event/", body, token)
        return
    }
}
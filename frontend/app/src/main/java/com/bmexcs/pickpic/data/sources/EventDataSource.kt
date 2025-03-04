package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.EmptyResponse
import com.bmexcs.pickpic.data.models.ListUserEventsItem
import com.bmexcs.pickpic.data.models.ListUserEventsResponse
import com.bmexcs.pickpic.data.utils.ApiService
import javax.inject.Inject

class EventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun getEvents(userId: String): List<ListUserEventsItem> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val eventResponse = ApiService.fetch("list-users-events/$userId/", ListUserEventsResponse::class.java, token)
        return eventResponse.owned_events + eventResponse.invited_events
    }

    suspend fun postEvent(body: CreateEvent): EmptyResponse {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val eventResponse = ApiService.post("event/", body, EmptyResponse::class.java, token)
        return eventResponse
    }
}

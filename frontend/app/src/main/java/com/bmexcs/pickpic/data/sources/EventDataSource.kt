package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.models.EventListItem
import com.bmexcs.pickpic.data.models.EventListResponse
import com.bmexcs.pickpic.data.utils.ApiService
import javax.inject.Inject

class EventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun getEvents(userId: String): List<EventListItem> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val eventResponse = ApiService.fetch("list-users-events/$userId/", EventListResponse::class.java, token)
        return eventResponse.ownedEvents + eventResponse.invitedEvents
    }
}

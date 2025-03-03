package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.models.ListUserEventItem
import com.bmexcs.pickpic.data.models.ListUserEventsResponse
import com.bmexcs.pickpic.data.utils.ApiService
import javax.inject.Inject

class HomePageDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun getEvents(userId: String): List<ListUserEventItem> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val eventResponse = ApiService.fetch("list-users-events/$userId/", ListUserEventsResponse::class.java, token)
        return eventResponse.owned_events + eventResponse.invited_events
    }
}

package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.EmptyResponse
import com.bmexcs.pickpic.data.models.ListUserEventsItem
import com.bmexcs.pickpic.data.models.ListUserEventsResponse
import com.bmexcs.pickpic.data.utils.ApiService
import javax.inject.Inject

private const val TAG = "EventDataSource"

class EventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {
    suspend fun getEvents(): List<ListUserEventsItem> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEvents for $userId")

        val eventResponse = ApiService.get("list-users-events/$userId/", ListUserEventsResponse::class.java, token)
        return eventResponse.owned_events + eventResponse.invited_events
    }

    suspend fun postEvent(name: String): CreateEvent {
        val newEvent = CreateEvent(
            event_name = name,
            owner = userDataSource.getUser().user_id
        )
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "postEvent for ${userDataSource.getUser().user_id}")

        ApiService.post("event/", newEvent, EmptyResponse::class.java, token)
        return newEvent
    }
}

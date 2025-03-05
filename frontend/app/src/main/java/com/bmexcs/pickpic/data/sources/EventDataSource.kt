package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.EventDetailsResponse
import com.bmexcs.pickpic.data.models.InviteLinkResponse
import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.EmptyResponse
import com.bmexcs.pickpic.data.models.ListUserEventsItem
import com.bmexcs.pickpic.data.models.ListUserEventsResponse
import com.bmexcs.pickpic.data.utils.ApiService
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "EventDataSource"

class EventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {

    suspend fun getEvents(): List<ListUserEventsItem> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEvents for $userId")

        val eventResponse = ApiService.get("list_users_events/$userId", ListUserEventsResponse::class.java, token)
        return eventResponse.owned_events + eventResponse.invited_events
    }

    suspend fun getUserEventsPending(): List<ListUserEventsItem> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getUserEventsPending for $userId")

        val eventResponse = ApiService.getList("/users/$userId/pending_events_full", ListUserEventsItem::class.java, token)
        return eventResponse
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

    suspend fun fetchObfuscatedEventId(eventId: String): Pair<String?, String?> =
        withContext(Dispatchers.IO) {
            val token = authDataSource.getIdToken() ?: throw Exception("No user token")
            try {
                // Assuming "generate_invite_link/$eventId/" is a GET request
                val response = ApiService.get(
                    "generate_invite_link/$eventId/",
                    InviteLinkResponse::class.java,
                    token
                )

                val inviteLink = response.invite_link
                val obfuscatedId = inviteLink.substringAfterLast("/")
                val eventName = fetchEventName(eventId)
                return@withContext Pair(obfuscatedId, eventName)
            } catch (e: Exception) {
                Log.e("EventDataSource", "Error fetching obfuscated ID: ${e.message}, eventId: $eventId")
                return@withContext Pair(null, null)
            }
        }

    suspend fun fetchEventName(eventId: String): String? = withContext(Dispatchers.IO) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        try {
            // Assuming "events/$eventId/" is a GET request
            val response = ApiService.get("events/$eventId/", EventDetailsResponse::class.java, token)
            return@withContext response.event_name
        } catch (e: Exception) {
            Log.e("EventDataSource", "Error fetching event name: ${e.message}")
            return@withContext null
        }
    }

    suspend fun acceptEvent(eventId: String))
}
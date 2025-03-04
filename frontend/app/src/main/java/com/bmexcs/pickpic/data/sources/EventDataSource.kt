package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.EventDetailsResponse
import com.bmexcs.pickpic.data.models.InviteLinkResponse
import com.bmexcs.pickpic.data.utils.ApiService
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun getEvents(userId: String): List<com.bmexcs.pickpic.data.models.ListUserEventsItem> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val eventResponse = ApiService.fetch(
            "list-users-events/$userId/",
            com.bmexcs.pickpic.data.models.ListUserEventsResponse::class.java,
            token
        )
        return eventResponse.owned_events + eventResponse.invited_events
    }

    suspend fun fetchObfuscatedEventId(eventId: String): Pair<String?, String?> =
        withContext(Dispatchers.IO) {
            val token = authDataSource.getIdToken() ?: throw Exception("No user token")
            try {
                val response = ApiService.fetch(
                    "generate_invite_link/$eventId/",
                    InviteLinkResponse::class.java,
                    token
                )

                val inviteLink = response.invite_link
                val obfuscatedId = inviteLink.substringAfterLast("/")
                val eventName = fetchEventName(eventId)
                return@withContext Pair(obfuscatedId, eventName)
            } catch (e: Exception) {
                Log.e("EventDataSource", "Error fetching obfuscated ID: ${e.message}")
                return@withContext Pair(null, null)
            }
        }

    suspend fun fetchEventName(eventId: String): String? = withContext(Dispatchers.IO) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        try {
            val response = ApiService.fetch("events/$eventId/", EventDetailsResponse::class.java, token)
            return@withContext response.event_name
        } catch (e: Exception) {
            Log.e("EventDataSource", "Error fetching event name: ${e.message}")
            return@withContext null
        }
    }
}
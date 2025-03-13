package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.EventCreation
import com.bmexcs.pickpic.data.models.EventMember
import com.bmexcs.pickpic.data.models.ImageInfo
import com.bmexcs.pickpic.data.services.EventApiService
import com.bmexcs.pickpic.data.services.UserApiService
import com.bmexcs.pickpic.data.utils.NotFoundException
import javax.inject.Inject

private const val TAG = "EventDataSource"

class EventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {

    private val eventApi = EventApiService()
    private val userApi = UserApiService()

    suspend fun getEvents(): List<EventInfo> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEvents for $userId")

        val eventResponse = userApi.getEvents(userId, token)
        return eventResponse.owned_events + eventResponse.invited_events
    }

    suspend fun getEventsPending(): List<EventMember> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getUserEventsPending for $userId")

        val eventResponse = userApi.getPendingEvents(userId, token)
        return eventResponse
    }

    suspend fun createEvent(name: String): EventInfo {
        val event = EventCreation(
            user_id = userDataSource.getUser().user_id,
            event_name = name
        )
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "createEvent for ${userDataSource.getUser().user_id}")

        val newEvent = eventApi.create(event, token)
        return newEvent
    }

    suspend fun fetchObfuscatedEventId(eventId: String): Pair<String?, String?>  {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return try {
            val inviteLink = eventApi.generateInviteLink(eventId, token)
            val obfuscatedId = inviteLink.substringAfterLast("/")
            val eventName = getEventName(eventId)
            Pair(obfuscatedId, eventName)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching obfuscated ID: ${e.message}, eventId: $eventId")
            Pair(null, null)
        }
    }

    private suspend fun getEventName(eventId: String): String {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEvent for $eventId")

        return eventApi.getInfo(eventId, token).event_name
    }

    suspend fun acceptEvent(eventId: String): Boolean {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "accept for $eventId")

        val eventResponse = eventApi.acceptInvite(eventId, userId, token)
        return eventResponse
    }

    suspend fun declineEvent(eventId: String): Boolean {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "declineEvent for $eventId")

        val eventResponse = eventApi.declineInvite(eventId, userId, token)
        return eventResponse
    }

    suspend fun getImageInfo(eventId: String): List<ImageInfo> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        val eventContentList = try {
            val response = eventApi.getImageInfo(eventId, token)
            response.toMutableList()
        } catch (e: NotFoundException) {
            emptyList()
        }

        return eventContentList
    }
}

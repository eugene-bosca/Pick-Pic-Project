package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.EventCreation
import com.bmexcs.pickpic.data.models.EventUser
import com.bmexcs.pickpic.data.services.EventApiService
import com.bmexcs.pickpic.data.services.UserApiService
import javax.inject.Inject

private const val TAG = "EventDataSource"

class EventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {

    private val eventApi = EventApiService()
    private val userApi = UserApiService()

    suspend fun getEvents(): List<Event> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEvents for $userId")

        val eventResponse = userApi.events(userId, token)
        return eventResponse.owned_events + eventResponse.invited_events
    }

    suspend fun getEventsPending(): List<EventUser> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getUserEventsPending for $userId")

        val eventResponse = userApi.eventsPending(userId, token)
        return eventResponse
    }

    suspend fun createEvent(name: String): Event {
        val event = EventCreation(
            event_name = name,
            owner = userDataSource.getUser().user_id
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

    private suspend fun getEventName(eventId: String): String?  {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEvent for $eventId")

        return eventApi.read(eventId, token).event_name
    }

    suspend fun acceptEvent(eventId: String): Boolean {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "accept for $eventId")

        val eventResponse = eventApi.acceptUser(eventId, userId, token)
        return eventResponse
    }

    suspend fun declineEvent(eventId: String): Boolean {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "declineEvent for $eventId")

        val eventResponse = eventApi.removeUser(eventId, userId, token)
        return eventResponse
    }
}

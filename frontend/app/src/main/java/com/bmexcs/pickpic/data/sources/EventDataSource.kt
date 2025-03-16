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

    suspend fun fetchObfuscatedEventId(eventId: String): Pair<String?, String?> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return try {
            // This should call 'event/<uuid:event_id>/invite/link/' endpoint
            val inviteLink = eventApi.generateInviteLink(eventId, token)

            // Extract the obfuscated ID more carefully based on the format from your backend
            // Assuming the response contains the full invite link
            val obfuscatedId = inviteLink.split("/").lastOrNull()
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

        // This should call 'event/<str:event_id>/' endpoint
        return eventApi.getInfo(eventId, token).event_name
    }

    suspend fun acceptEvent(eventId: String): Boolean {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "accept for $eventId")

        return eventApi.acceptInvite(eventId, token, userId)
    }

    suspend fun declineEvent(eventId: String): Boolean {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "declineEvent for $eventId")

        return eventApi.declineInvite(eventId, token, userId)
    }

    suspend fun getImageInfo(eventId: String): List<ImageInfo> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        // This should call 'event/<str:event_id>/content/' endpoint
        val eventContentList = try {
            val response = eventApi.getImageInfo(eventId, token)
            response.toMutableList()
        } catch (e: NotFoundException) {
            emptyList()
        }

        return eventContentList
    }

    suspend fun getUnrankedImages(eventId: String, count: Long): List<ImageInfo> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventApi.getUnrankedImages(eventId, userId, count, token)
    }
}

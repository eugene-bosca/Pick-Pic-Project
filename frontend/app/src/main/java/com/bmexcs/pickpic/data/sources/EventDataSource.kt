package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.EventCreation
import com.bmexcs.pickpic.data.models.Invitation
import com.bmexcs.pickpic.data.models.ImageInfo
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.services.EventApiService
import com.bmexcs.pickpic.data.services.UserApiService
import com.bmexcs.pickpic.data.utils.NotFoundException
import com.bmexcs.pickpic.data.utils.Vote
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

    suspend fun getEventInfo(eventId: String): EventInfo {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getting event with id $eventId")

        val eventResponse = eventApi.getInfo(eventId, token)
        return eventResponse
    }

    suspend fun getEventLastModified(eventId: String): Long {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEventLastModified for event $eventId")

        val eventResponse = eventApi.lastModified(eventId, token)
        return eventResponse
    }

    suspend fun getEventOwnerInfo(ownerId: String): User {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getting user with id $ownerId")

        val user = eventApi.getEventOwner(ownerId, token)
        return user
    }

    suspend fun getEventsPending(): List<Invitation> {
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

    suspend fun deleteEvent(eventId: String) {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        userApi.deleteEvent(userId, eventId, token)
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

    // directly adds a user to an event
    suspend fun addUserToEvent(eventId: String, userId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        eventApi.addUser(eventId, userId, token)
    }

    suspend fun getAllImageInfo(eventId: String): List<ImageInfo> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        // This should call 'event/<str:event_id>/content/' endpoint
        val eventContentList = try {
            val response = eventApi.getAllImageInfo(eventId, token)
            response.toMutableList()
        } catch (e: NotFoundException) {
            emptyList()
        }

        Log.d(TAG, "${eventContentList.size} found for $eventId")
        return eventContentList
    }

    suspend fun getUnrankedImageInfo(eventId: String): List<ImageInfo> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventApi.getUnrankedImages(eventId, userId, token)
    }

    suspend fun voteOnImage(eventId: String, imageId: String, vote: Vote) {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "voteOnImage")
        eventApi.vote(eventId, imageId, userId, vote, token)
    }

    suspend fun inviteUsersFromEmail(userIds: List<String>, eventId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "Inviting users $userIds to event $eventId")

        userApi.inviteUsersFromIds(userIds, eventId, token)
    }
}

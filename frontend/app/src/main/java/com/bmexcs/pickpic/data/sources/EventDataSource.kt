package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.dtos.EventInfo
import com.bmexcs.pickpic.data.dtos.EventCreation
import com.bmexcs.pickpic.data.dtos.ImageInfo
import com.bmexcs.pickpic.data.dtos.InvitedUser
import com.bmexcs.pickpic.data.dtos.User
import com.bmexcs.pickpic.data.services.EventApiService
import com.bmexcs.pickpic.data.services.UserApiService
import com.bmexcs.pickpic.data.services.NotFoundException
import com.bmexcs.pickpic.data.models.Vote
import javax.inject.Inject

private const val TAG = "EventDataSource"

class EventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {

    private val eventApi = EventApiService()
    private val userApi = UserApiService()

    suspend fun getAllEventsMetadata(): List<EventInfo> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getAllEventsMetadata for user $userId")

        val eventResponse = userApi.getEvents(userId, token)
        return eventResponse.invited_events
    }

    suspend fun getEventMetadata(eventId: String): EventInfo {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEventMetadata for event $eventId")

        val eventResponse = eventApi.getMetadata(eventId, token)
        return eventResponse
    }

    suspend fun getEventLastModified(eventId: String): Long {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEventLastModified for event $eventId")

        val eventResponse = eventApi.lastModified(eventId, token)
        return eventResponse
    }

    suspend fun getEventOwnerMetadata(ownerId: String): User {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEventOwnerMetadata for user $ownerId")

        val user = userApi.get(ownerId, token)
        return user
    }

    suspend fun getPendingEventsMetadata(): List<EventInfo> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getPendingEventsMetadata for user $userId")

        val eventResponse = userApi.getPendingEvents(userId, token)
        return eventResponse
    }

    /**
     * Retrieves the list of users for a specific event
     * @param eventId The ID of the event
     * @return List of UserInfo objects representing the users in the event
     */
    suspend fun getEventUsersMetadata(eventId: String): List<InvitedUser> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEventUsersMetadata for event $eventId")

        return eventApi.getUsers(eventId, token)
    }

    suspend fun createEvent(name: String): EventInfo {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val event = EventCreation(
            user_id = userDataSource.getUser().user_id,
            event_name = name
        )

        Log.d(TAG, "createEvent for user ${userDataSource.getUser().user_id}")

        val newEvent = eventApi.create(event, token)
        return newEvent
    }

    suspend fun deleteEvent(eventId: String) {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "deleteEvent for event $eventId")

        userApi.deleteEvent(userId, eventId, token)
    }

    suspend fun fetchObfuscatedEventId(eventId: String): Pair<String?, String?> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "fetchObfuscatedEventId for event $eventId")

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

        Log.d(TAG, "getEventName for event $eventId")

        return eventApi.getMetadata(eventId, token).event_name
    }

    suspend fun acceptEvent(eventId: String): Boolean {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "acceptEvent for event $eventId")

        return eventApi.acceptInvite(eventId, token, userId)
    }

    suspend fun declineEvent(eventId: String): Boolean {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "declineEvent for event $eventId")

        return eventApi.declineInvite(eventId, token, userId)
    }

    suspend fun inviteUsersFromEmail(userIds: List<String>, eventId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "inviteUsersFromEmail to event $eventId for users $userIds")

        userApi.inviteUsersFromIds(userIds, eventId, token)
    }

    suspend fun addUserToEvent(eventId: String, userId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "addUserToEvent to event $eventId for user $userId")

        eventApi.addUser(eventId, userId, token)
    }

    suspend fun removeUserFromEvent(eventId: String, userId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "removeUserFromEvent from event $eventId for user $userId")

        eventApi.removeUser(eventId, userId, token)
    }

    suspend fun getAllImagesMetadata(eventId: String): List<ImageInfo> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getAllImagesMetadata for event $eventId")

        val eventContentList = try {
            val response = eventApi.getAllImageMetadata(eventId, token)
            response.toMutableList()
        } catch (e: NotFoundException) {
            emptyList()
        }

        Log.d(TAG, "${eventContentList.size} found for $eventId")
        return eventContentList
    }

    suspend fun getUnrankedImagesMetadata(eventId: String): List<ImageInfo> {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getUnrankedImagesMetadata for event $eventId")

        return eventApi.getUnrankedImageMetadata(eventId, userId, token)
    }

    suspend fun voteOnImage(eventId: String, imageId: String, vote: Vote) {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "voteOnImage for event $eventId and image id $imageId")

        eventApi.voteOnImage(eventId, imageId, userId, vote, token)
    }
}

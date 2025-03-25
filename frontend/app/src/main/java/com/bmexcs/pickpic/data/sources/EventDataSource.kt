package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.models.ImageMetadata
import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.data.services.EventApiService
import com.bmexcs.pickpic.data.services.UserApiService
import com.bmexcs.pickpic.data.services.NotFoundException
import com.bmexcs.pickpic.data.models.VoteKind
import javax.inject.Inject

private const val TAG = "EventDataSource"

class EventDataSource @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {

    private val eventApi = EventApiService()
    private val userApi = UserApiService()

    suspend fun getAllEventsMetadata(): List<EventMetadata> {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getAllEventsMetadata for user $userId")

        val events = userApi.getEvents(userId, token)
        return events.invitedTo
    }

    suspend fun getEventMetadata(eventId: String): EventMetadata {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEventMetadata for event $eventId")

        val event = eventApi.getMetadata(eventId, token)
        return event
    }

    suspend fun getEventLastModified(eventId: String): Long {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEventLastModified for event $eventId")

        val timestamp = eventApi.lastModified(eventId, token)
        return timestamp
    }

    suspend fun getEventOwnerMetadata(ownerId: String): UserMetadata {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getEventOwnerMetadata for user $ownerId")

        val user = userApi.get(ownerId, token)
        return user
    }

    suspend fun getPendingEventsMetadata(): List<EventMetadata> {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getPendingEventsMetadata for user $userId")

        val events = userApi.getPendingEvents(userId, token)
        return events
    }

    suspend fun getAcceptedUsersMetadata(eventId: String): List<UserMetadata> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getAcceptedUsersMetadata for event $eventId")

        val users = eventApi.getAcceptedUsers(eventId, token)
        return users
    }

    suspend fun getPendingUsersMetadata(eventId: String): List<UserMetadata> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getPendingUsersMetadata for event $eventId")

        val users = eventApi.getPendingUsers(eventId, token)
        return users
    }

    suspend fun createEvent(name: String): EventMetadata {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "createEvent for user ${userDataSource.getUser().id}")

        val event = eventApi.create(name, userDataSource.getUser().id, token)
        return event
    }

    suspend fun deleteEvent(eventId: String) {
        val userId = userDataSource.getUser().id
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

        val event = eventApi.getMetadata(eventId, token)
        return event.name
    }

    suspend fun acceptEvent(eventId: String): Boolean {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "acceptEvent for event $eventId")

        val result = eventApi.acceptInvite(eventId, token, userId)
        return result
    }

    suspend fun declineEvent(eventId: String): Boolean {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "declineEvent for event $eventId")

        val result = eventApi.declineInvite(eventId, token, userId)
        return result
    }

    suspend fun inviteUsersFromEmail(userEmails: List<String>, eventId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "inviteUsersFromEmail to event $eventId for users $userEmails")

        userApi.directInviteUsersByEmail(userEmails, eventId, token)
    }

    suspend fun acceptDirectInvitation(eventId: String, userId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "acceptDirectInvitation for event $eventId for user $userId")

        eventApi.acceptDirectInvitation(eventId, userId, token)
    }

    suspend fun removeUserFromEvent(eventId: String, userId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "removeUserFromEvent from event $eventId for user $userId")

        eventApi.removeUser(eventId, userId, token)
    }

    suspend fun getAllImagesMetadata(eventId: String): List<ImageMetadata> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getAllImagesMetadata for event $eventId")

        val imageMetadataList = try {
            val result = eventApi.getAllImageMetadata(eventId, token)
            result.toMutableList()
        } catch (e: NotFoundException) {
            emptyList()
        }

        Log.d(TAG, "${imageMetadataList.size} found for $eventId")
        return imageMetadataList
    }

    suspend fun getUnrankedImagesMetadata(eventId: String): List<ImageMetadata> {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getUnrankedImagesMetadata for event $eventId")

        return eventApi.getUnrankedImageMetadata(eventId, userId, token)
    }

    suspend fun voteOnImage(eventId: String, imageId: String, voteKind: VoteKind) {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "voteOnImage for event $eventId and image id $imageId")

        eventApi.voteOnImage(eventId, imageId, userId, voteKind, token)
    }
}

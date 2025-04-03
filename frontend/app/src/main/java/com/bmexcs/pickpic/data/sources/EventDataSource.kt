package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.models.ImageMetadata
import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.data.services.EventApiService
import com.bmexcs.pickpic.data.services.UserApiService
import com.bmexcs.pickpic.data.models.VoteKind

private const val TAG = "EventDataSource"

class EventDataSource {
    private val eventApi = EventApiService()
    private val userApi = UserApiService()

    suspend fun getAllEventsMetadata(userId: String, token: String): List<EventMetadata> {
        Log.d(TAG, "getAllEventsMetadata for user $userId")

        val events = userApi.getEvents(userId, token)
        return events.invitedTo
    }

    suspend fun getEventMetadata(eventId: String, token: String): EventMetadata {
        Log.d(TAG, "getEventMetadata for event $eventId")

        val event = eventApi.getMetadata(eventId, token)
        return event
    }

    suspend fun getEventLastModified(eventId: String, token: String): Long {
        Log.d(TAG, "getEventLastModified for event $eventId")

        val timestamp = eventApi.lastModified(eventId, token)
        return timestamp
    }

    suspend fun getEventOwnerMetadata(ownerId: String, token: String): UserMetadata {
        Log.d(TAG, "getEventOwnerMetadata for user $ownerId")

        val user = userApi.get(ownerId, token)
        return user
    }

    suspend fun getPendingEventsMetadata(userId: String, token: String): List<EventMetadata> {
        Log.d(TAG, "getPendingEventsMetadata for user $userId")

        val events = userApi.getPendingEvents(userId, token)
        return events
    }

    suspend fun getAcceptedUsersMetadata(eventId: String, token: String): List<UserMetadata> {
        Log.d(TAG, "getAcceptedUsersMetadata for event $eventId")

        val users = eventApi.getJoinedUsers(eventId, token)
        return users
    }

    suspend fun getPendingUsersMetadata(eventId: String, token: String): List<UserMetadata> {
        Log.d(TAG, "getPendingUsersMetadata for event $eventId")

        val users = eventApi.getPendingUsers(eventId, token)
        return users
    }

    suspend fun createEvent(name: String, userId: String, token: String): EventMetadata {
        Log.d(TAG, "createEvent for user $userId")

        val event = eventApi.create(name, userId, token)
        return event
    }

    suspend fun deleteEvent(eventId: String, userId: String, token: String) {
        Log.d(TAG, "deleteEvent for event $eventId")

        userApi.deleteEvent(userId, eventId, token)
    }

    suspend fun generateInviteLink(eventId: String, token: String): String {
        Log.d(TAG, "generateInviteLink for event $eventId")

        val inviteLink = eventApi.generateInviteLink(eventId, token)
        return inviteLink
    }

    suspend fun acceptEvent(eventId: String, userId: String, token: String): Boolean {
        Log.d(TAG, "acceptEvent for event $eventId")

        val result = eventApi.acceptInvite(eventId, token, userId)
        return result
    }

    suspend fun declineEvent(eventId: String, userId: String, token: String): Boolean {
        Log.d(TAG, "declineEvent for event $eventId")

        val result = eventApi.declineInvite(eventId, token, userId)
        return result
    }

    suspend fun inviteUsersByEmails(userEmails: List<String>, eventId: String, token: String) {
        Log.d(TAG, "inviteUsersFromEmail to event $eventId for users $userEmails")

        eventApi.inviteUsersByEmails(eventId, userEmails, token)
    }

    suspend fun joinEvent(eventId: String, token: String) {
        Log.d(TAG, "joinEvent for event $eventId")

        eventApi.join(eventId, token)
    }

    suspend fun removeUserFromEvent(eventId: String, userId: String, token: String) {
        Log.d(TAG, "removeUserFromEvent from event $eventId for user $userId")

        eventApi.removeUser(eventId, userId, token)
    }

    suspend fun getAllImagesMetadata(eventId: String, token: String): List<ImageMetadata> {
        Log.d(TAG, "getAllImagesMetadata for event $eventId")

        val result = eventApi.getAllImageMetadata(eventId, token)
        return result
    }

    suspend fun getUnrankedImagesMetadata(eventId: String, userId: String, token: String): List<ImageMetadata> {
        Log.d(TAG, "getUnrankedImagesMetadata for event $eventId")

        return eventApi.getUnrankedImageMetadata(eventId, userId, token)
    }

    suspend fun voteOnImage(eventId: String, imageId: String, voteKind: VoteKind, userId: String, token: String) {
        Log.d(TAG, "voteOnImage for event $eventId and image id $imageId")

        eventApi.voteOnImage(eventId, imageId, userId, voteKind, token)
    }
}

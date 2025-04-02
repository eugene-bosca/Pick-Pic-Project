package com.bmexcs.pickpic.data.repositories

import android.util.Log
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.models.ImageMetadata
import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.data.sources.EventDataSource
import com.bmexcs.pickpic.data.models.VoteKind
import com.bmexcs.pickpic.data.services.NotFoundException
import com.bmexcs.pickpic.data.sources.AuthDataSource
import com.bmexcs.pickpic.data.sources.UserDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "EventRepository"

@Singleton
class EventRepository @Inject constructor(
    private val eventDataSource: EventDataSource,
    private val userDataSource: UserDataSource,
    private val authDataSource: AuthDataSource
) {
    private val _eventInfo = MutableStateFlow(EventMetadata())
    val event = _eventInfo

    private var timestamp: Long? = null

    suspend fun isUpdated(eventId: String): Boolean {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val lastModified =  eventDataSource.getEventLastModified(eventId, token)

        if (timestamp == null) {
            timestamp = lastModified
            return false
        }

        return if (lastModified > timestamp!!) {
            timestamp = lastModified
            true
        } else {
            false
        }
    }

    fun setCurrentEvent(eventInfo: EventMetadata) {
        event.value = eventInfo
    }

    suspend fun getAllEventsMetadata(): List<EventMetadata> {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventDataSource.getAllEventsMetadata(userId, token)
    }

    suspend fun getEventMetadata(eventId: String): EventMetadata {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventDataSource.getEventMetadata(eventId, token)
    }

    suspend fun getEventOwnerMetadata(ownerId: String): UserMetadata {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventDataSource.getEventOwnerMetadata(ownerId, token)
    }

    suspend fun getAcceptedUsersMetadata(eventId: String): List<UserMetadata> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventDataSource.getAcceptedUsersMetadata(eventId, token)
    }

    suspend fun getPendingUsersMetadata(eventId: String): List<UserMetadata> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventDataSource.getPendingUsersMetadata(eventId, token)
    }

    suspend fun getAllImagesMetadata(eventId: String): List<ImageMetadata> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        val imageMetadataList = try {
            val result = eventDataSource.getAllImagesMetadata(eventId, token)
            result.toMutableList()
        } catch (e: NotFoundException) {
            emptyList()
        }

        Log.d(TAG, "${imageMetadataList.size} found for $eventId")
        return imageMetadataList
    }

    suspend fun getUnrankedImagesMetadata(): List<ImageMetadata> {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventDataSource.getUnrankedImagesMetadata(event.value.id, userId, token)
    }

    suspend fun voteOnImage(imageId: String, voteKind: VoteKind) {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventDataSource.voteOnImage(event.value.id, imageId, voteKind, userId, token)
    }

    suspend fun createEvent(name: String): EventMetadata {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventDataSource.createEvent(name, userId, token)
    }

    suspend fun getPendingEventsMetadata(): List<EventMetadata> {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventDataSource.getPendingEventsMetadata(userId, token)
    }

    suspend fun deleteEvent(id: String) {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventDataSource.deleteEvent(id, userId, token)
    }

    suspend fun fetchObfuscatedEventId(eventId: String): Pair<String?, String?> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return try {
            // This should call 'event/<uuid:event_id>/invite/link/' endpoint
            val inviteLink = eventDataSource.generateInviteLink(eventId, token)

            // Extract the obfuscated ID more carefully based on the format from your backend
            // Assuming the response contains the full invite link
            val obfuscatedId = inviteLink.split("/").lastOrNull()
            val event = eventDataSource.getEventMetadata(eventId, token)
            Pair(obfuscatedId, event.name)

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching obfuscated ID: ${e.message}, eventId: $eventId")
            Pair(null, null)
        }
    }

    suspend fun acceptEvent(eventId: String) {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventDataSource.acceptEvent(eventId, userId, token)
    }

    suspend fun declineEvent(eventId: String) {
        val userId = userDataSource.getUser().id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventDataSource.declineEvent(eventId, userId, token)
    }

    suspend fun inviteUsersFromEmail(userEmails: List<String>, eventId: String)  {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventDataSource.inviteUsersByEmails(userEmails, eventId, token)
    }

    suspend fun joinEvent(eventId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventDataSource.joinEvent(eventId, token)
    }

    suspend fun removeUserFromEvent(eventId: String, userId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventDataSource.removeUserFromEvent(eventId, userId, token)
    }
}

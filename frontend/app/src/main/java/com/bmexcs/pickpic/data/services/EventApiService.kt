package com.bmexcs.pickpic.data.services

import android.util.Log
import com.bmexcs.pickpic.data.dtos.Event
import com.bmexcs.pickpic.data.dtos.EventCreation
import com.bmexcs.pickpic.data.dtos.EventLastModified
import com.bmexcs.pickpic.data.dtos.Image
import com.bmexcs.pickpic.data.dtos.ImageVote
import com.bmexcs.pickpic.data.dtos.User
import com.bmexcs.pickpic.data.dtos.UserEmails
import com.bmexcs.pickpic.data.dtos.UserEventInviteLink
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.models.ImageMetadata
import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.data.models.VoteKind
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

private const val TAG = "EventApiService"

class EventApiService {
    private val client = OkHttpClient()
    private val gson = Gson()

    /**
     * Retrieves metadata about the specified event.
     *
     * **Endpoint**: `GET /event/{event_id}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `dtos.EventInfo`
     *
     * **Return Type**: `models.EventMetadata`
     */
    suspend fun getMetadata(eventId: String, token: String): EventMetadata =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/"
            val url = Api.url(endpoint)

            Log.d(TAG, "GET: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<Event>() {}.type
                val result: Event = gson.fromJson(body, resultType)

                return@withContext EventMetadata(result)
            }
        }

    /**
     * Retrieves metadata for all images associated with the specified event.
     *
     * **Endpoint**: `GET /event/{event_id}/content/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response Body**: `List<models.ImageInfo>`
     *
     * **Return Type**: `List<models.ImageMetadata>`
     */
    suspend fun getAllImageMetadata(eventId: String, token: String): List<ImageMetadata> =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/content/"
            val url = Api.url(endpoint)

            Log.d(TAG, "GET: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<List<Image>>() {}.type
                val result: List<Image> = gson.fromJson(body, resultType)

                return@withContext result.map { ImageMetadata(it) }
            }
        }

    /**
     * Uploads an image to the specified event.
     *
     * **Endpoint**: `PUT /event/{event_id}/image/`
     *
     * **Request Body**: `ByteArray`
     *
     * **Request Content-Type**: PNG or JPEG
     *
     * **Response**: Empty
     *
     * **Return Type**: None
     */
    suspend fun uploadImage(
        eventId: String,
        imageData: ByteArray,
        token: String,
    ) = withContext(Dispatchers.IO) {
        val endpoint = "event/$eventId/image/"
        val url = Api.url(endpoint)

        Log.d(TAG, "PUT $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "image/png")
            .put(imageData.toRequestBody("image/png".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            Api.handleResponseStatus(response)
        }
    }

    /**
     * Downloads the specified image from an event.
     *
     * **Endpoint**: `GET /event/{event_id}/image/{image_id}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `ByteArray`
     *
     * **Return Type**: `ByteArray?`
     */
    suspend fun downloadImage(
        eventId: String,
        imageId: String,
        token: String
    ) : ByteArray? = withContext(Dispatchers.IO) {
        val endpoint = "event/$eventId/image/$imageId/"
        val url = Api.url(endpoint)

        Log.d(TAG, "GET $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            Api.handleResponseStatus(response)

            return@withContext response.body?.bytes()
        }
    }

    /**
     * Deletes the specified image from an event.
     *
     * **Endpoint**: `DELETE /event/{event_id}/image/{image_id}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: Empty
     *
     * **Return Type**: None
     */
    suspend fun deleteImage(
        eventId: String,
        imageId: String,
        token: String
    ) = withContext(Dispatchers.IO) {
        val endpoint = "event/$eventId/image/$imageId/"
        val url = Api.url(endpoint)

        Log.d(TAG, "DELETE $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            Api.handleResponseStatus(response)
        }
    }

    /**
     * Changes the ranking of the specified image by the specified vote value.
     *
     * **Endpoint**: `PUT /event/{event_id}/image/{image_id}/vote/`
     *
     * **Request Body**: models.ImageVote
     *
     * **Request Content-Type**: JSON
     *
     * **Response**: Empty
     *
     * **Return Type**: None
     */
    suspend fun voteOnImage(eventId: String, imageId: String, userId: String, voteKind: VoteKind, token: String) =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/image/$imageId/vote/"
            val url = Api.url(endpoint)

            Log.d(TAG, "PUT: $url")

            val imageVote = ImageVote(
                user_id = userId,
                vote = voteKind.toString()
            )

            val requestBody = gson.toJson(imageVote)
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)
            }
        }

    /**
     * Retrieves metadata for all images that the specified user has not yet ranked.
     *
     * **Endpoint**: `GET /event/{event_id}/image/user/{user_id}/unranked/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: List<dtos.ImageInfo>
     *
     * **Return Type**: List<models.ImageMetadata>
     */
    suspend fun getUnrankedImageMetadata(eventId: String, userId: String, token: String)
            : List<ImageMetadata> = withContext(Dispatchers.IO) {
        val endpoint = "event/$eventId/image/user/$userId/unranked/"
        val url = Api.url(endpoint)

        Log.d(TAG, "GET: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            Api.handleResponseStatus(response)

            val body = response.body?.string()
                ?: throw HttpException(response.code, "Empty response body")

            val resultType = object : TypeToken<List<Image>>() {}.type
            val result: List<Image> = gson.fromJson(body, resultType)

            return@withContext result.map { ImageMetadata(it) }
        }
    }

    /**
     * Accepts the invitation for the specified event.
     *
     * **Endpoint**: `POST /event/{event_id}/invitation/accept/`
     *
     * **Request Body**: JSON with `user_id`
     *
     * **Request Content-Type**: JSON
     *
     * **Response**: Empty or Error
     *
     * **Return Type**: Boolean
     */
    suspend fun acceptInvite(eventId: String, token: String, userId: String): Boolean =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/invitation/${"accept"}/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            val jsonBody = JSONObject().apply {
                put("user_id", userId)
            }.toString()

            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post(requestBody)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    Api.handleResponseStatus(response)
                    return@withContext true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error accepting invite: ${e.message}")
                return@withContext false
            }
        }

    /**
     * Declines the invitation for the specified event.
     *
     * **Endpoint**: `POST /event/{event_id}/invitation/decline/`
     *
     * **Request Body**: JSON with `user_id`
     *
     * **Request Content-Type**: JSON
     *
     * **Response**: Empty or Error
     *
     * **Return Type**: Boolean
     */
    suspend fun declineInvite(eventId: String, token: String, userId: String): Boolean =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/invitation/${"decline"}/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            val jsonBody = JSONObject().apply {
                put("user_id", userId)
            }.toString()

            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post(requestBody)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    Api.handleResponseStatus(response)
                    return@withContext true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error declining invite: ${e.message}")
                return@withContext false
            }
        }

    /**
     * Invites a list of users by their emails.
     *
     * **Endpoint**: `POST /event/{event_id}/invite/email/
     *
     * **Request Body**: `dtos.UserEmails`
     *
     * **Request Content-Type**: JSON
     *
     * **Response**: Empty
     *
     * **Return Type**: None
     *
     */
    suspend fun inviteUsersByEmails(eventId: String, emails: List<String>, token: String) =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/invite/email/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            val requestBody = gson.toJson(UserEmails(emails))
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)
            }
        }

    /**
     * Retrieves the time when the event was last modified.
     *
     * **Endpoint**: `GET /event/{event_id}/last_modified/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `dtos.EventLastModified`
     *
     * **Return Type**: `Long`
     */
    suspend fun lastModified(eventId: String, token: String): Long =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/last_modified/"
            val url = Api.url(endpoint)

            Log.d(TAG, "GET: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<EventLastModified>() {}.type
                val result: EventLastModified = gson.fromJson(body, resultType)

                val formatter = SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", Locale.getDefault())
                val timestamp = formatter.parse(result.last_modified)?.time
                    ?: throw Exception("Failed to parse date string")

                return@withContext timestamp
            }
        }

    /**
     * Retrieves metadata about all users who are pending for an event.
     *
     * **Endpoint**: `GET /event/{event_id}/pending_users/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `List<dtos.User>`
     *
     * **Return Type** `List<models.UserMetadata>`
     */
    suspend fun getPendingUsers(eventId: String, token: String): List<UserMetadata> =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/pending_users/"
            val url = Api.url(endpoint)

            Log.d(TAG, "GET: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<List<User>>() {}.type
                val result: List<User> = gson.fromJson(body, resultType)

                return@withContext result.map { UserMetadata(it) }
            }
        }

    /**
     * Removes a user from an event.
     *
     * **Endpoint**: `DELETE /event/{event_id}/user/{user}`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: NONE
     *
     * **Response**: Empty
     *
     * **Return Type**: None
     */
    suspend fun removeUser(eventId: String, userId: String, token: String) =
        withContext(Dispatchers.IO) {
            val endpoint = "event/${eventId}/user/$userId/"
            val url = Api.url(endpoint)

            Log.d(TAG, "Delete: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .delete()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)
            }
        }

    /**
     * Retrieves metadata about all users who have joined an event.
     *
     * **Endpoint**: `GET /event/{event_id}/users/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `List<dtos.User>`
     *
     * **Return Type** `List<models.UserMetadata>`
     */
    suspend fun getJoinedUsers(eventId: String, token: String): List<UserMetadata> =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/users/"
            val url = Api.url(endpoint)

            Log.d(TAG, "GET: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<List<User>>() {}.type
                val result: List<User> = gson.fromJson(body, resultType)

                return@withContext result.map { UserMetadata(it) }
            }
        }

    /**
     * Creates a new event with the specified user as the owner.
     *
     * **Endpoint**: `POST /event/create/`
     *
     * **Request Body**: `models.EventCreation`
     *
     * **Request Content-Type**: JSON
     *
     * **Response**: `dtos.EventInfo`
     *
     * **Return Type**: `models.EventMetadata`
     */
    suspend fun create(eventName: String, userId: String, token: String): EventMetadata =
        withContext(Dispatchers.IO) {
            val endpoint = "event/create/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            val eventCreation = EventCreation(
                user_id = userId,
                event_name = eventName
            )

            val requestBody = gson.toJson(eventCreation)
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    Api.handleResponseStatus(response)

                    val body = response.body?.string()
                        ?: throw HttpException(response.code, "Empty response body")

                    val resultType = object : TypeToken<Event>() {}.type
                    val result: Event = gson.fromJson(body, resultType)

                    return@withContext EventMetadata(result)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating event: ${e.message}")
                return@withContext EventMetadata()
            }
        }

    /**
     * Generates an obfuscated invitation link for the specified event.
     *
     * **Endpoint**: `GET /event/invite/generate/{event_id}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `dtos.UserEventInviteLink`
     *
     * **Return Type**: `String`
     */
    suspend fun generateInviteLink(eventId: String, token: String): String =
        withContext(Dispatchers.IO) {
            val endpoint = "event/invite/generate/$eventId/"
            val url = Api.url(endpoint)

            Log.d(TAG, "GET: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                Log.d(TAG, body)

                val resultType = object : TypeToken<UserEventInviteLink>() {}.type
                val result: UserEventInviteLink = gson.fromJson(body, resultType)

                return@withContext result.invite_link
            }
        }

    /**
     * Immediately joins a user to event.
     *
     * **Endpoint**: `POST /event/join/{event_id}/`
     *
     * **Request Body**: User to be added is identified by bearer token
     *
     * **Request Content-Type**: None
     *
     * **Response**: Empty
     *
     * **Return Type**: None
     */
    suspend fun join(eventId: String, token: String) =withContext(Dispatchers.IO) {
        val endpoint = "event/join/${eventId}/"
        val url = Api.url(endpoint)

        Log.d(TAG, "POST: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .post(EMPTY_REQUEST)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)
                return@withContext true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error joining event: ${e.message}")
            return@withContext false
        }
    }
}

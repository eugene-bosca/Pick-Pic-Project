package com.bmexcs.pickpic.data.services

import android.util.Log
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.ImageInfo
import com.bmexcs.pickpic.data.models.EventCreation
import com.bmexcs.pickpic.data.models.EventId
import com.bmexcs.pickpic.data.models.EventLastModified
import com.bmexcs.pickpic.data.models.ImageCount
import com.bmexcs.pickpic.data.models.ImageVote
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.models.UserEventInviteLink
import com.bmexcs.pickpic.data.models.UserInfo
import com.bmexcs.pickpic.data.utils.Api
import com.bmexcs.pickpic.data.utils.HttpException
import com.bmexcs.pickpic.data.utils.Vote
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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
     * **Response**: `models.EventInfo`
     */
    suspend fun getInfo(eventId: String, token: String): EventInfo =
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

                val resultType = object : TypeToken<EventInfo>() {}.type
                val result: EventInfo = gson.fromJson(body, resultType)

                return@withContext result
            }
        }

    /**
     * TODO: remove
     * Retrieves the user data for the owner of an event.
     *
     * **Endpoint**: `GET /user/{userId}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `models.User`
     */
    suspend fun getEventOwner(userId: String, token: String): User =
        withContext(Dispatchers.IO) {
            val endpoint = "user/$userId/"
            val url = Api.url(endpoint)

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<User>() {}.type
                val result: User = gson.fromJson(body, resultType)

                return@withContext result
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
     */
    suspend fun getImageInfo(eventId: String, token: String): List<ImageInfo> =
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

                val resultType = object : TypeToken<List<ImageInfo>>() {}.type
                val result: List<ImageInfo> = gson.fromJson(body, resultType)

                return@withContext result
            }
        }

    /**
     * Uploads an image for the specified event.
     *
     * **Endpoint**: `PUT /event/{event_id}/image/user/{user_id}/create/`
     *
     * **Request Body**: `ByteArray`
     *
     * **Request Content-Type**: PNG or JPEG
     *
     * **Response**: Empty
     */
    suspend fun uploadImage(
        eventId: String,
        userId: String,
        imageData: ByteArray,
        token: String,
        contentType: String = "image/png"
    ) = withContext(Dispatchers.IO) {
        val endpoint = "event/$eventId/image/user/$userId/create/"
        val url = Api.url(endpoint)

        Log.d(TAG, "PUT $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", contentType)
            .put(imageData.toRequestBody())
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
     * **Request Body**: `models.ImageVote`
     *
     * **Request Content-Type**: JSON
     *
     * **Response**: Empty
     */
    suspend fun vote(eventId: String, imageId: String, userId: String, vote: Vote, token: String) =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/image/$imageId/vote/"
            val url = Api.url(endpoint)

            Log.d(TAG, "PUT: $url")

            val imageVote = ImageVote(
                user_id = userId,
                vote = vote.toString()
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
     * Retrieves the number of images added to an event.
     *
     * **Endpoint**: `GET /event/{event_id}/image/count/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `models.ImageCount` as `Long`
     */
    suspend fun getImageCount(eventId: String, token: String): Long =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/image/count/"
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

                val resultType = object : TypeToken<ImageCount>() {}.type
                val result: ImageCount = gson.fromJson(body, resultType)

                return@withContext result.image_count
            }
        }

    /**
     * Retrieves the highest-scored image for the specified event.
     *
     * **Endpoint**: `GET /event/{event_id}/image/highest_score/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `ByteArray`
     */
    suspend fun getHighestScoredImage(eventId: String, token: String): ByteArray =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/image/highest_score/"
            val url = Api.url(endpoint)

            Log.d(TAG, "GET: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body
                    ?: throw HttpException(response.code, "Empty response body")

                return@withContext body.bytes()
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
     */
    suspend fun acceptInvite(eventId: String, token: String, userId: String): Boolean =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/invitation/accept/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            // Create a JSON body with the user_id
            val jsonBody = JSONObject().apply {
                put("user_id", userId)
            }.toString()

            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post(requestBody)  // Use POST with the request body
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
     */
    suspend fun declineInvite(eventId: String, token: String, userId: String): Boolean =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/invitation/decline/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            // Create a JSON body with the user_id
            val jsonBody = JSONObject().apply {
                put("user_id", userId)
            }.toString()

            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post(requestBody)  // Use POST with the request body
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
     * Generates an obfuscated invitation link for the specified event.
     *
     * **Endpoint**: `GET /event/invite/generate/{event_id}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `models.UserEventInviteLink` as `String`
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

            Log.d(TAG, "Request: $request")

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<UserEventInviteLink>() {}.type
                val result: UserEventInviteLink = gson.fromJson(body, resultType)

                Log.d(TAG, "Generated invite link: ${result.invite_link}")

                return@withContext result.invite_link
            }
        }

    /**
     * Retrieves all ImageInfos that the specified user has not yet ranked.
     *
     * **Endpoint**: `GET /event/{event_id}/image/user/{user_id}/unranked/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: List<models.ImageInfo>
     *
     */
    suspend fun getUnrankedImages(eventId: String, userId: String, token: String)
        : List<ImageInfo> = withContext(Dispatchers.IO) {
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

                val resultType = object : TypeToken<List<ImageInfo>>() {}.type
                val result: List<ImageInfo> = gson.fromJson(body, resultType)

                return@withContext result
            }
        }

    /**
     * Retrieves when the event was last modified.
     *
     * **Endpoint**: `GET /event/{event_id}/last_modified/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `models.EventLastModified` as `Long`
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
     * Retrieves metadata about the users in an event.
     *
     * **Endpoint**: `GET /event/{event_id}/users/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `List<models.UserInfo>`
     */
    suspend fun getUsers(eventId: String, token: String): List<UserInfo> =
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

                val resultType = object : TypeToken<List<UserInfo>>() {}.type
                val result: List<UserInfo> = gson.fromJson(body, resultType)

                return@withContext result
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
     * **Response**: `models.EventInfo`
     */
    suspend fun create(eventCreation: EventCreation, token: String): EventInfo =
        withContext(Dispatchers.IO) {
            val endpoint = "event/create/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

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

                    val resultType = object : TypeToken<EventInfo>() {}.type
                    val result: EventInfo = gson.fromJson(body, resultType)

                    return@withContext result
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating event: ${e.message}")
                return@withContext EventInfo()
            }
        }

    /**
     * Resolves an obfuscated invite link to get the event ID.
     *
     * **Endpoint**: `GET /event/invite/link/decode/{invite_link}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `models.EventId` as `String`
     */
    suspend fun resolveInviteLink(inviteLink: String, token: String): String =
        withContext(Dispatchers.IO) {
            val endpoint = "event/invite/link/decode/$inviteLink/" // Removed leading slash
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

                val resultType = object : TypeToken<EventId>() {}.type
                val result: EventId = gson.fromJson(body, resultType)

                return@withContext result.event_id
            }
        }
}

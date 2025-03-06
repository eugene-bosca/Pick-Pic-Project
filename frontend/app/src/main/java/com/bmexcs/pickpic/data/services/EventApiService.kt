package com.bmexcs.pickpic.data.services

import android.util.Log
import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.EventCreation
import com.bmexcs.pickpic.data.models.EventDetailsResponse
import com.bmexcs.pickpic.data.models.EventId
import com.bmexcs.pickpic.data.models.EventInvite
import com.bmexcs.pickpic.data.models.EventUser
import com.bmexcs.pickpic.data.models.ImageCount
import com.bmexcs.pickpic.data.models.UserEventInviteLink
import com.bmexcs.pickpic.data.utils.Api
import com.bmexcs.pickpic.data.utils.HttpContentType
import com.bmexcs.pickpic.data.utils.HttpException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private const val TAG = "EventApiService"

class EventApiService {
    private val client = OkHttpClient()
    private val gson = Gson()

    // POST event/create/
    // Response: Event
    suspend fun create(eventCreation: EventCreation, token: String): Event =
        withContext(Dispatchers.IO) {
        val endpoint = "event/create/"
        val url = Api.url(endpoint)

        Log.d(TAG, "POST: $url")

        val requestBody = gson.toJson(eventCreation)
            .toRequestBody(HttpContentType.JSON.toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", HttpContentType.JSON.toString())
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            Api.handleResponseStatus(response)

            val body = response.body?.string()
                ?: throw HttpException(response.code, "Empty response body")

            val resultType = object : TypeToken<Event>() {}.type
            val result: Event = gson.fromJson(body, resultType)

            return@withContext result
        }
    }

    // GET /event/{event_id}/highest_scored_image/
    // Response: ByteArray
    suspend fun highestScoredImage(eventId: String, token: String): ByteArray? =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/highest_scored_image/"
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

                return@withContext response.body?.bytes()
            }
        }

    // POST /event/{event_id}/invite/
    // Response: Empty
    suspend fun invite(eventId: String, userId: String, token: String): EventUser =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/invite/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            val eventInvite = EventInvite(
                event_id = eventId,
                user_id = userId
            )

            val requestBody = gson.toJson(eventInvite)
                .toRequestBody(HttpContentType.JSON.toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", HttpContentType.JSON.toString())
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<EventUser>() {}.type
                val result: EventUser = gson.fromJson(body, resultType)

                return@withContext result
            }
        }

    // GET /event/{event_id}/generate_invite_link/
    // Response: InviteLinkResponse (returned as String)
    suspend fun generateInviteLink(eventId: String, token: String): String =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/generate_invite_link/"
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

                val resultType = object : TypeToken<UserEventInviteLink>() {}.type
                val result: UserEventInviteLink = gson.fromJson(body, resultType)

                return@withContext result.invite_link
            }
        }

    // GET /event/resolve_invite_link/{event_id}/
    // Response: EventId (returned as String)
    suspend fun resolveInviteLink(eventId: String, token: String): String =
        withContext(Dispatchers.IO) {
            val endpoint = "event/resolve_invite_link/$eventId/"
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

    // POST /event/{event_id}/add_user/{user_id}/
    // Response: TODO: JSON with a message which is useless to us
    suspend fun addUser(eventId: String, userId: String, token: String) =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/add_user/$userId/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            // Empty request body.
            val requestBody = gson.toJson("")
                .toRequestBody(HttpContentType.JSON.toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", HttpContentType.JSON.toString())
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)
            }
        }

    // GET /event/{event_id}/image_count/
    // Response: ImageCount (returned as Long)
    suspend fun imageCount(eventId: String, token: String): Long =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/image_count/"
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

    // PUT /event/{event_id}/user/{user_id}/accept/
    // Response: TODO: JSON with a message which is useless to us
    suspend fun acceptUser(eventId: String, userId: String, token: String) =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/user/$userId/accept/"
            val url = Api.url(endpoint)

            Log.d(TAG, "PUT: $url")

            // Empty request body.
            val requestBody = gson.toJson("")
                .toRequestBody(HttpContentType.JSON.toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)
            }
        }

    // DELETE /event/{event_id}/user/{user_id}/remove/
    // Response: Empty
    suspend fun removeUser(eventId: String, userId: String, token: String) =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/user/$userId/remove/"
            val url = Api.url(endpoint)

            Log.d(TAG, "DELETE: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .delete()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)
            }
        }

    // TODO: wtf is an EventDetailsResponse
    // GET /event/{event_id}/
    // Response: EventDetailsResponse
    suspend fun read(eventId: String, token: String): EventDetailsResponse =
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

                val resultType = object : TypeToken<EventDetailsResponse>() {}.type
                val result: EventDetailsResponse = gson.fromJson(body, resultType)

                return@withContext result
            }
        }
}

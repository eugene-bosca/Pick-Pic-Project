package com.bmexcs.pickpic.data.utils

import android.util.Log
import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.EventDetailsResponse
import com.bmexcs.pickpic.data.models.InviteLinkResponse
import com.bmexcs.pickpic.data.models.ListUserEventsResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

private const val TAG = "EventApiService"

class EventApiService {
    private val baseUrl = URL("https://pick-pic-service-627889116714.northamerica-northeast2.run.app")

    private val client = OkHttpClient()
    private val gson = Gson()

    // GET /list_users_events/{user_id}/
    // Response: ListUserEventsResponse
    suspend fun getListUsersEvents(userId: String, token: String): ListUserEventsResponse = withContext(Dispatchers.IO) {
        val endpoint = "list_users_events/$userId/"
        val url = URL("$baseUrl/$endpoint")

        Log.d(TAG, "GET: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            if (response.code == 404) {
                throw NotFoundException("Endpoint does not exist")
            }

            val body = response.body?.string()
                ?: throw HttpException(response.code, "Empty response body")

            val resultType = object : TypeToken<ListUserEventsResponse>() {}.type
            val result: ListUserEventsResponse = gson.fromJson(body, resultType)

            return@withContext result
        }
    }

    // POST /event/
    // Response: models.CreateEvent
    suspend fun post(eventCreation: CreateEvent, token: String): CreateEvent = withContext(Dispatchers.IO) {
        val endpoint = "event/"
        val url = URL("$baseUrl/$endpoint")

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
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            if (response.code == 404) {
                throw NotFoundException("Endpoint does not exist")
            }

            val body = response.body?.string()
                ?: throw HttpException(response.code, "Empty response body")

            val resultType = object : TypeToken<CreateEvent>() {}.type
            val result: CreateEvent = gson.fromJson(body, resultType)

            return@withContext result
        }
    }

    // GET /event/{event_id}/
    // Response: models.EventDetailsResponse
    suspend fun get(eventId: String, token: String): EventDetailsResponse = withContext(Dispatchers.IO) {
        val endpoint = "event/$eventId/"
        val url = URL("$baseUrl/$endpoint")

        Log.d(TAG, "GET: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            if (response.code == 404) {
                throw NotFoundException("Endpoint does not exist")
            }

            val body = response.body?.string()
                ?: throw HttpException(response.code, "Empty response body")

            val resultType = object : TypeToken<EventDetailsResponse>() {}.type
            val result: EventDetailsResponse = gson.fromJson(body, resultType)

            return@withContext result
        }
    }

    // GET /generate_invite_link/{event_id}/
    // Response: models.InviteLinkResponse
    suspend fun getGenerateInviteLink(eventId: String, token: String): InviteLinkResponse = withContext(Dispatchers.IO) {
        val endpoint = "generate_invite_link/$eventId/"
        val url = URL("$baseUrl/$endpoint")

        Log.d(TAG, "GET: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            if (response.code == 404) {
                throw NotFoundException("Endpoint does not exist")
            }

            val body = response.body?.string()
                ?: throw HttpException(response.code, "Empty response body")

            val resultType = object : TypeToken<InviteLinkResponse>() {}.type
            val result: InviteLinkResponse = gson.fromJson(body, resultType)

            return@withContext result
        }
    }

    // PUT /events/{event_id}/users/{user_id}/accept/
    // Response: Empty
    suspend fun putAcceptUser(eventId: String, userId: String, token: String) = withContext(Dispatchers.IO) {
        val endpoint = "events/$eventId/users/$userId/accept/"
        val url = URL("$baseUrl/$endpoint")

        Log.d(TAG, "PUT: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .put("".toRequestBody(HttpContentType.JSON.toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            if (response.code == 404) {
                throw NotFoundException("Endpoint does not exist")
            }
        }
    }

    // Delete /events/{event_id}/users/{user_id}/remove/
    // Response: Empty
    suspend fun deleteRemoveUser(eventId: String, userId: String, token: String) = withContext(Dispatchers.IO) {
        val endpoint = "events/$eventId/users/$userId/remove/"
        val url = URL("$baseUrl/$endpoint")

        Log.d(TAG, "DELETE: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code != 200) {
                Log.w(TAG, "Response code: ${response.code}")
            } else {
                Log.i(TAG, "Got response ${response.code}")
            }

            if (response.code == 404) {
                throw NotFoundException("Endpoint does not exist")
            }
        }
    }
}

package com.bmexcs.pickpic.data.utils

import android.util.Log
import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.EventDetailsResponse
import com.bmexcs.pickpic.data.models.InviteLinkResponse
import com.bmexcs.pickpic.data.models.ListUserEventsItem
import com.bmexcs.pickpic.data.models.ListUserEventsResponse
import com.bmexcs.pickpic.data.utils.apiServices.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

private const val TAG = "EventApiService"

class EventApiService {
    private val client = OkHttpClient()
    private val gson = Gson()

    // GET /list_users_events/{user_id}/
    // Response: ListUserEventsResponse
    suspend fun getListUsersEvents(userId: String, token: String): ListUserEventsResponse = withContext(Dispatchers.IO) {
        val endpoint = "list_users_events/$userId/"
        val url = buildUrl(endpoint)

        Log.d(TAG, "GET: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            handleResponseStatus(response)

            val body = response.body?.string()
                ?: throw HttpException(response.code, "Empty response body")

            val resultType = object : TypeToken<ListUserEventsResponse>() {}.type
            val result: ListUserEventsResponse = gson.fromJson(body, resultType)

            return@withContext result
        }
    }

    // GET /list_users_events/{user_id}/
    // Response: ListUserEventsResponse
    suspend fun getListPendingUsersEvents(userId: String, token: String):
            List<ListUserEventsItem> = withContext(Dispatchers.IO) {
        val endpoint = "/users/$userId/pending_events_full"

        Log.d(TAG, "GET: $endpoint")

        val request = Request.Builder()
            .url(buildUrl(endpoint))
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            handleResponseStatus(response)

            val body = response.body?.string()
                ?: throw HttpException(response.code, "Empty response body")

            try {
                // Create a Type for List<T>
                val listType: Type = TypeToken.getParameterized(List::class.java, ListUserEventsItem::class.java).type

                // Ensure the return type is explicitly List<T>
                val parsedResponse: List<ListUserEventsItem> = Gson().fromJson(body, listType)
                    ?: throw IllegalStateException("Failed to parse response")

                return@withContext parsedResponse
            } catch (e: Exception) {
                throw IllegalStateException("Error parsing response: ${e.message}", e)
            }
        }
    }

    // POST /event/
    // Response: models.CreateEvent
    suspend fun post(eventCreation: CreateEvent, token: String): CreateEvent = withContext(Dispatchers.IO) {
        val endpoint = "event/"
        val url = buildUrl(endpoint)

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
            handleResponseStatus(response)

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
        val url = buildUrl(endpoint)

        Log.d(TAG, "GET: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            handleResponseStatus(response)

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
        val url = buildUrl(endpoint)

        Log.d(TAG, "GET: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            handleResponseStatus(response)

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
        val url = buildUrl(endpoint)

        Log.d(TAG, "PUT: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .put("".toRequestBody(HttpContentType.JSON.toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            handleResponseStatus(response)
        }
    }

    // Delete /events/{event_id}/users/{user_id}/remove/
    // Response: Empty
    suspend fun deleteRemoveUser(eventId: String, userId: String, token: String) = withContext(Dispatchers.IO) {
        val endpoint = "events/$eventId/users/$userId/remove/"
        val url = buildUrl(endpoint)

        Log.d(TAG, "DELETE: $url")

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            handleResponseStatus(response)
        }
    }
}

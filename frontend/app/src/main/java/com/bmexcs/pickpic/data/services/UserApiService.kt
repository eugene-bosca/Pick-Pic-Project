package com.bmexcs.pickpic.data.services

import android.util.Log
import com.bmexcs.pickpic.data.dtos.Event
import com.bmexcs.pickpic.data.dtos.UserEventList
import com.bmexcs.pickpic.data.dtos.User
import com.bmexcs.pickpic.data.dtos.UserCreation
import com.bmexcs.pickpic.data.dtos.UserEmails
import com.bmexcs.pickpic.data.dtos.UserFirebaseId
import com.bmexcs.pickpic.data.dtos.UserIds
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.models.EventMetadataList
import com.bmexcs.pickpic.data.models.UserMetadata
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private const val TAG = "UserApiService"

class UserApiService {
    private val client = OkHttpClient()
    private val gson = Gson()

    /**
     * Creates a new user.
     *
     * **Endpoint**: `POST /user/`
     *
     * **Request Body**: `dtos.UserCreation`
     *
     * **Request Content-Type**: JSON
     *
     * **Response Type**: `dtos.User`
     *
     * **Return Type**: `models.UserMetadata`
     */
    suspend fun create(
        firebaseId: String,
        name: String,
        email: String,
        token: String): UserMetadata
    = withContext(Dispatchers.IO) {
        val endpoint = "user/"
        val url = Api.url(endpoint)

        Log.d(TAG, "POST: $url")

        val userCreation = UserCreation(
            firebase_id = firebaseId,
            display_name = name,
            email = email
        )

        val requestBody = gson.toJson(userCreation)
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            Api.handleResponseStatus(response)

            val body = response.body?.string()
                ?: throw HttpException(response.code, "Empty response body")

            val resultType = object : TypeToken<User>() {}.type
            val result: User = gson.fromJson(body, resultType)

            return@withContext UserMetadata(result)
        }
    }

    /**
     * Retrieves information about the specified user.
     *
     * **Endpoint**: `GET /user/{user_id}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `dtos.User`
     *
     * **Return Type**: `models.UserMetadata`
     */
    suspend fun get(userId: String, token: String): UserMetadata
        = withContext(Dispatchers.IO) {
            val endpoint = "user/$userId/"
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

                val resultType = object : TypeToken<User>() {}.type
                val result: User = gson.fromJson(body, resultType)

                return@withContext UserMetadata(result)
            }
        }

    /**
     * Updates information about the specified user.
     *
     * **Endpoint**: `PUT /user/{user_id}/`
     *
     * **Request Body**: `dtos.User`
     *
     * **Request Content-Type**: JSON
     *
     * **Response Type**: `dtos.User`
     *
     * **Return Type**: `models.UserMetadata`
     */
    suspend fun update(firebaseId: String, user: UserMetadata, token: String): UserMetadata
        = withContext(Dispatchers.IO) {
            val endpoint = "user/${user.id}/"
            val url = Api.url(endpoint)

            Log.d(TAG, "PUT: $url")

            val userUpdate = UserCreation(
                firebase_id = firebaseId,
                display_name = user.name,
                email = user.email,
                phone = user.phone,
                profile_picture = user.profilePicture
            )

            val requestBody = gson.toJson(userUpdate)
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<User>() {}.type
                val result: User = gson.fromJson(body, resultType)

                return@withContext UserMetadata(result)
            }
        }

    /**
     * Retrieves the list of events that the user owns and has joined.
     *
     * **Endpoint**: `GET /user/{user_id}/events/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `dtos.UserEventList`
     *
     * **Return Type**: `models.EventMetadataList`
     */
    suspend fun getEvents(userId: String, token: String): EventMetadataList =
        withContext(Dispatchers.IO) {
            val endpoint = "user/$userId/events/"
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

                val resultType = object : TypeToken<UserEventList>() {}.type
                val result: UserEventList = gson.fromJson(body, resultType)

                return@withContext EventMetadataList(result)
            }
        }

    /**
     * Deletes the specified event.
     *
     * **Endpoint**: `DELETE /user/{user_id}/events/{event_id}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: Empty
     *
     * **Return Type**: None
     */
    suspend fun deleteEvent(userId: String, eventId: String, token: String)
        = withContext(Dispatchers.IO) {
            val endpoint = "user/$userId/events/$eventId/"
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

    /**
     * Retrieves the list of pending event invitations.
     *
     * **Endpoint**: `GET /user/{user_id}/pending_event_invitations/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `List<dtos.EventInfo>`
     *
     * **Return Type**: `List<EventMetadata>`
     */
    suspend fun getPendingEvents(userId: String, token: String): List<EventMetadata> =
        withContext(Dispatchers.IO) {
            val endpoint = "user/$userId/pending_event_invitations/"
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

                val resultType = object : TypeToken<List<Event>>() {}.type
                val result: List<Event> = gson.fromJson(body, resultType)

                return@withContext result.map { EventMetadata(it) }
            }
        }

    /**
     * Retrieves the User ID given a list of email addresses.
     *
     * **Endpoint**: `POST /user/id/from_fire_base/`
     *
     * **Request Body**: `models.UserFirebaseIds` as `List<String>`
     *
     * **Request Content-Type**: None
     *
     * **Response**: `dtos.User`
     *
     * **Return Type**: `models.UserMetadata`
     */
    suspend fun userFromFirebaseId(firebaseId: String, token: String): UserMetadata =
        withContext(Dispatchers.IO) {
            val endpoint = "user/id/from_fire_base/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            val requestBody = gson.toJson(UserFirebaseId(firebaseId))
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<User>() {}.type
                val result: User = gson.fromJson(body, resultType)

                return@withContext UserMetadata(result)
            }
        }
}

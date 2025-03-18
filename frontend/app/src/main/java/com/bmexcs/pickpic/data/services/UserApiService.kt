package com.bmexcs.pickpic.data.services

import android.util.Log
import com.bmexcs.pickpic.data.models.Invitation
import com.bmexcs.pickpic.data.models.UserEventList
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.models.UserCreation
import com.bmexcs.pickpic.data.models.UserEmails
import com.bmexcs.pickpic.data.models.UserFirebaseId
import com.bmexcs.pickpic.data.models.UserIds
import com.bmexcs.pickpic.data.utils.Api
import com.bmexcs.pickpic.data.utils.HttpException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "UserApiService"

class UserApiService {
    private val client = OkHttpClient()
    private val gson = Gson()

    /**
     * Creates a new user.
     *
     * **Endpoint**: `POST /user/`
     *
     * **Request Body**: `models.UserCreation`
     *
     * **Request Content-Type**: JSON
     *
     * **Response Type**: `models.User`
     */
    suspend fun create(userCreation: UserCreation, token: String): User =
        withContext(Dispatchers.IO) {
            val endpoint = "user/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

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

                return@withContext result
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
     * **Response**: `models.User`
     */
    suspend fun get(userId: String, token: String): User = withContext(Dispatchers.IO) {
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

            return@withContext result
        }
    }

    /**
     * Updates information about the specified user.
     *
     * **Endpoint**: `PUT /user/{user_id}/`
     *
     * **Request Body**: `models.User`
     *
     * **Request Content-Type**: JSON
     *
     * **Response Type**: `models.User`
     */
    suspend fun update(user: User, token: String): User = withContext(Dispatchers.IO) {
        val endpoint = "user/${user.user_id}/"
        val url = Api.url(endpoint)

        Log.d(TAG, "PUT: $url")

        val userUpdate = UserCreation(
            firebase_id = user.firebase_id,
            display_name = user.display_name,
            email = user.email,
            phone = user.phone,
            profile_picture = user.profile_picture
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

            return@withContext result
        }
    }

    /**
     * Partially updates information about the specified user.
     *
     * **Endpoint**: `PATCH /user/{user_id}/`
     *
     * **Request Body**: `models.User`
     *
     * **Request Content-Type**: JSON
     *
     * **Response Type**: `models.User`
     */
    suspend fun updatePartial(user: User, token: String): User = withContext(Dispatchers.IO) {
        val endpoint = "user/${user.user_id}/"
        val url = Api.url(endpoint)

        Log.d(TAG, "PATCH: $url")

        val userPatch = UserCreation(
            firebase_id = user.firebase_id,
            display_name = user.display_name,
            email = user.email,
            phone = user.phone,
            profile_picture = user.profile_picture
        )

        val requestBody = gson.toJson(userPatch)
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .patch(requestBody)
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
     * Deletes the specified user.
     *
     * **Endpoint**: `DELETE /user/{user_id}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: Empty
     */
    suspend fun delete(userId: String, token: String) = withContext(Dispatchers.IO) {
        val endpoint = "user/$userId/"
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
     * Retrieves the list of events that the user owns or has joined.
     *
     * **Endpoint**: `GET /user/{user_id}/events/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `models.UserEventList`
     */
    suspend fun getEvents(userId: String, token: String): UserEventList =
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

                return@withContext result
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
     */
    suspend fun deleteEvent(userId: String, eventId: String, token: String) = withContext(Dispatchers.IO) {
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
     * **Endpoint**: `GET /user/{user_id}/pending_events_full/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `List<models.EventMember>`
     */
    suspend fun getPendingEvents(userId: String, token: String): List<Invitation> =
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

                val resultType = object : TypeToken<List<Invitation>>() {}.type
                val result: List<Invitation> = gson.fromJson(body, resultType)

                return@withContext result
            }
        }

    /**
     * Retrieves the User given an email address.
     *
     * **Endpoint**: `POST /user/id/from_email/`
     *
     * **Request Body**: `models.UserEmails` as `List<String>`
     *
     * **Request Content-Type**: JSON
     *
     * **Response**: `List<models.User>`
     */
    suspend fun usersFromEmails(emails: List<String>, token: String): List<String> =
        withContext(Dispatchers.IO) {
            val endpoint = "user/id/from_email/"
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

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                Log.d(TAG, body)

                val resultType = object : TypeToken<UserIds>() {}.type
                val result: UserIds = gson.fromJson(body, resultType)

                return@withContext result.users
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
     * **Response**: `models.User`
     */
    suspend fun userFromFirebaseId(firebaseId: String, token: String): User =
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

                Log.d(TAG, body)

                val resultType = object : TypeToken<User>() {}.type
                val result: User = gson.fromJson(body, resultType)

                return@withContext result
            }
        }

    suspend fun inviteUsersFromIds(userIds: List<String>, eventId: String, token: String) =
        withContext(Dispatchers.IO) {
            val endpoint = "event/${eventId}/invite/users/${"poggers"}"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            // Create a JSON body with the user_id

            val userIds: List<String> = userIds
            val jsonBody = JSONObject().apply {
                put("user_ids", JSONArray(userIds)) // Convert List<String> to JSONArray
            }.toString()
            Log.d(TAG, "JSON body: $jsonBody")

            val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()
            Log.d(TAG, "Request: $request")

            client.newCall(request).execute().use { response ->
                Log.d(TAG, "Response: $response")
                Api.handleResponseStatus(response)
            }
        }
}

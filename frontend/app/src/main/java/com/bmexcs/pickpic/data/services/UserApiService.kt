package com.bmexcs.pickpic.data.services

import android.util.Log
import com.bmexcs.pickpic.data.models.Email
import com.bmexcs.pickpic.data.models.EventMember
import com.bmexcs.pickpic.data.models.UserEventList
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.models.UserCreation
import com.bmexcs.pickpic.data.models.UserId
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
    suspend fun getPendingEvents(userId: String, token: String): List<EventMember> =
        withContext(Dispatchers.IO) {
            val endpoint = "user/$userId/pending_events_full/"
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

                val resultType = object : TypeToken<List<EventMember>>() {}.type
                val result: List<EventMember> = gson.fromJson(body, resultType)

                return@withContext result
            }
        }

    /**
     * Retrieves the User ID given an email address.
     *
     * **Endpoint**: `GET /user/from_email/{email}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `models.UserId` as `String
     */
    suspend fun userIdFromEmail(email: String, token: String): UserId =
        withContext(Dispatchers.IO) {
            val endpoint = "user/from_email/$email/"
            val url = Api.url(endpoint)

            Log.d(TAG, "GET: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                Api.handleResponseStatus(response)

                val body = response.body?.string()
                    ?: throw HttpException(response.code, "Empty response body")

                val resultType = object : TypeToken<UserId>() {}.type
                val result: UserId = gson.fromJson(body, resultType)

                return@withContext result
            }
        }

    /**
     * Retrieves the User ID given an email address.
     *
     * **Endpoint**: `GET /user/from_fire_base/{firebase_id}/`
     *
     * **Request Body**: Empty
     *
     * **Request Content-Type**: None
     *
     * **Response**: `models.UserId` as `String`
     */
    suspend fun userIdFromFirebase(firebaseId: String, token: String): String =
        withContext(Dispatchers.IO) {
            val endpoint = "user/from_fire_base/$firebaseId/"
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

                val resultType = object : TypeToken<UserId>() {}.type
                val result: UserId = gson.fromJson(body, resultType)

                return@withContext result.user_id
            }
        }

    suspend fun inviteUsersFromEmail(userIds: List<UserId>, eventId: String, token: String): String =
        withContext(Dispatchers.IO) {
            val endpoint = "event/$eventId/invite/users/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            val requestBody = gson.toJson(userIds)
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

                val resultType = object : TypeToken<String>() {}.type
                val result: String = gson.fromJson(body, resultType)

                return@withContext result
            }
        }
}

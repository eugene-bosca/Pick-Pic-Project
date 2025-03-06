package com.bmexcs.pickpic.data.services

import android.util.Log
import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.EventUser
import com.bmexcs.pickpic.data.models.UserEventList
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.models.UserCreation
import com.bmexcs.pickpic.data.models.UserId
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

private const val TAG = "UserApiService"

class UserApiService {
    private val client = OkHttpClient()
    private val gson = Gson()

    // GET /user/{user_id}/pending_events_full/
    // Response: List<Event>
    suspend fun eventsPending(userId: String, token: String): List<EventUser> =
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

                val resultType = object : TypeToken<List<EventUser>>() {}.type
                val result: List<EventUser> = gson.fromJson(body, resultType)

                return@withContext result
            }
        }

    // GET /user/{user_id}/events/
    // Response: UserEventList
    suspend fun events(userId: String, token: String): UserEventList =
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

    // GET /user/from_fire_base/{firebase_id}/
    // Response: UserId (returned as String)
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

    // GET /user/from_email/{email}/
    // Response: UserId (returned as String)
    suspend fun userIdFromEmail(email: String, token: String): String =
        withContext(Dispatchers.IO) {
            val endpoint = "user/from_email/$email/"
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

    // GET /user/{user_id}/
    // Response: User
    suspend fun read(userId: String, token: String): User = withContext(Dispatchers.IO) {
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

    // POST /user/
    // Response: User
    suspend fun create(userCreation: UserCreation, token: String): User =
        withContext(Dispatchers.IO) {
            val endpoint = "user/"
            val url = Api.url(endpoint)

            Log.d(TAG, "POST: $url")

            val requestBody = gson.toJson(userCreation)
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

                val resultType = object : TypeToken<User>() {}.type
                val result: User = gson.fromJson(body, resultType)

                return@withContext result
            }
        }

    // PUT /user/{user_id}/
    // Response: User
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
            .toRequestBody(HttpContentType.JSON.toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", HttpContentType.JSON.toString())
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

    // PATCH /user/{user_id}/
    // Response: User
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
            .toRequestBody(HttpContentType.JSON.toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", HttpContentType.JSON.toString())
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

    // DELETE /user/{user_id}/
    // Response: Empty
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
}

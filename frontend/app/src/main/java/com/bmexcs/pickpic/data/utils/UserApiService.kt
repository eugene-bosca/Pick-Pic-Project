package com.bmexcs.pickpic.data.utils

import android.util.Log
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.models.UserCreation
import com.bmexcs.pickpic.data.models.UserId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

private const val TAG = "UserApiService"

class UserApiService {
    private val baseUrl = URL("https://pick-pic-service-627889116714.northamerica-northeast2.run.app")
    private val endpointGroup = "users"

    private val client = OkHttpClient()
    private val gson = Gson()

    // GET /get_user_id_by_firebase_id/{firebase_id}/
    // Response: String (User UUID)
    suspend fun getUserIdByFirebaseId(firebaseId: String, token: String): String = withContext(Dispatchers.IO) {
        val endpoint = "get_user_id_by_firebase_id/$firebaseId/"
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

            val resultType = object : TypeToken<UserId>() {}.type
            val result: UserId = gson.fromJson(body, resultType)

            return@withContext result.user_id
        }
    }

    // GET /get_user_id_from_email/{email}/
    // Response: String (User UUID)
    suspend fun getUserIdByEmail(email: String, token: String): String = withContext(Dispatchers.IO) {
        val endpoint = "get_user_id_from_email/$email/"
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

            val resultType = object : TypeToken<UserId>() {}.type
            val result: UserId = gson.fromJson(body, resultType)

            return@withContext result.user_id
        }
    }

    // POST /users/
    // Response: models.User
    suspend fun post(userCreation: UserCreation, token: String): User = withContext(Dispatchers.IO) {
        val endpoint = "$endpointGroup/"
        val url = URL("$baseUrl/$endpoint")

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

            val resultType = object : TypeToken<User>() {}.type
            val result: User = gson.fromJson(body, resultType)

            return@withContext result
        }
    }

    // GET /users/{user_id}/
    // Response: models.User
    suspend fun get(userId: String, token: String): User = withContext(Dispatchers.IO) {
        val endpoint = "$endpointGroup/$userId/"
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

            val resultType = object : TypeToken<User>() {}.type
            val result: User = gson.fromJson(body, resultType)

            return@withContext result
        }
    }

    // PUT /users/{user_id}/
    // Response: models.User
    suspend fun put(user: User, token: String): User = withContext(Dispatchers.IO) {
        val endpoint = "$endpointGroup/${user.user_id}/"
        val url = URL("$baseUrl/$endpoint")

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

            val resultType = object : TypeToken<User>() {}.type
            val result: User = gson.fromJson(body, resultType)

            return@withContext result
        }
    }

    // PATCH /users/{user_id}/
    // Response: models.User
    suspend fun patch(user: User, token: String): User = withContext(Dispatchers.IO) {
        val endpoint = "$endpointGroup/${user.user_id}/"
        val url = URL("$baseUrl/$endpoint")

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

            val resultType = object : TypeToken<User>() {}.type
            val result: User = gson.fromJson(body, resultType)

            return@withContext result
        }
    }

    // DELETE /users/{user_id}/
    // Response: Empty
    suspend fun delete(userId: String, token: String) = withContext(Dispatchers.IO) {
        val endpoint = "$endpointGroup/$userId/"
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

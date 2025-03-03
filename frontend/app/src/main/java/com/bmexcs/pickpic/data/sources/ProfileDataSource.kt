package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import javax.inject.Inject
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileDataSource @Inject constructor(
    authDataSource: AuthDataSource
) {
    private val client = OkHttpClient()

    suspend fun getProfile(userId: String): User {
        val request = Request.Builder()
            .url("https://localhost:8080/users/$userId")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (!response.isSuccessful || responseBody == null) {
                    throw Exception("Failed to fetch profile")
                }
                Json.decodeFromString(User.serializer(), responseBody)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            User()
        }
    }

    suspend fun saveProfile(userId: String, user: User) {
        Log.d("ProfileDataSource", "Profile saving")
        val jsonBody = Json.encodeToString(mapOf(
            "display_name" to user.displayName,
            "email" to user.email,
            "phone" to user.phone,
            "profile_picture" to user.profilePictureId.toString()
        ))

        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://pick-pic-service-627889116714.northamerica-northeast2.run.app/api/swagger/$userId")
            .patch(requestBody)
            .build()

        try {
            // Use withContext to switch to a background thread for the network operation
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (!response.isSuccessful) {
                Log.e("ProfileDataSource", "Profile failed to save")
                throw Exception("Failed to save profile")
            }

            Log.d("ProfileDataSource", "Profile saved successfully")
        } catch (e: Exception) {
            Log.e("ProfileDataSource", "Unknown error", e)
        }
    }
}

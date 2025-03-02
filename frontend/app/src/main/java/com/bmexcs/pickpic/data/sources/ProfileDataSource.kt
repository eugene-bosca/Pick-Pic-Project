package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.Profile
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

    suspend fun getProfile(userId: String): Profile {
        val request = Request.Builder()
            .url("https://localhost:8080/users/$userId")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (!response.isSuccessful || responseBody == null) {
                    throw Exception("Failed to fetch profile")
                }
                Json.decodeFromString(Profile.serializer(), responseBody)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Profile()
        }
    }

    suspend fun saveProfile(userId: String, profile: Profile) {
        Log.d("ProfileDataSource", "Profile saving")
        val jsonBody = Json.encodeToString(mapOf(
            "display_name" to profile.displayName,
            "email" to profile.email,
            "phone" to profile.phone,
            "profile_picture" to profile.profilePictureId.toString()
        ))

        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://localhost:8080/users/$userId")
            .patch(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("ProfileDataSource", "Profile failed to save")

                    throw Exception("Failed to save profile")
                }
                if (response.isSuccessful) {
                    Log.d("ProfileDataSource", "Profile saved successfully")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

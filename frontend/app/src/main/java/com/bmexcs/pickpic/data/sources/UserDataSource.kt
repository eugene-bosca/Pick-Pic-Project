package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.utils.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import javax.inject.Inject
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private const val TAG = "UserDataSource"

class UserDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    private val client = OkHttpClient()

    suspend fun getUser(userId: String): User {
        Log.d(TAG, "Get user state")

        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val user = ApiService.fetch("users/$userId", User::class.java, token)
        return user
    }

    suspend fun updateUser(user: User) {
        Log.d(TAG, "Updating user state")

        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        // TODO: change response object type
        val result = ApiService.patch("users/${user.id}", user, User::class.java, token)
    }
}

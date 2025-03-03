package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.utils.ApiService
import javax.inject.Inject

private const val TAG = "UserDataSource"

class UserDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
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
        val result = ApiService.patch("users/${user.user_id}", user, User::class.java, token)
    }
}

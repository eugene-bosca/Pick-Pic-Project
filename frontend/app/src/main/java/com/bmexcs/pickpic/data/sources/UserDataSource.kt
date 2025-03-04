package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.models.UserCreation
import com.bmexcs.pickpic.data.serializable.SerializableUUID
import com.bmexcs.pickpic.data.utils.ApiService
import com.bmexcs.pickpic.data.utils.NotFoundException
import java.util.UUID
import javax.inject.Inject

private const val TAG = "UserDataSource"

class UserDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun createUser(): User {
        val userCreation = UserCreation(
            firebase_id = authDataSource.getCurrentUser().uid,
            display_name = authDataSource.getCurrentUser().displayName ?: "",
            email = authDataSource.getCurrentUser().email ?: ""
        )

        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        try {
            return ApiService.post("users/", userCreation, User::class.java, token)
        } catch (e: Exception) {
            Log.e(TAG, "$e")
            return User()
        }
    }

    suspend fun getUser(userId: String): User? {
        Log.d(TAG, "Get user state")

        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        return try {
            ApiService.fetch("users/$userId", User::class.java, token)
        } catch (e: NotFoundException) {
            null
        }
    }

    suspend fun updateUser(user: User) {
        Log.d(TAG, "Updating user state")

        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        // TODO: change response object type
        val result = ApiService.patch("users/${user.user_id}", user, User::class.java, token)
    }
}

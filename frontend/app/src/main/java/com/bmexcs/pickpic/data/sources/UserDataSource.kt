package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.models.UserCreation
import com.bmexcs.pickpic.data.utils.UserApiService
import com.bmexcs.pickpic.data.utils.NotFoundException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UserDataSource"

@Singleton
class UserDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    private var cachedUser: User? = null

    private val api = UserApiService()

    fun getUser(): User {
        return cachedUser ?: throw Exception("Null user")
    }

    suspend fun initUserWithFirebase() {
        val firebaseId = authDataSource.getCurrentUser().uid
        val token = authDataSource.getIdToken() ?: throw Exception("Invalid Firebase ID token")

        Log.d(TAG, "Initialize user state with firebaseId=$firebaseId")

        cachedUser = try {
            val userId = api.getUserIdByFirebaseId(firebaseId, token)
            val user = api.get(userId, token)
            user
        } catch (e: NotFoundException) {
            createUser()
        }
    }

    private suspend fun createUser(): User {
        val userCreation = UserCreation(
            firebase_id = authDataSource.getCurrentUser().uid,
            display_name = authDataSource.getCurrentUser().displayName ?: throw Exception("Empty display name"),
            email = authDataSource.getCurrentUser().email ?: throw Exception("Empty email field")
        )
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "createUser with firebaseID=${authDataSource.getCurrentUser().uid}")

        return try {
            val newUser = api.post(userCreation, token)
            newUser
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create new user: $e")
            User()
        }
    }

    suspend fun updateUser(user: User) {
        Log.d(TAG, "Updating user state")

        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        cachedUser = api.put(user, token)
    }
}
